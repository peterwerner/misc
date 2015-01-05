package com.peternwerner.iagogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;


public class ClassicMenu {

	private static final String TAG = ClassicMenu.class.getSimpleName();
	
	// constants
	private int WIDTH, WIDTHactual;
	private int HEIGHT;
	
	private int spaceTop, widthRibbon, widthSquare, widthPages, buffer, widthThird, heightMode, spacer;
	private int scaleOffset = MainGame.scaleOffset;
	private float[] completions = new float[5];	// completion ratios (0-1) for each mode (not including random)
	
	// paths
	private int xPtsRibbons[][] = new int[3][4];
	private int yPtsRibbons[][] = new int[3][4];
	private Rect[] modeRects = new Rect[9];

	private boolean lockMed = false, lockMedRand = false, lockHard = false, lockHardRand = false, lockGenius = false, lockGeniusRand = false;
	private boolean messageOnScreen = false;
	private String line1, line2, line3, lineButton;

	
	
	
	// run when the object is first instantiated
	public void init() {
		
		// set completion values (FYI: i + 1 = n)
		for(int i = 1; i < completions.length; i++) {
			float sumStars = 0;
			
			for(int j = 0; j < MainGame.scoreListStars[i + 1].length; j++)
				sumStars += MainGame.scoreListStars[i + 1][j];
			
			completions[i] = sumStars / (float)(3 * MainGame.levelListSize[i + 1]);
		}
		completions[0] = 0;
		
		// determine locking status based on completions
		if(!MainGame.unlockAll) {
				
			if(completions[1] < 0.15)
				lockMed = true;
			if(completions[2] < 0.15) {
				lockHard = true;
				lockGenius = true;
			}
			if(completions[2] < 0.50)
				lockMedRand = true;
			if(completions[3] < 0.50)
				lockHardRand = true;		
			if(completions[4] < 0.50)
				lockGeniusRand = true;
		}
		
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


		int left = (WIDTH - widthSquare) / 2;
		heightMode = (widthSquare - (4 * buffer)) / 4;
		// blank accent block 1
		modeRects[0] = new Rect(left, spaceTop, WIDTH - left, spaceTop + (heightMode / 2));
		// normal modes (2x2, 3x3, 4x4, 5x5)
		int offset = widthPages / 2;
		modeRects[1] = new Rect(left, modeRects[0].bottom + buffer, (WIDTH - buffer) / 2, heightMode + modeRects[0].bottom + buffer + offset);
		modeRects[2] = new Rect((WIDTH + buffer) / 2, modeRects[0].bottom + buffer, left + widthSquare, heightMode + modeRects[0].bottom + buffer + offset);
		modeRects[3] = new Rect(left, modeRects[1].bottom + buffer, (WIDTH - buffer) / 2, heightMode + modeRects[1].bottom + buffer + offset);
		modeRects[4] = new Rect((WIDTH + buffer) / 2, modeRects[2].bottom + buffer, left + widthSquare, heightMode + modeRects[2].bottom + buffer + offset);
		// blank accent block 2
		modeRects[8] = new Rect(left, modeRects[3].bottom + buffer, WIDTH - left, modeRects[3].bottom + buffer + (heightMode / 2));
		// random modes (3x3, 4x4, 5x5)
		widthThird = (widthSquare - (buffer * 2)) / 3;
		modeRects[5] = new Rect(left, modeRects[8].bottom + buffer, left + widthThird, heightMode + modeRects[8].bottom + buffer);
		modeRects[6] = new Rect(left + widthThird + buffer, modeRects[8].bottom + buffer, left + widthSquare - (widthThird + buffer), heightMode + modeRects[8].bottom + buffer);
		modeRects[7] = new Rect(left + widthSquare - widthThird, modeRects[8].bottom + buffer, left + widthSquare, heightMode + modeRects[8].bottom + buffer);

		for(int i = 0; i < modeRects.length; i++)
			modeRects[i].offset(scaleOffset, 0);
	}
	
	
	
	// what we do when the user clicks on the screen...
	public boolean checkClick(float x, float y) {
		
		// clicking is disabled by a message, clicking anywhere on screen will make the message go away
		if(messageOnScreen) {
			messageOnScreen = false;
			return true;
		}
		
		// user clicks on the tutorial button 
		/*
		if(modeRects[0].contains((int) x, (int) y)) {
			
			// TODO: tutorial
			
			return true;
		}
		*/
		
		// user clicks on the easy button
		else if(modeRects[1].contains((int) x, (int) y)) {
			
			MainGame.n = 2;
			MainGame.gameState = 3;

			return true;
		}
		
		// user clicks on the medium button
		else if(modeRects[2].contains((int) x, (int) y)) {
			
			if(lockMed) {
				displayLockMessage("Earn at least", "15% completion in", "Novice to unlock", "Ok");
				return true;
			}
			
			MainGame.n = 3;
			MainGame.gameState = 3;
			
			return true;
		}
		
		// user clicks on the hard button
		else if(modeRects[3].contains((int) x, (int) y)) {
			
			if(lockHard) {
				displayLockMessage("Earn at least", "15% completion in", "Medium to unlock", "Ok");
				return true;
			}
			
			MainGame.n = 4;
			MainGame.gameState = 3;

			return true;
		}
		
		// user clicks on the genius button
		else if(modeRects[4].contains((int) x, (int) y)) {
			
			if(lockGenius) {
				displayLockMessage("Earn at least", "15% completion in", "Medium to unlock", "Ok");
				return true;
			}
			
			MainGame.n = 5;
			MainGame.gameState = 3;

			return true;
		}
		
		// user clicks on the random medium button
		else if(modeRects[5].contains((int) x, (int) y)) {
			
			if(lockMedRand) {
				displayLockMessage("Earn at least", "50% completion in", "Medium to unlock", "Ok");
				return true;
			}
			
			MainGame.n = 3;
			MainGame.gameState = 5;

			return true;
		}
		
		// user clicks on the random hard button
		else if(modeRects[6].contains((int) x, (int) y)) {
			
			if(lockHardRand) {
				displayLockMessage("Earn at least", "50% completion in", "Hard to unlock", "Ok");
				return true;
			}
			
			MainGame.n = 4;
			MainGame.gameState = 5;

			return true;
		}
		
		// user clicks on the random genius button
		else if(modeRects[7].contains((int) x, (int) y)) {
			
			if(lockGeniusRand) {
				displayLockMessage("Earn at least", "50% completion in", "Genius to unlock", "Ok");
				return true;
			}
			
			MainGame.n = 5;
			MainGame.gameState = 5;

			return true;
		}
		
		// user clicks on the menu button
		else if(y >= yPtsRibbons[1][0]  &&  y <= yPtsRibbons[1][2]) {
			
			MainGame.gameState = 0;

			return true;
		}
		
		return false;
	}
	
	
	
	// paint everything
	public void paint(Canvas canvas) {

		paintRibbons(canvas);
		paintModes(canvas);
		paintText(canvas);
		paintLockRects(canvas);

		if(messageOnScreen)
			paintLockMessage(canvas);
		
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
	
	
	
	// paint the mode art
	public void paintModes(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		for(int i = 0; i < modeRects.length  &&  modeRects[i] != null; i++) {
			
			if(i < 1  ||  i > 7)
				paint.setColor(Color.rgb(150,190,185));
			else if(i <= 4)
				paint.setColor(MainGame.color_medium);
			else
				paint.setColor(Color.rgb(195,161,149));
			canvas.drawRect(modeRects[i], paint);
		}
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
		
		canvas.drawText("Select a Mode", WIDTHactual / 2, (float) (yPtsRibbons[0][2] - (0.14 * widthRibbon)), paint);
		canvas.drawText("Main Menu", WIDTHactual / 2, (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
		
		// paint mode text
		
		paint.setTextSize((float) (0.40 * heightMode));
		paint.setColor(MainGame.color_white);
		
		paint.setTextSize((float) (0.64 * heightMode));
		paint.setColor(MainGame.color_white);

		canvas.drawText("Novice", (modeRects[1].left + modeRects[1].right) / 2, modeRects[1].top + (float)(0.74 * heightMode), paint);
		canvas.drawText("Medium", (modeRects[2].left + modeRects[2].right) / 2, modeRects[2].top + (float)(0.74 * heightMode), paint);
		canvas.drawText("Expert", (modeRects[3].left + modeRects[3].right) / 2, modeRects[3].top + (float)(0.74 * heightMode), paint);
		canvas.drawText("Genius", (modeRects[4].left + modeRects[4].right) / 2, modeRects[4].top + (float)(0.74 * heightMode), paint);
		
		paint.setTextSize((float) (0.24 * heightMode));
		paint.setColor(MainGame.color_light);

		canvas.drawText((int)(completions[1] * 100) + "% Completed", (modeRects[1].left + modeRects[1].right) / 2, modeRects[1].top + (float)(0.96 * heightMode), paint);
		canvas.drawText((int)(completions[2] * 100) + "% Completed", (modeRects[2].left + modeRects[2].right) / 2, modeRects[2].top + (float)(0.96 * heightMode), paint);
		canvas.drawText((int)(completions[3] * 100) + "% Completed", (modeRects[3].left + modeRects[3].right) / 2, modeRects[3].top + (float)(0.96 * heightMode), paint);
		canvas.drawText((int)(completions[4] * 100) + "% Completed", (modeRects[4].left + modeRects[4].right) / 2, modeRects[4].top + (float)(0.96 * heightMode), paint);

		paint.setTextSize((float) (0.40 * heightMode));
		paint.setColor(MainGame.color_white);
		
		canvas.drawText("Random", (modeRects[5].left + modeRects[5].right) / 2, modeRects[5].top + (float)(0.46 * heightMode), paint);
		canvas.drawText("Medium", (modeRects[5].left + modeRects[5].right) / 2, modeRects[5].top + (float)(0.82 * heightMode), paint);
		canvas.drawText("Random", (modeRects[6].left + modeRects[6].right) / 2, modeRects[6].top + (float)(0.46 * heightMode), paint);
		canvas.drawText("Expert", (modeRects[6].left + modeRects[6].right) / 2, modeRects[6].top + (float)(0.82 * heightMode), paint);
		canvas.drawText("Random", (modeRects[7].left + modeRects[7].right) / 2, modeRects[7].top + (float)(0.46 * heightMode), paint);
		canvas.drawText("Genius", (modeRects[7].left + modeRects[7].right) / 2, modeRects[7].top + (float)(0.82 * heightMode), paint);
		
	}
	
	
	
	// paint over the mode rectangles to display that they are locked
	private void paintLockRects(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		paint.setColor(MainGame.color_white);
		paint.setAlpha(140);
		
		// paint over the rects
		if(lockMed)
			canvas.drawRect(modeRects[2], paint);
		if(lockHard)
			canvas.drawRect(modeRects[3], paint);
		if(lockGenius)
			canvas.drawRect(modeRects[4], paint);
		if(lockMedRand)
			canvas.drawRect(modeRects[5], paint);
		if(lockHardRand)
			canvas.drawRect(modeRects[6], paint);
		if(lockGeniusRand)
			canvas.drawRect(modeRects[7], paint);
	}
	
	
	
	
	// message that takes up the entire screen
	private void displayLockMessage(String _line1, String _line2, String _line3, String _lineButton) {
		
		line1 = _line1;
		line2 = _line2;
		line3 = _line3;
		lineButton = _lineButton;
		
		messageOnScreen = true;
	}
	
	private void paintLockMessage(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(MainGame.tf);
		paint.setTextScaleX((float) 1.15);
		paint.setTextAlign(Paint.Align.CENTER);
		
		// paint rectangle
		paint.setColor(MainGame.color_white);
		paint.setAlpha(225);
		canvas.drawRect(0, 0, WIDTHactual, HEIGHT, paint);
		
		// paint message text
		
		int yMid = HEIGHT / 2, xMid = WIDTHactual / 2;
		int textSize = WIDTH / 10;
		paint.setTextSize(textSize);
		paint.setColor(MainGame.color_medium);
		paint.setAlpha(255);

		canvas.drawText(line1, xMid, yMid - textSize, paint);
		canvas.drawText(line2, xMid, yMid, paint);
		canvas.drawText(line3, xMid, yMid + textSize, paint);
		
		paint.setTextSize((float) (textSize * 1.3));		
		paint.setColor(Color.rgb(195,161,149));	
		
		canvas.drawText(lineButton, xMid, (float) (yMid + textSize * 2.5), paint);
	}

}
