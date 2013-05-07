package com.authorwjf.bounce;

import android.app.Activity;

import android.widget.Button;
import android.widget.TextView;

public class AutomaticQueryThread extends Thread {

	private Button buttonToSend;

	private BluetoothChat activity;

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

	public AutomaticQueryThread(Button buttonToSend, BluetoothChat activity)	{
		this.buttonToSend = buttonToSend;
		this.activity = activity;
	}

	public void run()	{
		try {
			Thread.sleep(1000);
		}
		catch(Exception ignored) {}
		while(true)	{
			long lastTime = System.currentTimeMillis();
			for(ELMTuple tuple: englishFields){
				activity.hackMessage(tuple.elmName);
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

}
