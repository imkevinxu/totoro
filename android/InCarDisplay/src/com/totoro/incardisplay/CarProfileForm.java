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

public class CarProfileForm extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_profile_form);
		
		ProfileCarDB db = new ProfileCarDB(this);
		if (db.getProfileCount() >= 1) {
			try {
            	// retrieve all data
            	
            	Intent k = new Intent(CarProfileForm.this, Login.class);
            	startActivity(k);
            } catch (Exception e) {
            	
            }
		}
		
		// build form fields
		Spinner spinner_year = (Spinner) findViewById(R.id.profile_year);
		ArrayAdapter<CharSequence> adapter_year = ArrayAdapter.createFromResource(this,
		        R.array.car_profile_year, android.R.layout.simple_spinner_item);
		adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_year.setAdapter(adapter_year);
		
		Spinner spinner_make = (Spinner) findViewById(R.id.profile_make);
		ArrayAdapter<CharSequence> adapter_make = ArrayAdapter.createFromResource(this,
		        R.array.car_profile_make, android.R.layout.simple_spinner_item);
		adapter_make.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_make.setAdapter(adapter_make);
		
		final Button submit_button = (Button) findViewById(R.id.form_profile_submit);
		submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                	submitProfileData(v);
                	
                	Intent k = new Intent(CarProfileForm.this, Login.class);
                	startActivity(k);
                } catch (Exception e) {
                	
                }
            }
        });
	}
	
	public void submitProfileData(View v) {
		// retrieve all data
    	Spinner spinner_year = (Spinner) findViewById(R.id.profile_year);
    	Spinner spinner_make = (Spinner) findViewById(R.id.profile_make);
    	EditText text_model = (EditText) findViewById(R.id.profile_model);
    	EditText text_mpg = (EditText) findViewById(R.id.profile_mpg);
    	
    	int year = Integer.parseInt(spinner_year.getSelectedItem().toString());
    	String str_make = spinner_make.getSelectedItem().toString();
    	String str_model = text_model.getText().toString();
    	
    	double mpg = 20;
    	String str_mpg = text_mpg.getText().toString();
    	try
    	{
    	  mpg = Double.parseDouble(str_mpg);
    	}
    	catch(NumberFormatException e)
    	{// set mpg to 20
    	}
    	
    	Log.i("Put in ProfileCarDB: ", "year: " + year + " make: " + str_make+ " model: " + str_model + " mpg: " + mpg);
    	ProfileCarDB db = new ProfileCarDB(v.getContext()); 
    	db.addProfile(year, str_make, str_model, mpg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.car_profile_form, menu);
		return true;
	}

}
