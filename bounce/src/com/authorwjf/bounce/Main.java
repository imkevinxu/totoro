package com.authorwjf.bounce;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.authorwjf.bounce.AutomaticQueryThread.ELMTuple;
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

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//System.out.println("HEEELLLLLLOOOOO");
			case MESSAGE_READ:
				System.out.println("HELLLO");
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
				Log.e(TAG, "LOOPING");
				while(true)	{
					Log.e("LOOP", "LET US QUERY!");
					long lastTime = System.currentTimeMillis();
					for(ELMTuple tuple: englishFields){
						sendMessage(tuple.elmName);
						try {
							Thread.sleep(800);
						}
						catch(Exception ignored) {}
					}
					long query = 1000 - (System.currentTimeMillis() - lastTime);
					if(query > 0)	{
						try	{
							Thread.sleep(query);
						}
						catch(Exception ignored) {}
					}
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mChatService = new BluetoothChatService(this, mHandler);

		if (fbid == null) {
			setContentView(R.layout.login);     
		}
		//setContentView(R.layout.main);
		//Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.android.BluetoothChat");
		//startActivity(launchIntent);
		//   
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
								macAddress = "14:10:9F:D2:6F:3E";

								// Get the device MAC address
								String address = macAddress;
								// Get the BluetoothDevice object
								BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
								if(device == null)	{
									throw new RuntimeException("This thing is null");
								}
								// Attempt to connect to the device
								Log.e(TAG, "ABOUT TO CONNECT");

								mChatService.connect(device, false);
								Log.e(TAG, "POST-CONNECTION");

								loopQuery();
								//setContentView(R.layout.main);

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
