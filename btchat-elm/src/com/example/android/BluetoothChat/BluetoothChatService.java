/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BluetoothChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {
	// Debugging
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";
	private static final String NAME_INSECURE = "BluetoothChatInsecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE =
			UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID MY_UUID_INSECURE =
			UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mSecureAcceptThread;
	private AcceptThread mInsecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	private double MAFval = 0.000001;
	private double VSS = 0.000001;
	
	private ConcurrentHashMap<String, String> mapValues;
	
	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * @param context  The UI Activity Context
	 * @param handler  A Handler to send messages back to the UI Activity
	 */
	public BluetoothChatService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
		mapValues = new ConcurrentHashMap<String, String>();
	}

	/**
	 * Set the current state of the chat connection
	 * @param state  An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Return the current connection state. */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume() */
	public synchronized void start() {
		if (D) Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		setState(STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mSecureAcceptThread == null) {
			mSecureAcceptThread = new AcceptThread(true);
			mSecureAcceptThread.start();
		}
		if (mInsecureAcceptThread == null) {
			mInsecureAcceptThread = new AcceptThread(false);
			mInsecureAcceptThread.start();
		}
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * @param device  The BluetoothDevice to connect
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		if (D) Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, secure);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * @param socket  The BluetoothSocket on which the connection was made
	 * @param device  The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice
			device, final String socketType) {
		if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Cancel the accept thread because we only want to connect to one device
		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}
		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.cancel();
			mInsecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket, socketType);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D) Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.cancel();
			mInsecureAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED) return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted
	 * (or until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;
		private String mSocketType;

		public AcceptThread(boolean secure) {
			BluetoothServerSocket tmp = null;
			mSocketType = secure ? "Secure":"Insecure";

			// Create a new listening server socket
			try {
				if (secure) {
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
							MY_UUID_SECURE);
				} else {
					tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
							NAME_INSECURE, MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D) Log.d(TAG, "Socket Type: " + mSocketType +
					"BEGIN mAcceptThread" + this);
			setName("AcceptThread" + mSocketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice(),
									mSocketType);
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

		}

		public void cancel() {
			if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
			}
		}
	}


	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device.createRfcommSocketToServiceRecord(
							MY_UUID_SECURE);
				} else {
					Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
					tmp = (BluetoothSocket) m.invoke(device, 1);
					//tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
			} catch (NoSuchMethodException e) {
				Log.e(TAG, "NoSuchMethodException: " + e);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "IllegalArgumentException: " + e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "IllegalAccessException: " + e);
			} catch (InvocationTargetException e) {
				Log.e(TAG, "InvocationTargetException: " + e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				// Close the socket
				Log.e(TAG, "exception at " + e);
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() " + mSocketType +
							" socket during connection failure", e2);
				}
				connectionFailed();
				return;

			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice, mSocketType);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private final BufferedReader mmInStreamBuf;

		public ConnectedThread(BluetoothSocket socket, String socketType) {
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			mmInStreamBuf = new BufferedReader(new InputStreamReader(tmpIn));
		}

		private double calculateMPG() {
			double mpg = (710.7 * VSS) / MAFval;
			return mpg;
		}
		
		private void postData() {
			AsyncTask.execute(new Runnable() {
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();
						String getURL = "http://omnidrive.herokuapp.com/data?fbid=1566713904&mpg=" +  calculateMPG();
						for(String out: mapValues.keySet()) {
							getURL += "&" + out + "=" + mapValues.get(out);
						}
						System.out.println(getURL);
						mapValues.clear();
						HttpGet get = new HttpGet(getURL);
						HttpResponse responseGet = client.execute(get);
						System.out.println("Success!!!!!");
						/*HttpEntity responseEntity = responseGet.getEntity();
                	String response = EntityUtils.toString(responseEntity);
                	JSONObject jObject = new JSONObject(response);*/
					} catch (Exception e) {
						Log.e(TAG, "FAILURE");
					}
				}
			});
		}



		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes = 0;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					String ln = mmInStreamBuf.readLine();

					if(ln.length() > 6) {
						try {
							String header = ln.substring(0, 5);
							String rest = ln.substring(6).replace(" ", "");
							long value = new BigInteger(rest, 16).longValue();

							if(header.equals("41 0C")) {
								ln = "Engine RPM: " + (value / 4) + " rpm";
								mapValues.put("rpm", Long.toString(value/4));
							} else if(header.equals("41 0D")) {
								ln = "Speed: " + value + " kph";
								mapValues.put("speed", Long.toString(value));
								VSS = value;
							} else if(header.equals("41 0F")) {
								ln = "Intake air temperature: " + (value-40) + " deg C";
								mapValues.put("intakeTemp", Long.toString(value-40));
							} else if(header.equals("41 10")) {
								ln = "MAF: " + (value) + "hundreds of g/s"; 
								MAFval = value;
								mapValues.put("MAF", Long.toString(value));
							} else if(header.equals("41 1F")) {
								ln = "Runtime seconds: " + (value) + " seconds";
								mapValues.put("runSec", Long.toString(value));
							} else if(header.equals("41 30")) {
								ln = "Number of warmups: " + value;
								mapValues.put("numWarmup", Long.toString(value));
							} else if(header.equals("41 33")) {
								ln = "Barometer: " + value + " kPa";
								mapValues.put("baro", Long.toString(value));
							} else if(header.equals("41 46")) {
								ln = "Ambient Temp: " + (value - 40) + " deg C";
								mapValues.put("ambTemp", Long.toString(value-40));
							} else if(header.equals("41 49")) {
								ln = "Throttle: " + (value * 100 / 255) + " %";
								mapValues.put("throttle", Long.toString(value * 100 / 255));
								postData();
							}
						} catch (NumberFormatException ignored) { }
					}

					buffer = ln.getBytes();
					bytes = buffer.length;
					//System.out.println(ln);
					//bytes = mmInStream.read(buffer);
					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					// Start the service over to restart listening mode
					BluetoothChatService.this.start();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * @param buffer  The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				Log.i(TAG, "writing byte array of length " + buffer.length);

				byte[] actualBuffer = new byte[buffer.length + 1];
				for(int i = 0; i < buffer.length; i++)
					actualBuffer[i] = buffer[i];

				actualBuffer[buffer.length] = 0x0d;

				mmOutStream.write(actualBuffer);
				mmOutStream.flush();

				// Share the sent message back to the UI Activity
				mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
				.sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}
