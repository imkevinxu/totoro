package com.authorwjf.bounce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.format.Time;
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
	private int sashay = 0;
	private int flambe = 15;
	private double amount = 1.0;
	private int scoreNum = 99;
	private int scoreDec = 0;
	
	String rec = "";
	int recCountup = 0;
	int status = 0;
	boolean scrollIn = false;
	
	
	// Image data
	private int winWidth = -1;
	private int winHeight = -1;
	private int greenWidth = -1;
	private int greenHeight = -1;
	private int grayWidth = -1;
	private int grayHeight = -1;
	private int ballWidth = -1;
	private BitmapDrawable greenCircle = null;
	private BitmapDrawable grayCircle = null;
	private BitmapDrawable ball = null;
	private MediaPlayer mp1;
	private long last;
	private Bitmap grayMap;
	private int[] grayPixels;

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
	
	private String getRecommendation()	{
		switch((int)(5 * Math.random()))	{
		case 0: 
			mp1 = MediaPlayer.create(mContext, R.raw.rec1);
			return "Try to avoid flooring the accelerator";//.\nSudden changes in acceleration produce significantly larger\n quantities of carbon dioxide.";
		case 1: 
			mp1 = MediaPlayer.create(mContext, R.raw.rec2);
			return "Try hitting the brake pedal more softly.";//\nThis will prevent degradation of your brakes.";
		case 2: 
			mp1 = MediaPlayer.create(mContext, R.raw.rec3);
			return "Try to turn more smoothly.";
		case 3: 
			mp1 = MediaPlayer.create(mContext, R.raw.rec4);
			return "Avoid accelerating on inclines.";//\nUse your momentum to carry you through inclines.";
		case 4: 
			mp1 = MediaPlayer.create(mContext, R.raw.rec5);
			return "Avoid idling your engine.";//\nTurn off your car if you're going to not use it for extended periods of time.";
		default:
			return "No recommendation.";
		}
	}
	
	private void drawCircles(BitmapDrawable greenCircle, BitmapDrawable grayCircle, Canvas c, double amt) {
		Paint scorePaint = new Paint();
		if (scoreNum <=25) {
			scorePaint.setColor(Color.RED);
		} else {
			scorePaint.setColor(Color.WHITE);
		}
		scorePaint.setTextSize(200);

		
		if (scoreDec == 4) {
			scoreNum--;
			scoreDec = 0;
			if (scoreNum == 99) {
				last = System.currentTimeMillis();
			}
			else if (scoreNum % 10 == 0) {
				long curr = System.currentTimeMillis();
				System.out.println(scoreNum + ": " + (curr - last));
				last = curr;
			}
		} else {
			scoreDec++;
		}

		
		String score = "" + scoreNum;
		winWidth = this.getWidth();
		winHeight = this.getHeight();
		
		Rect bounds = new Rect();
		scorePaint.getTextBounds(score, 0, score.length(), bounds);
		int scoreX = winWidth/2  - bounds.width()/2 - 15;
		int scoreY = greenWidth/2 + bounds.height()/2 - 10;
		
		int endX = grayWidth;
		int endY = (int)(grayHeight * (1.0 - amt));
		int greenX = winWidth/2  - greenWidth/2;
		int grayX = winWidth/2  - grayWidth/2;

		c.drawBitmap(greenCircle.getBitmap(), greenX, 10, null);  
		c.drawBitmap(grayPixels, 0, grayMap.getWidth(), grayX, 10, endX, endY, false, null);

		//c.drawBitmap(transform, grayX, 10, null);  
	
		c.drawText(score, scoreX, scoreY, scorePaint);
		if (scoreNum < 85) {
			if (rec.equals("")) {
				rec = getRecommendation();

				mp1.start();
				scrollIn = true;
			} else {
				if (scrollIn) {
					if (status < 80) {
						status += 10;
					} else {
						scrollIn = false;
					}
				} else {
					recCountup++;
					if (recCountup > 80) {
						status -= 10;
					}
				}
				
				if (status > -10) {
				
					Rect notification = new Rect(0, getHeight() - status, getWidth(), getHeight());

					scorePaint.setColor(Color.parseColor("#3498db"));
					scorePaint.setStyle(Style.FILL);
					c.drawRect(notification, scorePaint);
					scorePaint.setTextSize(40);
					scorePaint.setColor(Color.WHITE);
					Rect recBounds = new Rect();
					scorePaint.getTextBounds(rec, 0, rec.length(), recBounds);
					c.drawText(rec, this.getWidth()/2 - recBounds.width() / 2, this.getHeight() - status + recBounds.height() + 12, scorePaint);
				}
			}
		} else {
			// reset recommendation variables 
			rec = "";
			recCountup = 0;
			scrollIn = false;
			status = 0;
		}
		
	}

	protected void onDraw(Canvas c) {  
		
		if (greenCircle == null) {
			greenCircle = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.darkcircle1);
			grayCircle = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.graycircle);
			grayMap = grayCircle.getBitmap();
			ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.waterdrop);  
			winWidth = this.getWidth();
			winHeight = this.getHeight();
			grayWidth = grayCircle.getBitmap().getWidth();
			grayHeight = grayCircle.getBitmap().getHeight();
			greenWidth = greenCircle.getBitmap().getWidth();
			greenHeight = greenCircle.getBitmap().getHeight();
			ballWidth = ball.getBitmap().getWidth();	
			grayPixels = new int[grayMap.getWidth() * grayMap.getHeight()];
			grayCircle.getBitmap().getPixels(grayPixels, 0, grayMap.getWidth(), 1, 1, grayMap.getWidth() - 1, grayMap.getHeight() - 1);
		}
		
		if (flambe >= 15) {
			
			if (x<0 && y <0) {
				x = winWidth/2  - ballWidth/2;
				y = winHeight/2;
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
				if ((y > winHeight) || (y < 0)) {
					yVelocity = 5;
					x = winWidth/2 - ballWidth/2;
					y = winHeight/2;
					sashay = 0;
					flambe = 0;
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

		} else {
			flambe++;
			amount -= .002;
			if (amount < 0.01) {
				amount = 0.99;
				scoreNum = 99;

			}
			drawCircles(greenCircle, grayCircle, c, amount);

			h.postDelayed(r, FRAME_RATE);
			
		}
	} 

}
