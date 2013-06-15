package com.authorwjf.bounce;

import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class Main extends Activity {
	public static String fbid = null;
	String TAG = "BluetoothChatService";
	public static final int MESSAGE_READ = 2;

	Activity act;

	public static String macAddress = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChatService mChatService = null;

	private int counter = 0;
	private ConcurrentHashMap<String, String> mapValues;
	private double MAFval = 0.000001;
	private double VSS = 0.000001;

	private static ELMTuple[] englishFields = new ELMTuple[]{
		new ELMTuple("rpm", "010C"),
		new ELMTuple("speed", "010D"),
		new ELMTuple("intakeAir", "010F"),
		new ELMTuple("MAF", "0110"),
		new ELMTuple("Runtime in seconds", "011F"),
		new ELMTuple("Number of warmups", "0130"),
		new ELMTuple("Barometer", "0133"),
		new ELMTuple("Ambient temperature", "0146"),
		new ELMTuple("Throttle", "0149"),
	};

	static class ELMTuple	{
		public String englishName;
		public String elmName;
		public ELMTuple(String englishName, String elmName) {
			super();
			this.englishName = englishName;
			this.elmName = elmName;
		}
	}

	private double calculateMPG() {
		double mpg = (710.7 * VSS) / MAFval;
		return mpg;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				Log.e("Handler", "Message: "+ readMessage);

				break;
			}
		}
	};

	private void sendMessage(String message) {

		// Check that there's actually something to send
		if (message != null && message.length() > 0) {
			// PARSER
			String lmsg = message.toLowerCase();
			if(lmsg.equals("rpm")) {
				message = "010C";
			} else if(lmsg.equals("speed")) {
				message = "010D";
			} else if(lmsg.equals("baro")) {
				message = "0133";
			} else if(lmsg.equals("ambient")) {
				message = "0146";
			} else if(lmsg.equals("throttle")) {
				message = "0149";
			} else if(lmsg.equals("maf")) {
				message = "0110";
			}
		}
		// Get the message bytes and tell the BluetoothChatService to write
		byte[] send = message.getBytes();
		mChatService.write(send);

		// Reset out string buffer to zero and clear the edit text field
		//mOutStringBuffer.setLength(0);
		//mOutEditText.setText(mOutStringBuffer);
	}


	private void loopQuery() {
		AsyncTask.execute(new Runnable() {
			public void run() {
				while(true)	{
					long lastTime = System.currentTimeMillis();
					for(ELMTuple tuple: englishFields){
						sendMessage(tuple.elmName);
						try {
							Thread.sleep(800);
						}
						catch(Exception ignored) {
							Log.e("LOOP", "ELM SEND EXCEPTION");
						}
					}
					long query = 1000 - (System.currentTimeMillis() - lastTime);
					if(query > 0)	{
						try	{
							Thread.sleep(query);
						}
						catch(Exception ignored) {
							Log.e("LOOP", "SLEEP FAILED");
						}
					}
				}
			}
		});
	}
	@Override
	public synchronized void onResume() {
		super.onResume();
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				// mChatService.start();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.summary);

		act = this;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mChatService = new BluetoothChatService(this, mHandler);
		mapValues = new ConcurrentHashMap<String, String>();

		if (fbid == null) {
			setContentView(R.layout.login);     
		}

		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								// need to change the mac address to the proper address of the OBDII device a specific user is using 
								macAddress = "00:0D:18:28:04:F4";
								//macAddress = "00:0D:18:00:7A:BE";
								fbid = user.getId();  

								// Get the device MAC address
								String address = macAddress;
								// Get the BluetoothDevice object
								BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
								if(device == null)	{
									throw new RuntimeException("This thing is null");
								}
								// Attempt to connect to the device
								mChatService.connect(device, false);
								try	{
									Thread.sleep(500);
								} catch(Exception ignored) {
								}
								//mChatService.start();
								loopQuery();
								setContentView(R.layout.main);
							}
						}
					});
				} 
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
}
