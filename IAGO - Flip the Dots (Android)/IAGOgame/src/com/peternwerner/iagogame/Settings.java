package com.peternwerner.iagogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;


public class Settings {

	private static final String TAG = ClassicMenu.class.getSimpleName();
	
	// constants
	private int WIDTH, WIDTHactual;
	private int HEIGHT;
	
	private int spaceTop, widthRibbon, widthSquare, widthPages, widthAdd, buffer, heightOption, spacer, left;
	private int scaleOffset = MainGame.scaleOffset;
	
	// paths
	private int xPtsRibbons[][] = new int[3][4];
	private int yPtsRibbons[][] = new int[3][4];
	private Rect optionRects[]  = new Rect[7];
	
	// is the reset "are you sure" message being displayed?
	private boolean confirmReset = false;
	// has the reset been triggered?
	private boolean didReset = false;
	
	
	// run when the object is first instantiated
	public void init() {
	
		// initialize variables
		
		WIDTH = MainGame.WIDTH;		HEIGHT = MainGame.HEIGHT;	WIDTHactual = MainGame.WIDTHactual;
		spaceTop = MainGame.distFromTopSquare - (int) (0.0520833 * HEIGHT);	// subtract width of puzzle mode scoreboard
		widthRibbon = (int) (0.065625 * HEIGHT);
		widthPages = (int) (0.0520833 * HEIGHT);
		widthSquare = MainGame.widthSquare;
		buffer = MainGame.distBufferSide;
		
		spacer = (int) (0.5 * (spaceTop - widthRibbon));
		
		// Top ribbon
		xPtsRibbons[0][0] = 0;							yPtsRibbons[0][0] = spacer;
		xPtsRibbons[0][1] = WIDTHactual;				yPtsRibbons[0][1] = spacer;
		xPtsRibbons[0][2] = WIDTHactual;				yPtsRibbons[0][2] = widthRibbon + spacer;
		xPtsRibbons[0][3] = 0;							yPtsRibbons[0][3] = widthRibbon + spacer;
	
		// Sub ribbon
		xPtsRibbons[1][0] = WIDTHactual;				yPtsRibbons[1][0] = spaceTop + MainGame.widthSquare + spacer + widthPages;
		xPtsRibbons[1][1] = 0;							yPtsRibbons[1][1] = spaceTop + MainGame.widthSquare + spacer + widthPages;
		xPtsRibbons[1][2] = 0;							yPtsRibbons[1][2] = widthRibbon + spaceTop + MainGame.widthSquare + spacer + widthPages;
		xPtsRibbons[1][3] = WIDTHactual;				yPtsRibbons[1][3] = widthRibbon + spaceTop + MainGame.widthSquare + spacer + widthPages;
		
		// Bottom ribbon
		xPtsRibbons[2][0] = 0;							yPtsRibbons[2][0] = yPtsRibbons[1][2] + spacer;
		xPtsRibbons[2][1] = WIDTHactual;				yPtsRibbons[2][1] = yPtsRibbons[1][3] + spacer;
		xPtsRibbons[2][2] = WIDTHactual;				yPtsRibbons[2][2] = HEIGHT;
		xPtsRibbons[2][3] = 0;							yPtsRibbons[2][3] = HEIGHT;
	
		widthAdd = spaceTop - yPtsRibbons[0][2];
	
		// start building the options rectangles
		
		left = (WIDTHactual - widthSquare) / 2;
		heightOption = (int) ((widthSquare + widthAdd - (4 * buffer)) / 4);
		
		// tutorial button (2)
		optionRects[2] = new Rect(left, spaceTop, left + widthSquare, spaceTop + heightOption);
		// blank accent strips (0)
		optionRects[0] = new Rect(left, optionRects[2].bottom + buffer, left + widthSquare, optionRects[2].bottom + buffer + heightOption / 2);
		/*
		// sound button (3, 4, 5) (main, on, off)
		optionRects[3] = new Rect(left, optionRects[0].bottom + buffer, (int) (left + 0.7 * widthSquare) - buffer, optionRects[0].bottom + buffer + heightOption);
		optionRects[4] = new Rect((int) (left + 0.7 * widthSquare), optionRects[3].top, left + widthSquare, optionRects[3].top + ((heightOption)/2));
		optionRects[5] = new Rect((int) (left + 0.7 * widthSquare), optionRects[3].top + ((heightOption)/2), left + widthSquare, optionRects[3].bottom);
		*/
		// unlock all button
		optionRects[3] = new Rect(left, optionRects[0].bottom + buffer, left + widthSquare, optionRects[0].bottom + buffer + heightOption);
		optionRects[4] = new Rect(left, optionRects[0].bottom + buffer, left, optionRects[0].bottom + buffer + heightOption);
		optionRects[5] = new Rect(left, optionRects[0].bottom + buffer, left, optionRects[0].bottom + buffer + heightOption);

		// blank  accent strips (1)
		optionRects[1] = new Rect(left, optionRects[3].bottom + buffer, left + widthSquare, optionRects[3].bottom + buffer + heightOption / 2);
		// reset button (6)
		optionRects[6] = new Rect(left, optionRects[1].bottom + buffer, left + widthSquare, optionRects[1].bottom + buffer + heightOption);

	}
	
	
	
	
	// what we do when the user clicks on the screen...
	public boolean checkClick(float x, float y) {
		
		// user clicks on the menu button
		if(y >= yPtsRibbons[1][0]  &&  y <= yPtsRibbons[1][2]) {
			
			MainGame.gameState = 0;
			
			return true;
		}
		
		// user clicks on the tutorial button
		else if(optionRects[2].contains((int) x, (int) y)) {
			
			MainGame.gameState = 8;
			
			return true;
		}
		
		// user clicks on the sound buttons
		else if(optionRects[3].contains((int) x, (int) y)  ||  optionRects[4].contains((int) x, (int) y)  ||  optionRects[5].contains((int) x, (int) y)) {
			
			/*
			if(!MainGame.soundEnabled)
				MainGame.soundEnabled = true;
			else
				MainGame.soundEnabled = false;
			*/
			
			MainGame.unlockAll = true;
			
			return true;
		}
		
		// user clicks on the reset button
		else if(!didReset  &&  optionRects[6].contains((int) x, (int) y)) {
			
			if(!confirmReset)
				confirmReset = true;
			else {
				if(x < WIDTHactual / 2  &&  x > left) {
					
					reset();
				}
				else if(x > WIDTHactual / 2  &&  x < WIDTHactual - left) {
					
					confirmReset = false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	
	
	// paint everything
	public void paint(Canvas canvas) {

		paintRibbons(canvas);
		paintOptions(canvas);
		paintText(canvas);
	
	}
	
	
	// paint the ribbons
	public void paintRibbons(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(MainGame.color_dark);
		
		for(int i = 0; i < xPtsRibbons.length; i++) {
			
			Path path = new Path();
			path.moveTo(xPtsRibbons[i][0], yPtsRibbons[i][0]);
			
			for(int j = 1; j < xPtsRibbons[i].length; j++) {
				
				path.lineTo(xPtsRibbons[i][j], yPtsRibbons[i][j]);
			}
			path.close();
			
			canvas.drawPath(path, paint);
		}
		
	}
	
	
	
	// paint the options art
	public void paintOptions(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		// paint accent strips
		for(int i = 0; i <= 1; i++) {
			paint.setColor(Color.rgb(150,190,185));
			canvas.drawRect(optionRects[i], paint);
		}
		
		// paint the tutorial button
		paint.setColor(Color.rgb(195,161,149));
		canvas.drawRect(optionRects[2], paint);
		
		// paint the sound buttons
		paint.setColor(MainGame.color_medium);
		canvas.drawRect(optionRects[3], paint);

		paint.setColor(MainGame.color_medium);
		if(!MainGame.soundEnabled)
			paint.setColor(Color.LTGRAY);
		canvas.drawRect(optionRects[4], paint);
		
		paint.setColor(MainGame.color_medium);
		if(MainGame.soundEnabled)
			paint.setColor(Color.LTGRAY);
		canvas.drawRect(optionRects[5], paint);
		
		// paint the reset button
		paint.setColor(Color.rgb(195,161,149));
		canvas.drawRect(optionRects[6], paint);

	}
	
	
	
	// paint the text
	public void paintText(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(MainGame.tf);
		paint.setTextScaleX((float) 1.15);
		paint.setTextAlign(Paint.Align.CENTER);
		
		// paint ribbon text
		
		paint.setTextSize(widthRibbon);
		paint.setColor(MainGame.color_white);
		
		canvas.drawText("Settings", WIDTHactual / 2, (float) (yPtsRibbons[0][2] - (0.14 * widthRibbon)), paint);
		canvas.drawText("Main Menu", WIDTHactual / 2, (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
		
		// paint option text
		
		paint.setTextSize((float) (0.40 * heightOption));
		canvas.drawText("Not sure what's going on?", (optionRects[2].left + optionRects[2].right) / 2, optionRects[2].top + (float)(0.49 * heightOption), paint);
		canvas.drawText("Try this quick tutorial", (optionRects[2].left + optionRects[2].right) / 2, optionRects[2].top + (float)(0.83 * heightOption), paint);

		/*
		canvas.drawText("Toggle Sounds", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.49 * heightOption), paint);
		if(MainGame.soundEnabled)
			canvas.drawText("(currently on)", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.83 * heightOption), paint);
		else
			canvas.drawText("(currently off)", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.83 * heightOption), paint);
		canvas.drawText("On", (optionRects[4].left + optionRects[4].right) / 2, optionRects[4].top + (float)(0.74 * 0.5 *heightOption), paint);
		canvas.drawText("Off", (optionRects[5].left + optionRects[5].right) / 2, optionRects[5].top + (float)(0.74 * 0.5 * heightOption), paint);
		*/
		
		if(!MainGame.unlockAll) {
			canvas.drawText("Not a hard worker?", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.49 * heightOption), paint);
			canvas.drawText("Unlock all levels", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.83 * heightOption), paint);
		}
		else {
			canvas.drawText("All levels unlocked", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.49 * heightOption), paint);
			canvas.drawText("Are you any happier?", (optionRects[3].left + optionRects[3].right) / 2, optionRects[3].top + (float)(0.83 * heightOption), paint);
		}
		
		if(!confirmReset  &&  !didReset) {
			canvas.drawText("Hate your own success?", (optionRects[6].left + optionRects[6].right) / 2, optionRects[6].top + (float)(0.49 * heightOption), paint);
			canvas.drawText("Reset all progress", (optionRects[6].left + optionRects[6].right) / 2, optionRects[6].top + (float)(0.83 * heightOption), paint);
		}
		else if(confirmReset) {
			canvas.drawText("Really", (optionRects[6].left + optionRects[6].right) / 2 - (widthSquare / 4), optionRects[6].top + (float)(0.49 * heightOption), paint);
			canvas.drawText("Reset?", (optionRects[6].left + optionRects[6].right) / 2 - (widthSquare / 4), optionRects[6].top + (float)(0.83 * heightOption), paint);
			canvas.drawText("Wait!", (optionRects[6].left + optionRects[6].right) / 2 + (widthSquare / 4), optionRects[6].top + (float)(0.49 * heightOption), paint);
			canvas.drawText("Cancel!", (optionRects[6].left + optionRects[6].right) / 2 + (widthSquare / 4), optionRects[6].top + (float)(0.83 * heightOption), paint);
		}
		else if(didReset) {
			canvas.drawText("The deed is done...", (optionRects[6].left + optionRects[6].right) / 2, optionRects[6].top + (float)(0.49 * heightOption), paint);
			canvas.drawText("All progress reset", (optionRects[6].left + optionRects[6].right) / 2, optionRects[6].top + (float)(0.83 * heightOption), paint);
		}
			
	}
	
	
	public void reset() {
		
		// reset main game score variables
		for(int i = 0; i < MainGame.scoreListBest.length; i++) {
			for(int j = 0; j < MainGame.scoreListBest[i].length; j++) {
				MainGame.scoreListBest[i][j]  = 0;
				MainGame.scoreListStars[i][j] = 0;
			}
		}
		for(int i = 0; i < MainGame.menuPage.length; i++) {
			MainGame.menuPage[i] = 1;
		}

		MainGame.unlockAll = false;
		
		confirmReset = false;
		didReset = true;
	}
	
}