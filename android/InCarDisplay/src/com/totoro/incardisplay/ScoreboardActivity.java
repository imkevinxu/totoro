package com.totoro.incardisplay;

import android.support.v4.app.Fragment;

import com.totoro.incardisplay.ScoreboardFragment;
import com.totoro.incardisplay.SingleFragmentActivity;

/**
 *  Activity used once a user opens the Facebook scoreboard - all logic
 *  is within ScoreboardFragment
 */
public class ScoreboardActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new ScoreboardFragment();
	}
	
}

