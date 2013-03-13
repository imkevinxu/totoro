package com.totoro.cardatareader;

import java.util.ArrayList;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.totoro.cardatareader.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Dashboard extends Activity {
	
	private static final String TAG = "Dashboard";
    private static final boolean D = true;
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_TOAST = 3;
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    private static final String URL_ENDPOINT = "http://omnidrive.herokuapp.com/load";
    
    private BluetoothServices mBluetoothServices = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private static final int REQUEST_CONNECT_DEVICE = 0;
	private static final int REQUEST_ENABLE_BT = 1;
	
	private static String BLUETOOTH_ADDRESS = "device_address";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		if(D) Log.e(TAG, "++ ON START ++");
		
		if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mBluetoothServices == null) setupBluetooth();
        }
		
		IntentConnectDevice(REQUEST_CONNECT_DEVICE);
   	}
	
	@Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        if (mBluetoothServices != null) {
            if (mBluetoothServices.getState() == BluetoothServices.STATE_NONE) {
            	mBluetoothServices.start();
            }
        }
    }
	
	private void setupBluetooth() {
        Log.d(TAG, "setupBluetooth()");
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		ArrayList<String> mArrayList = new ArrayList<String>();
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        mArrayList.add(device.getName() + "\n" + device.getAddress());
		        BLUETOOTH_ADDRESS = device.getAddress();
		    }
		}
		
		ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
		ListView lv = (ListView)findViewById(R.id.listView1);
		lv.setAdapter(mArrayAdapter);
		
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBluetoothServices = new BluetoothServices(this, mHandler);
    }
	
	
    // The Handler that gets information back from the BluetoothServices
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothServices.STATE_CONNECTED:
                    break;
                case BluetoothServices.STATE_CONNECTING:
                    break;
                case BluetoothServices.STATE_LISTEN:
                case BluetoothServices.STATE_NONE:
                    break;
                }
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
                
                if(D) Log.e(TAG, "READ: " + readMessage);
                EditText et = (EditText)findViewById(R.id.editText1);
                et.setText(readMessage);
                sendData(readMessage);
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
	
	private void sendData(String data)
	{
		Log.d(TAG, "sendData " + data);
	     // 1) Connect via HTTP. 2) Encode data. 3) Send data.
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("data", data));
	    try
	    {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(URL_ENDPOINT);
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        Log.i("postData", response.getStatusLine().toString());
	            //Could do something better with response.
	    }
	    catch(Exception e)
	    {
	        Log.e("log_tag", "Error:  "+e.toString());
	    }  
	}
	
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBluetoothServices != null) mBluetoothServices.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    protected void IntentConnectDevice(int requestCode) {
		if(D) Log.d(TAG, "IntentConnectDevice " + requestCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	if (BLUETOOTH_ADDRESS != "device_address") {
            	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(BLUETOOTH_ADDRESS);
               mBluetoothServices.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (BLUETOOTH_ADDRESS != "device_address") {
                // Bluetooth is now enabled, so set up a chat session
                setupBluetooth();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                finish();
            }
        }
	}
    
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	if (BLUETOOTH_ADDRESS != "device_address") {
            	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(BLUETOOTH_ADDRESS);
               mBluetoothServices.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupBluetooth();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                finish();
            }
        }
	}*/
}
