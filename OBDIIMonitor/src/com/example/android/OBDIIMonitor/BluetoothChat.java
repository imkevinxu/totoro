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

package com.example.android.OBDIIMonitor;

import com.example.android.OBDIIMonitor.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;


/**
 * This is the main Activity that displays the current chat session.
 * @param <ImageView>
 */
@SuppressLint("HandlerLeak")
public class BluetoothChat<ImageView> extends Activity {
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
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

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }
    
    //keep track of current PID number
    int message_number = 1;
    
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        //mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        /*mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message + '\r');
            }
        });*/

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        
        //---Get Vehicle Data Button---
        final ToggleButton getDataButton = (ToggleButton) findViewById(R.id.toggleButton1);
        getDataButton.setOnClickListener(new View.OnClickListener() 
        {
        	
            public void onClick(View v) {
 
            	if(getDataButton.isChecked()) {
            		startTransmission();
            	}
            	else {
            		message_number = 0;
            	}

            }
        });
        
        
        //---Clear Trouble Codes Button---
        Button getCodesButton = (Button) findViewById(R.id.button2);
        getCodesButton.setOnClickListener(new View.OnClickListener() 
        {
    	
            public void onClick(View v) {
 
            	clearCodes();
            }
        });   
       
    }
    
    public void startTransmission() {
    	
    	sendMessage("01 00" + '\r'); 
    
    }
    

	public void getData(int messagenumber) {
	
		final TextView TX = (TextView) findViewById(R.id.TXView2); 
		
		switch(messagenumber) {
    	
        	case 1:
        		sendMessage("01 0C" + '\r'); //get RPM
        		TX.setText("01 0C");
        		messagenumber++;
        		break;
        		
        	case 2:
        		sendMessage("01 0D" + '\r'); //get MPH
        		TX.setText("01 0D");
        		messagenumber++;
        		break;
        	case 3:
        		sendMessage("01 04" + '\r'); //get Engine Load
        		TX.setText("01 04");
        		messagenumber++;
        		break;
        	case 4:
        		sendMessage("01 05" + '\r'); //get Coolant Temperature
        		TX.setText("01 05");
        		messagenumber++;
        		break;
        	case 5:
        		sendMessage("01 0F" + '\r'); //get Intake Temperature
        		TX.setText("01 0F");
        		messagenumber++;
        		break;
        	
        	case 6:
        		sendMessage("AT RV" + '\r'); //get Voltage
        		TX.setText("AT RV");
        		messagenumber++;
        		break;
        		
        	default: ; 		 
		}
    }
    

    public void clearCodes() {
    	
    	final TextView TX = (TextView) findViewById(R.id.TXView2);
    	
        if(mConnectedDeviceName != null) {
        		
        	sendMessage("04" + '\r'); //send Clear Trouble Codes Command
        	TX.setText("Clear Codes");
        	Toast.makeText(getApplicationContext(), "OBD Trouble Codes Cleared", Toast.LENGTH_SHORT).show();
        
        }
        else {
        	Toast.makeText(getApplicationContext(), "OBD Adapter NOT CONNECTED", Toast.LENGTH_SHORT).show();
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
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    /*private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }*/

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message); 
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    //Contains previous value of parameters for gauge transition
    int prev_intake = 0;
	int prev_load = 0;
	int prev_coolant = 0;
	int prev_MPH = 0;
	int prev_RPM = 0;
	int prev_voltage = 0;
    

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
    		

        @Override
        public void handleMessage(Message msg) {
        	
        	//<--------- Initialize Data Display Fields ---------->//    	
        	final TextView RPM = (TextView) findViewById(R.id.RPMView); 
        	final TextView MPH = (TextView) findViewById(R.id.MPHView); 
        	final TextView engineLoad = (TextView) findViewById(R.id.LoadView);
        	final TextView coolantTemperature = (TextView) findViewById(R.id.CoolantView); 
        	final TextView intakeTemperature = (TextView) findViewById(R.id.IntakeView); 
        	final TextView voltage = (TextView) findViewById(R.id.voltView); 
        	final TextView RX = (TextView) findViewById(R.id.RXView2); 
        	
        	//<-------- Initialize Needle Animations --------->//
        	final ImageView pointer;
        	final ImageView pointer1;
        	final ImageView pointer2;
        	final ImageView pointer3;
        	final ImageView pointer4;
        	final ImageView pointer5;
        	final ImageView pointer6;
        	pointer = (ImageView) findViewById(R.id.imageView1);
        	pointer1 = (ImageView) findViewById(R.id.ImageView2);
        	pointer2 = (ImageView) findViewById(R.id.ImageView3);
        	pointer3 = (ImageView) findViewById(R.id.ImageView4);
        	pointer4 = (ImageView) findViewById(R.id.ImageView5);
        	pointer5 = (ImageView) findViewById(R.id.ImageView6);
        	
        	String dataRecieved;
        	int value = 0;
        	int value2 = 0;
        	int PID = 0;
        	
        	
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
              
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer               
                String readMessage = new String(readBuf, 0, msg.arg1);
                
                
                
                // ------- ADDED CODE FOR OBD -------- //      
                dataRecieved = readMessage;
                RX.setText(dataRecieved);
                
                
                if((dataRecieved != null) && (dataRecieved.matches("\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\r?\n?" ))) {

	        			dataRecieved = dataRecieved.trim();
	        			String[] bytes = dataRecieved.split(" ");
	
	        			if((bytes[0] != null)&&(bytes[1] != null)) {
	        				
	        				PID = Integer.parseInt(bytes[0].trim(), 16);
	        				value = Integer.parseInt(bytes[1].trim(), 16); 
	        				}
	        			

	        		switch(PID) {
	            	
		            	case 15://PID(0F): Intake Temperature
		            		
		            		value = value - 40; //Formula for Intake Temperature
		                    value = ((value * 9)/ 5) + 32; //Convert from Celsius to Farenheit 
		                    String displayIntakeTemp = String.valueOf(value);
		                	intakeTemperature.setText(displayIntakeTemp);
		                    
		                    int needle_value = (((value-30) * 21)/10) - 105;
		                    
			                    if(prev_intake == 0) {
			                    	RotateAnimation Intake_animation = new RotateAnimation(-105, needle_value, 30, 97);
			                    	prev_intake = needle_value;
			                    	Intake_animation.setInterpolator(new LinearInterpolator());
				            	    Intake_animation.setDuration(500);
				            	    Intake_animation.setFillAfter(true);
				            	    ((View) pointer4).startAnimation(Intake_animation); 
			                    }
			                    else {
			                    	RotateAnimation Intake_animation = new RotateAnimation(prev_intake, needle_value, 30, 97);
			                    	prev_intake = needle_value;
			                    	Intake_animation.setInterpolator(new LinearInterpolator());
				            	    Intake_animation.setDuration(500);
				            	    Intake_animation.setFillAfter(true);
				            	    ((View) pointer4).startAnimation(Intake_animation); 
			                    }
		                    
		                    
		            		break;
		            		

		            	case 4://PID(04): Engine Load
		            		
		            		value = (value * 100 ) / 255;
		            		needle_value = ((value * 21)/10) - 105;
		              	  	String displayEngineLoad = String.valueOf(value);
		              	  	
			              	  if(prev_load == 0) {
			                    	prev_load = needle_value;
			                    	RotateAnimation Load_animation = new RotateAnimation(-105, needle_value, 30, 97);
				              	  	Load_animation.setInterpolator(new LinearInterpolator());
				              	  	Load_animation.setDuration(500);
				              	  	Load_animation.setFillAfter(true);
				              	    ((View) pointer2).startAnimation(Load_animation); 
			                    }
			                    else {
			                    	RotateAnimation Load_animation = new RotateAnimation(prev_load, needle_value, 30, 97);
			                    	prev_load = needle_value;
				              	  	Load_animation.setInterpolator(new LinearInterpolator());
				              	  	Load_animation.setDuration(500);
				              	  	Load_animation.setFillAfter(true);
				              	    ((View) pointer2).startAnimation(Load_animation); 
			                    }
		              	  	
		              	  	engineLoad.setText(displayEngineLoad);
		            		break;
		            		
		            	case 5://PID(05): Coolant Temperature
		            		
		            		value = value - 40;
		            		value = ((value * 9)/ 5) + 32; //convert to deg F
		            		needle_value = (((value-50) * 21)/20) - 105;
		            		
			            		if(prev_coolant == 0) {
				            		RotateAnimation Coolant_animation = new RotateAnimation(-83, needle_value, 30, 97);
				            		Coolant_animation.setInterpolator(new LinearInterpolator());
				             	    Coolant_animation.setDuration(500);
				             	    Coolant_animation.setFillAfter(true);
				             	    ((View) pointer3).startAnimation(Coolant_animation);
				             	    prev_coolant = needle_value;
			            		}
			            		else {
			            			RotateAnimation Coolant_animation = new RotateAnimation(prev_coolant, needle_value, 30, 97);
				            		Coolant_animation.setInterpolator(new LinearInterpolator());
				             	    Coolant_animation.setDuration(500);
				             	    Coolant_animation.setFillAfter(true);
				             	    ((View) pointer3).startAnimation(Coolant_animation);
				             	    prev_coolant = needle_value;
			            		}
		            		
		             	    String displayCoolantTemp = String.valueOf(value);
		               
		             	    coolantTemperature.setText(displayCoolantTemp);
		            		break;
		            	
		            	case 12: //PID(0C): RPM
		                		int RPM_value = (value*256)/4;
		                		needle_value = ((RPM_value * 22)/1000) - 85;
		                		
		                		if(prev_RPM == 0) {
		    	            		RotateAnimation RPM_animation = new RotateAnimation(-85, needle_value, 30, 97);
		    	            		RPM_animation.setInterpolator(new LinearInterpolator());
		    	            	    RPM_animation.setDuration(500);
		    	            	    RPM_animation.setFillAfter(true);
		    	            	    ((View) pointer).startAnimation(RPM_animation);
		    	            	    prev_RPM = needle_value;
		                		}
		                		else {
		                			RotateAnimation RPM_animation = new RotateAnimation(prev_RPM, needle_value, 30, 97);
		    	            		RPM_animation.setInterpolator(new LinearInterpolator());
		    	            	    RPM_animation.setDuration(500);
		    	            	    RPM_animation.setFillAfter(true);
		    	            	    ((View) pointer).startAnimation(RPM_animation);
		    	            	    prev_RPM = needle_value;
		                		}
		                		
		                		String displayRPM = String.valueOf(RPM_value);
		                 	    RPM.setText(displayRPM);
		            		break;
		            		
		            		
		            	case 13://PID(0D): MPH
		            		
		            		value = (value * 5) / 8; //convert KPH to MPH
		            		needle_value = ((value * 21)/20) - 85;
		            		
			            		if(prev_MPH == 0) {
				            		RotateAnimation MPH_animation = new RotateAnimation(-85, needle_value, 30, 97);
				            		MPH_animation.setInterpolator(new LinearInterpolator());
				             	    MPH_animation.setDuration(500);
				             	    MPH_animation.setFillAfter(true);
				             	    ((View) pointer1).startAnimation(MPH_animation);
				             	    prev_MPH = needle_value;
			            		}
			            		else {
			            			RotateAnimation MPH_animation = new RotateAnimation(prev_MPH, needle_value, 30, 97);
				            		MPH_animation.setInterpolator(new LinearInterpolator());
				             	    MPH_animation.setDuration(500);
				             	    MPH_animation.setFillAfter(true);
				             	    ((View) pointer1).startAnimation(MPH_animation);
				             	    prev_MPH = needle_value;
			            		}
		            		
		             	    String displayMPH = String.valueOf(value);
		            	    MPH.setText(displayMPH);
		            		break;
		            		
		            	default: ;

	        		}

        	}
            else if((dataRecieved != null) && (dataRecieved.matches("\\s*[0-9A-Fa-f]{1,2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\r?\n?" ))) {
            	
    			dataRecieved = dataRecieved.trim();
    			String[] bytes = dataRecieved.split(" ");
    			
    			if((bytes[0] != null)&&(bytes[1] != null)&&(bytes[2] != null)) {
    				
    				PID = Integer.parseInt(bytes[0].trim(), 16);
    				value = Integer.parseInt(bytes[1].trim(), 16);
    				value2 = Integer.parseInt(bytes[2].trim(), 16);
    			}
    			
    			//PID(0C): RPM
            	if(PID == 12) {
            		
            		int RPM_value = ((value*256)+value2)/4;
            		int needle_value = ((RPM_value * 22)/1000) - 85;
            		
            		if(prev_RPM == 0) {
	            		RotateAnimation RPM_animation = new RotateAnimation(-85, needle_value, 30, 97);
	            		RPM_animation.setInterpolator(new LinearInterpolator());
	            	    RPM_animation.setDuration(500);
	            	    RPM_animation.setFillAfter(true);
	            	    ((View) pointer).startAnimation(RPM_animation);
	            	    prev_RPM = needle_value;
            		}
            		else {
            			RotateAnimation RPM_animation = new RotateAnimation(prev_RPM, needle_value, 30, 97);
	            		RPM_animation.setInterpolator(new LinearInterpolator());
	            	    RPM_animation.setDuration(500);
	            	    RPM_animation.setFillAfter(true);
	            	    ((View) pointer).startAnimation(RPM_animation);
	            	    prev_RPM = needle_value;
            		}
            		
            		String displayRPM = String.valueOf(RPM_value);
             	    RPM.setText(displayRPM);
            	} 
            	else if((PID == 1)||(PID == 65)) {
            		
            		switch(value) {
	            	
			            	case 15://PID(0F): Intake Temperature
			            		
			            		value2 = value2 - 40; //formula for INTAKE AIR TEMP
			                    value2 = ((value2 * 9)/ 5) + 32; //convert to deg F
			                    int needle_value = (((value2-30) * 21)/10) - 105;
			                    
				                    if(prev_intake == 0) {
				                    	RotateAnimation Intake_animation = new RotateAnimation(-105, needle_value, 30, 97);
				                    	prev_intake = needle_value;
				                    	Intake_animation.setInterpolator(new LinearInterpolator());
					            	    Intake_animation.setDuration(500);
					            	    Intake_animation.setFillAfter(true);
					            	    ((View) pointer4).startAnimation(Intake_animation); 
				                    }
				                    else {
				                    	RotateAnimation Intake_animation = new RotateAnimation(prev_intake, needle_value, 30, 97);
				                    	prev_intake = needle_value;
				                    	Intake_animation.setInterpolator(new LinearInterpolator());
					            	    Intake_animation.setDuration(500);
					            	    Intake_animation.setFillAfter(true);
					            	    ((View) pointer4).startAnimation(Intake_animation); 
				                    }
			                    
			                    String displayIntakeTemp = String.valueOf(value2);
			                	intakeTemperature.setText(displayIntakeTemp);
			            		break;
			            		
			            	case 4://PID(04): Engine Load
			            		
			            		value2 = (value2 * 100 ) / 255;
			              	  	String displayEngineLoad = String.valueOf(value2);
			              	  	needle_value = ((value2 * 21)/10) - 105;
			              	  	
				              	  if(prev_load == 0) {
				                    	prev_load = needle_value;
				                    	RotateAnimation Load_animation = new RotateAnimation(-105, needle_value, 30, 97);
					              	  	Load_animation.setInterpolator(new LinearInterpolator());
					              	  	Load_animation.setDuration(500);
					              	  	Load_animation.setFillAfter(true);
					              	    ((View) pointer2).startAnimation(Load_animation); 
				                    }
				                    else {
				                    	RotateAnimation Load_animation = new RotateAnimation(prev_load, needle_value, 30, 97);
				                    	prev_load = needle_value;
					              	  	Load_animation.setInterpolator(new LinearInterpolator());
					              	  	Load_animation.setDuration(500);
					              	  	Load_animation.setFillAfter(true);
					              	    ((View) pointer2).startAnimation(Load_animation); 
				                    }
			                
			              	  	engineLoad.setText(displayEngineLoad);
			            		break;
			            		
			            	case 5://PID(05): Coolant Temperature
			            		
			            		value2 = value2 - 40;
			            		value2 = ((value2 * 9)/ 5) + 32; //convert to deg F
			            		needle_value = (((value2-50) * 21)/20) - 105;
			            		
				            		if(prev_coolant == 0) {
					            		RotateAnimation Coolant_animation = new RotateAnimation(-83, needle_value, 30, 97);
					            		Coolant_animation.setInterpolator(new LinearInterpolator());
					             	    Coolant_animation.setDuration(500);
					             	    Coolant_animation.setFillAfter(true);
					             	    ((View) pointer3).startAnimation(Coolant_animation);
					             	    prev_coolant = needle_value;
				            		}
				            		else {
				            			RotateAnimation Coolant_animation = new RotateAnimation(prev_coolant, needle_value, 30, 97);
					            		Coolant_animation.setInterpolator(new LinearInterpolator());
					             	    Coolant_animation.setDuration(500);
					             	    Coolant_animation.setFillAfter(true);
					             	    ((View) pointer3).startAnimation(Coolant_animation);
					             	    prev_coolant = needle_value;
				            		}
			             	    
			             	    String displayCoolantTemp = String.valueOf(value2);
			             	    coolantTemperature.setText(displayCoolantTemp);
			            		break;
			            		
			            	case 13://PID(0D): MPH
			            		
			            		value2 = (value2 * 5) / 8; //convert to MPH
			            		needle_value = ((value2 * 21)/20) - 85;
			            		
				            		if(prev_MPH == 0) {
					            		RotateAnimation MPH_animation = new RotateAnimation(-85, needle_value, 30, 97);
					            		MPH_animation.setInterpolator(new LinearInterpolator());
					             	    MPH_animation.setDuration(500);
					             	    MPH_animation.setFillAfter(true);
					             	    ((View) pointer1).startAnimation(MPH_animation);
					             	    prev_MPH = needle_value;
				            		}
				            		else {
				            			RotateAnimation MPH_animation = new RotateAnimation(prev_MPH, needle_value, 30, 97);
					            		MPH_animation.setInterpolator(new LinearInterpolator());
					             	    MPH_animation.setDuration(500);
					             	    MPH_animation.setFillAfter(true);
					             	    ((View) pointer1).startAnimation(MPH_animation);
					             	    prev_MPH = needle_value;
				            		}
			             	    
			            		String displayMPH = String.valueOf(value2);
			            	    MPH.setText(displayMPH);
			            		break;
			            		
			            	default: ;
            				}
            	}
            	
            }
            else if((dataRecieved != null) && (dataRecieved.matches("\\s*[0-9]+(\\.[0-9]?)?V\\s*\r*\n*" ))) {
            	
            	dataRecieved = dataRecieved.trim();
            	String volt_number = dataRecieved.substring(0, dataRecieved.length()-1); 
            	double needle_value = Double.parseDouble(volt_number);
            	needle_value = (((needle_value - 11)*21) /0.5) - 100;
            	int volt_value = (int)(needle_value);
            	
	            	if(prev_voltage == 0) {
		            	RotateAnimation Voltage_animation = new RotateAnimation(-100, volt_value, 30, 97);
		            	Voltage_animation.setInterpolator(new LinearInterpolator());
		        	    Voltage_animation.setDuration(500);
		        	    Voltage_animation.setFillAfter(true);
		        	    ((View) pointer5).startAnimation(Voltage_animation); 
		            	prev_voltage = volt_value;
	            	}
	            	else {
	            		RotateAnimation Voltage_animation = new RotateAnimation(prev_voltage, volt_value, 30, 97);
		            	Voltage_animation.setInterpolator(new LinearInterpolator());
		        	    Voltage_animation.setDuration(500);
		        	    Voltage_animation.setFillAfter(true);
		        	    ((View) pointer5).startAnimation(Voltage_animation); 
		            	prev_voltage = volt_value;
	            	}
            	voltage.setText(dataRecieved);
            	
            } 
            else if((dataRecieved != null) && (dataRecieved.matches("\\s*[0-9]+(\\.[0-9]?)?V\\s*V\\s*>\\s*\r*\n*" ))) {
            	
            	dataRecieved = dataRecieved.trim();
            	String volt_number = dataRecieved.substring(0, dataRecieved.length()-1); 
            	double needle_value = Double.parseDouble(volt_number);
            	needle_value = (((needle_value - 11)*21) /0.5) - 100;
            	int volt_value = (int)(needle_value);
            	
	            	if(prev_voltage == 0) {
		            	RotateAnimation Voltage_animation = new RotateAnimation(-100, volt_value, 30, 97);
		            	Voltage_animation.setInterpolator(new LinearInterpolator());
		        	    Voltage_animation.setDuration(500);
		        	    Voltage_animation.setFillAfter(true);
		        	    ((View) pointer5).startAnimation(Voltage_animation); 
		            	prev_voltage = volt_value;
	            	}
	            	else {
	            		RotateAnimation Voltage_animation = new RotateAnimation(prev_voltage, volt_value, 30, 97);
		            	Voltage_animation.setInterpolator(new LinearInterpolator());
		        	    Voltage_animation.setDuration(500);
		        	    Voltage_animation.setFillAfter(true);
		        	    ((View) pointer5).startAnimation(Voltage_animation); 
		            	prev_voltage = volt_value;
	            	} 
            	
            	voltage.setText(dataRecieved);
            	
            }    
            else if((dataRecieved != null) && (dataRecieved.matches("\\s*[ .A-Za-z0-9\\?*>\r\n]*\\s*>\\s*\r*\n*" ))) {
            	
            	if(message_number == 7) message_number = 1;
            	getData(message_number++);
            }
            else {
        		
        		;	
        	}
                
 
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }

		
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
       /* case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;*/
        }
        return false;
    }
}

	
