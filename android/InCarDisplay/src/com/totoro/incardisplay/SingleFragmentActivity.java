package com.totoro.incardisplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.WindowManager;

/**
 *  Only used by Activities where a single Fragment is used and not changed (i.e. used by
 *  all Activities except HomeActivity
 */
public abstract class SingleFragmentActivity extends FragmentActivity {
	
	abstract Fragment createFragment();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
		
		if (fragment == null)
		{
			fragment = createFragment();
			manager.beginTransaction()
				.add(R.id.fragmentContainer, fragment)
				.commit();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Hide the notification bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// Measure mobile app install ads
 		// Ref: https://developers.facebook.com/docs/tutorials/mobile-app-ads/
 		com.facebook.Settings.publishInstallAsync(this, ((OmniDriveApplication)getApplication()).getString(R.string.app_id));
	}

}

