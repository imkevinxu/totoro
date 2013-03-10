
package com.example.android.OBDIIMonitor;

import java.util.Arrays;

import android.app.Application;

public class OBDIIMonitor extends Application {
	
	String[] Arraydata= new String[100];
	
	public String getArraydata(int i){
	    return Arraydata[i];
	  }
	
	  public void setArraydata(String s, int i){
	    Arraydata[i] = s;
	  }
	  
	  public void clearArraydata() {
		  
		  Arrays.fill(Arraydata, null);
	  }
	
}

