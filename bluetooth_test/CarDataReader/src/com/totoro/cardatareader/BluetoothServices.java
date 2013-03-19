package com.totoro.cardatareader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//RRS232 interpreter     ELM327 v1.5  prot 
// 1850-PWM
// odb standard odb-ii     prot:iso9141-2 5 baud 
//calib id 62003001

public class BluetoothServices {
	
	private static final String TAG = "BluetoothServices";
	private static final Boolean D = true;
	
	private final BluetoothAdapter mAdapter;
   	private final Handler mHandler;
	private int mState;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private AcceptThread mAcceptThread;
	
	private final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private final UUID DEVICE_UUID_2 = UUID.fromString("00001200-0000-1000-8000-00805f9b34fb");
	private final String NAME = "BluetoothServices";
	
	public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connection
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	
	public BluetoothServices(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
	}
	
	private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        mHandler.obtainMessage(Dashboard.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
	
	public synchronized int getState() {
        return mState;
    }
	
	public synchronized void start() {
        if (D) Log.d(TAG, "start");

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);
        
        if (mAcceptThread == null) {
        	mAcceptThread = new AcceptThread();
        	mAcceptThread.start();
        }
    }
	
	public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
	
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }
	
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

        setState(STATE_NONE);
    }
	
	private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Dashboard.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Dashboard.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothServices.this.start();
    }
	
	private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Dashboard.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Dashboard.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothServices.this.start();
    }
	
	private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
            	UUID uuid = device.getUuids()[0].getUuid();
                UUID uuid2 = device.getUuids()[1].getUuid();
                Log.i(TAG, "UUID: " + uuid + " " + uuid2 + " Size: " + device.getUuids().length);
                tmp = device.createRfcommSocketToServiceRecord(uuid);
                
				/*try {
					Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					tmp = (BluetoothSocket) m.invoke(device, 1);
				} catch (NoSuchMethodException e) {
					Log.e(TAG, "create() failed 1", e);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "create() failed 2", e);
				} catch (IllegalAccessException e) {
					Log.e(TAG, "create() failed 3", e);
				} catch (InvocationTargetException e) {
					Log.e(TAG, "create() failed 4", e);
				}*/
                
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                    Log.i(TAG, "CONNECT mConnectThread FAILED");
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return; 
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothServices.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }
    }
	
	private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Dashboard.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothServices.this.start();
                    break;
                }
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
	
	private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
            	tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, DEVICE_UUID);              	
   				/*try {
					Method m = BluetoothAdapter.getDefaultAdapter().getClass().getMethod("listenUsingRfcommOn", new Class[] { int.class });
					tmp = (BluetoothServerSocket) m.invoke(BluetoothAdapter.getDefaultAdapter(), 1);
				} catch (NoSuchMethodException e) {
					Log.e(TAG, "create() failed 1", e);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "create() failed 2", e);
				} catch (IllegalAccessException e) {
					Log.e(TAG, "create() failed 3", e);
				} catch (InvocationTargetException e) {
					Log.e(TAG, "create() failed 4", e);
				} */
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    Log.i(TAG, "SERVER ACCEPT PLEASE");
                    socket = mmServerSocket.accept();
                    Log.i(TAG, "SERVER ACCEPT DONE");
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                
                Log.i(TAG, "SERVER ACCEPT YAYYY");

                if (socket != null) {
                    synchronized (BluetoothServices.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
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
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "ACCEPT THREAD cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }
}
