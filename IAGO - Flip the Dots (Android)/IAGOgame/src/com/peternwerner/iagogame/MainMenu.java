package com.peternwerner.iagogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

public class MainMenu {

	private static final String TAG = MainMenu.class.getSimpleName();
	
	float heightTitle = (float) (0.48 * MainGame.HEIGHT);
	int lastButtonClicked = 0;	// 1 => classic play, 2 => settings
	
	// Effect timing variables
	private static long timeStart, timeNow;
	private static long timeDelay = 400;	// delay time in milliseconds
	
	// Effect controls
	boolean startFade = true;
	boolean doDisableClick = false;
	boolean doPaintFade = false;
	
	int nextState = 0;
	
		
	// run when the object is first instantiated
	public void init() {
	}
	
	
	// run every time the thread updates
	public void update() {
	}
	
	
	// paints everything
	public void paint(Canvas canvas) {
		
		// paint text
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(MainGame.color_medium);		
		paint.setTypeface(MainGame.tf);
		paint.setTextScaleX((float) 1.15);
		paint.setTextAlign(Paint.Align.CENTER);
				
		paint.setTextSize((float) (MainGame.WIDTH * 0.58));
						
		canvas.drawText("IAGO", (float) (0.492 * MainGame.WIDTHactual), heightTitle, paint);
				
		paint.setTextSize((float) (MainGame.WIDTH * 0.115));

		paint.setColor(MainGame.color_dark);	
		if(lastButtonClicked == 1)
			paint.setColor(MainGame.color_medium);				
		canvas.drawText("Start Playing", (float) (0.5 * MainGame.WIDTHactual), heightTitle + (float) (MainGame.WIDTH * 0.18), paint);
		
		paint.setColor(MainGame.color_dark);		
		if(lastButtonClicked == 2)
			paint.setColor(MainGame.color_medium);
		canvas.drawText("Settings", (float) (0.5 * MainGame.WIDTHactual), heightTitle + (float) (MainGame.WIDTH * 0.34), paint);
		
		// paint fade effect
		if(doPaintFade) {
			paintFade(canvas);
		}
		
	}
	
	
	
	// paint the fade effect
	public boolean paintFade(Canvas canvas) {

		// only return true the first time the win condition is met
		if(!startFade) {
			
			// move to the next state if it has been long enough since startfade first returned true
			timeNow = System.currentTimeMillis();

			if(timeNow - timeStart >= timeDelay) {
				doPaintFade = false;
				
				if(lastButtonClicked == 1) {
					if(MainGame.didTutorial)
						nextState = -4;
					else
						nextState = -8;
				}
				else if(lastButtonClicked == 2)
					nextState = -7;
					;

				// Log.d(TAG, "NEXT STATE " + lastButtonClicked);
				MainGame.gameState = nextState;
				
				doDisableClick = false;
				startFade = true;
				
				lastButtonClicked = 0;
			}
		
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
		
			float timeFactor = (timeDelay / 4) + timeNow - timeStart;
			if(timeFactor > timeDelay)
				timeFactor = timeDelay;
			
			int alpha = (int) (255 * (((float)(timeFactor) / timeDelay)));
			paint.setAlpha(alpha);
			
			canvas.drawRect(0, 0, MainGame.WIDTHactual, MainGame.HEIGHT, paint);
			
			return false;
		}
		doPaintFade = false;
		
		// if the win condition is met for the first time, store time stamp, disable clicking, paint screen burst, return true
		timeStart = System.currentTimeMillis();
		doDisableClick = true;
		doPaintFade = true;
		startFade = false;
		return true;
		
	}
	
	
	
	// detects screen clicks
	public boolean checkClick(float x, float y) {
		
		if(!doDisableClick  &&  x > 0.25 * MainGame.WIDTHactual  &&  x < 0.75 * MainGame.WIDTHactual) {

			// user clicks the play classic mode button
			if(y > heightTitle + (float) (MainGame.WIDTH * (0.18 - 0.13))  &&  y < heightTitle + (float) (MainGame.WIDTH * (0.18 + 0.03))) {
				
				lastButtonClicked = 1;
				
				// start the fade effect (this will move to the next state when done displaying effect)
				doPaintFade = true;
				doDisableClick = true;
				
				return true;
			}
			
			// user clicks the settings button
			if(y > heightTitle + (float) (MainGame.WIDTH * (0.34 - 0.13))  &&  y < heightTitle + (float) (MainGame.WIDTH * (0.34 + 0.03))) {
				
				lastButtonClicked = 2;
				
				// start the fade effect (this will move to the next state when done displaying effect)
				doPaintFade = true;
				doDisableClick = true;
				
				return true;
			}

		}
		return false;
	}
	
}
