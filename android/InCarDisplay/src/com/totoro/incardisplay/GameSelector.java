package com.totoro.incardisplay;

import com.totoro.incardisplay.ScoreboardActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class GameSelector extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selector);

		
		final ImageButton start_button = (ImageButton) findViewById(R.id.start);
		start_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                	Intent i = new Intent(GameSelector.this, ScoreboardActivity.class);
            		startActivityForResult(i, 0);
                } catch (Exception e) {
                	
                }
            }
        });
		
		final ImageButton back_button = (ImageButton) findViewById(R.id.back);
		back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                	Intent i = new Intent(GameSelector.this, Login.class);
            		startActivity(i);
                } catch (Exception e) {
                	
                }
            }
        });
		

		
		final ImageButton green_driving_button = (ImageButton) findViewById(R.id.green_driving_button);
		green_driving_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					green_driving_button.setImageResource(R.drawable.green_driving);
					green_driving_button.setBackgroundColor(16711680);
				} catch (Exception e) {
					
				}
			}
		});
		
		final ImageButton skill_driving_button = (ImageButton) findViewById(R.id.skill_driving_button);
		skill_driving_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					skill_driving_button.setImageResource(R.drawable.skill_driving);
					skill_driving_button.setBackgroundColor(16711680);
				} catch (Exception e) {
					
				}
			}
		});
	}

}

