package com.authorwjf.bounce;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SummaryActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.summary);
        
        TextView mpgText = (TextView)findViewById(R.id.avg_mpg);
        mpgText.setText("" + Double.toString(AnimatedView.avgMpg));
        
        TextView recText = (TextView)findViewById(R.id.top_tip);
        recText.setText("" + AnimatedView.recommendedTip);
        
        TextView coinText = (TextView)findViewById(R.id.total_coins);
        coinText.setText("" + AnimatedView.totalCoinsWon);
        
        TextView repText = (TextView)findViewById(R.id.rep_field);
        repText.setText("" + AnimatedView.rep);
        
        ProgressBar progress = (ProgressBar)findViewById(R.id.level_progress);
        progress.setMax(levelUp(AnimatedView.totalCoinsWon));
        progress.setProgress(AnimatedView.totalCoinsWon);
        
        ImageButton exitButton = (ImageButton) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               moveTaskToBack(true);
               finish();
            }
        });
        
    }
    
    private int levelUp(int curCoins) {
    	// level 1
    	if (curCoins < 50000) {
    		return 50000;
    	} else if (curCoins < 100000) {
    		return 100000;
    	} else if (curCoins < 500000) {
    		return 500000;
    	} else if (curCoins < 1000000) {
    		return 1000000;
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
