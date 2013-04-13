package com.example.android.BluetoothChat;

import android.app.Activity;

import android.widget.Button;
import android.widget.TextView;

public class AutomaticQueryThread extends Thread {
	
	private Button buttonToSend;
	
	private Activity activity;
	
    private int counter = 0;

    private static ELMTuple[] englishFields = new ELMTuple[]{
    	new ELMTuple("rpm", "010C"),
    	new ELMTuple("speed", "010D"),
    	new ELMTuple("intakeAir", "010F"),
    	new ELMTuple("engineTime", "011F"),
    	new ELMTuple("warmupSinceCode", "0130"),
    	new ELMTuple("torque", "0163")
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
    
	public AutomaticQueryThread(Button buttonToSend, Activity activity)	{
		this.buttonToSend = buttonToSend;
		this.activity = activity;
	}
	
	public void run()	{
		TextView view = (TextView) activity.findViewById(R.id.edit_text_out);
		while(true)	{
			long lastTime = System.currentTimeMillis();
			for(ELMTuple tuple: englishFields){
				view.setText(tuple.elmName);
				buttonToSend.performClick();
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
	
}
