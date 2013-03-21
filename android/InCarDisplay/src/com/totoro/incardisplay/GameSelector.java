package com.totoro.incardisplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class GameSelector extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selector);

		
		final Button start_button = (Button) findViewById(R.id.start);
		start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {

                } catch (Exception e) {
                	
                }
            }
        });
	}

}

