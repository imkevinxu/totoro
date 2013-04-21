package com.authorwjf.bounce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimatedView extends ImageView{

	private Context mContext;
	int x = -1;
	int y = -1;
	//private int xVelocity = 10;
	private int yVelocity = 5;
	private Handler h;
	private final int FRAME_RATE = 30;
	int sashay = 0;
	int flambe = 15;
	private double amount = 1.0;
	int scoreNum = 99;
	int scoreDec = 0;

	public AnimatedView(Context context, AttributeSet attrs)  {  
		super(context, attrs);  
		mContext = context;  
		h = new Handler();
	} 

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			invalidate(); 
		}
	};
	
	private void drawCircles(BitmapDrawable greenCircle, BitmapDrawable grayCircle, Canvas c, double amt) {
		Paint scorePaint = new Paint();
		scorePaint.setColor(Color.WHITE);
		scorePaint.setTextSize(200);
		
		if (scoreDec == 4) {
			scoreNum--;
			scoreDec = 0;
		} else {
			scoreDec++;
		}
		
		String score = "" + scoreNum;
		
		Rect bounds = new Rect();
		scorePaint.getTextBounds(score, 0, score.length(), bounds);
		int scoreX = this.getWidth()/2  - bounds.width()/2 - 15;
		int scoreY = greenCircle.getBitmap().getWidth()/2 + bounds.height()/2 - 10;
		
		int endX = grayCircle.getBitmap().getWidth();
		int endY = (int)(grayCircle.getBitmap().getHeight() * (1.0 - amt));
		int greenX = this.getWidth()/2  - greenCircle.getBitmap().getWidth()/2;
		int grayX = this.getWidth()/2  - grayCircle.getBitmap().getWidth()/2;
		
		Bitmap transform = Bitmap.createBitmap(grayCircle.getBitmap(), 0, 0, endX, endY);
		c.drawBitmap(greenCircle.getBitmap(), greenX, 10, null);  
		c.drawBitmap(transform, grayX, 28, null);  
		
		c.drawText(score, scoreX, scoreY, scorePaint);
	}

	protected void onDraw(Canvas c) {  
		BitmapDrawable greenCircle = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.darkcircle);
		BitmapDrawable grayCircle = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.graycircle);
		if (flambe >= 15) {
			BitmapDrawable ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.waterdrop);  
			
			if (x<0 && y <0) {
				x = this.getWidth()/2  - ball.getBitmap().getWidth()/2;
				y = this.getHeight()/2;
				yVelocity *= 1.1;
			} else {
				y += yVelocity;
				if (sashay < 2) {
					yVelocity *= 1.1;
					sashay++;
				} else if (sashay < 18) {
					yVelocity *= 1.25;
					sashay++;
				} else {
					yVelocity *= 1.2;
				}
				if ((y > this.getHeight()/* - ball.getBitmap().getHeight()*/) || (y < 0)) {
					yVelocity = 5;
					x = this.getWidth()/2 - ball.getBitmap().getWidth()/2;
					y = this.getHeight()/2;
					sashay = 0;
					flambe = 0;
					System.out.println("flambe: " + flambe);
				}
			}
			if (flambe > 0) {
				c.drawBitmap(ball.getBitmap(), x, y, null);  

			}
			amount -= .002;
			if (amount < 0.01) {
				amount = 0.99;
				scoreNum = 99;

			}
			drawCircles(greenCircle, grayCircle, c, amount);

			h.postDelayed(r, FRAME_RATE);

			System.out.println("flambe: " + flambe);

		} else {
			flambe++;
			amount -= .002;
			if (amount < 0.01) {
				amount = 0.99;
				scoreNum = 99;

			}
			drawCircles(greenCircle, grayCircle, c, amount);

			h.postDelayed(r, FRAME_RATE);
			
			System.out.println("flambe: " + flambe);
		}
	} 

}
