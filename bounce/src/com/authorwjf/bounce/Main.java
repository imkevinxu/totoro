package com.authorwjf.bounce;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.android.BluetoothChat");
		//startActivity(launchIntent);
    }
}
