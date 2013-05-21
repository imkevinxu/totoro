package com.authorwjf.bounce;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SummaryActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        
        TextView mpgText = (TextView)findViewById(R.id.avg_mpg);
        mpgText.setText(Double.toString(AnimatedView.avgMpg));
        
        TextView recText = (TextView)findViewById(R.id.top_tip);
        recText.setText(AnimatedView.recommendedTip);
        
        TextView coinText = (TextView)findViewById(R.id.total_coins);
        coinText.setText(AnimatedView.totalCoinsWon);
        
        ProgressBar progress = (ProgressBar)findViewById(R.id.level_progress);
        progress.setMax(levelUp(AnimatedView.totalCoinsWon));
        progress.setProgress(AnimatedView.totalCoinsWon);
    }
    
    private int levelUp(int curCoins) {
    	// level 1
    	if (curCoins < 500) {
    		return 500;
    	} else if (curCoins < 1000) {
    		return 1000;
    	} else if (curCoins < 5000) {
    		return 5000;
    	} else if (curCoins < 10000) {
    		return 10000;
    	} else {
    		return 0;
    	}
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    
}
