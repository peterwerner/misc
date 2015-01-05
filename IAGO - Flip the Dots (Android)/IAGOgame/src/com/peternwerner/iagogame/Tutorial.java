package com.peternwerner.iagogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;

public class Tutorial {

	private static final String TAG = Tutorial.class.getSimpleName();

	// constants
	private int WIDTH, WIDTHactual;
	private int HEIGHT;
	
	private int spaceTop, widthRibbon, widthSquare, widthPages, buffer, widthThird, heightMode, spacer;
	private int scaleOffset = MainGame.scaleOffset;
	
	// paths
	private int xPtsRibbons[][] = new int[3][4];
	private int yPtsRibbons[][] = new int[3][4];

	private boolean canProceed = false;
	private int step = -1;
	
	private int countClicks = 0;
	
	Dots dotsObj = null;	
	Connectors connectorsObj = null;
	
	

	//run when the object is first instantiated
	public void init(Dots dotsObj, Connectors connectorsObj) {
	
		// initialize variables
		
		this.dotsObj = dotsObj;
		this.connectorsObj = connectorsObj;
		
		MainGame.n = 3;
		dotsObj.init();
		connectorsObj.init();
		
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

		nextStep();
	}
	
	
	
	// what we do when the user clicks on the screen...
	public boolean checkClick(float x, float y) {
	
		// user clicks on the proceed button
		if(y >= yPtsRibbons[1][0]  &&  y <= yPtsRibbons[1][2]) {
			
			countClicks++;
			
			if(canProceed)
				nextStep();
			return true;
		}
		
		// user clicks on dots
		if(dotsObj != null  &&  connectorsObj != null  &&  dotsObj.checkClick(x, y, connectorsObj, dotsObj)) {
			
			countClicks++;
			
			if(checkWin())
				canProceed = true;
		}
		
		return false;
	}
	
	
	// paint everything
	public void paint(Canvas canvas) {
				
		paintRibbons(canvas);
		paintText(canvas);
		connectorsObj.paintConnectors(canvas);
		dotsObj.paintDots(canvas);
		
		for(int i = 0; i < MainGame.tracerList.size(); i++)
			MainGame.tracerList.get(i).paint(canvas);
	}
	
	
	// paint ribbons (for each step)
	private void paintRibbons(Canvas canvas) {
		
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
	
	
	// paint text
	private void paintText(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(MainGame.tf);
		paint.setTextScaleX((float) 1.15);
		paint.setTextAlign(Paint.Align.CENTER);
		
		// paint ribbon text
		
		paint.setTextSize(widthRibbon);
		paint.setColor(MainGame.color_white);
		
		canvas.drawText("How to Play", WIDTHactual / 2, (float) (yPtsRibbons[0][2] - (0.14 * widthRibbon)), paint);
		if(!canProceed)
			paint.setColor(MainGame.color_medium);
		canvas.drawText("I'm Ready", WIDTHactual / 2, (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
		
		int textSize = WIDTH * 2 / 21;
		
		// paint first step text
		if(step == 0) {
			
			paint.setTextSize(textSize);
			paint.setColor(MainGame.color_medium);
			canvas.drawText("Touch a dot to toggle", WIDTHactual / 2, spaceTop + (float) (textSize * 1.7), paint);
			canvas.drawText("on (color) and off (grey)", WIDTHactual / 2, spaceTop + (float) (textSize * 2.6), paint);
			paint.setTextSize((float) (textSize * 1.15));
			paint.setColor(Color.rgb(195,161,149));			
			if(!canProceed)
				canvas.drawText("Try it out!", WIDTHactual / 2, spaceTop + (float) (textSize * 4.2), paint);
			else
				canvas.drawText("You've got it!", WIDTHactual / 2, spaceTop + (float) (textSize * 4.2), paint);
			paint.setTextSize(textSize);
			paint.setColor(MainGame.color_medium);			
			canvas.drawText("A puzzle is solved when", WIDTHactual / 2, spaceTop + (float) (textSize * 8.6), paint);
			canvas.drawText("All dots are colored", WIDTHactual / 2, spaceTop + (float) (textSize * 9.5), paint);
		}
		
		// paint second step text
		else if(step == 1) {
			
			paint.setTextSize(textSize);
			paint.setColor(MainGame.color_medium);
			canvas.drawText("Dots that are connected", WIDTHactual / 2, spaceTop + (float) (textSize * 1.7), paint);
			canvas.drawText("Toggle with each other", WIDTHactual / 2, spaceTop + (float) (textSize * 2.6), paint);
			paint.setTextSize((float) (textSize * 1.15));
			paint.setColor(Color.rgb(195,161,149));			
			if(!canProceed)
				canvas.drawText("Try it out!", WIDTHactual / 2, spaceTop + (float) (textSize * 4.2), paint);
			else
				canvas.drawText("Get it?", WIDTHactual / 2, spaceTop + (float) (textSize * 4.2), paint);		}
	}
	
	
	
	// move to the next step
	private void nextStep() {
		
		countClicks = 0;
		
		// start first step
		if(step < 0) {
			
			resetPuzzle();
			
			dotsObj.matrix[0][1] = 0;
			dotsObj.matrix[1][1] = 1;
			dotsObj.matrix[2][1] = 0;

			step = 0;
		}
		
		// start second step
		else if(step == 0) {
			
			resetPuzzle();
			dotsObj.matrix[0][1] = 1;
			dotsObj.matrix[0][2] = 1;
			connectorsObj.connectorsVertical[0][1] = 1;
			dotsObj.matrix[1][1] = 0;
			dotsObj.matrix[2][1] = 0;
			dotsObj.matrix[1][2] = 0;
			dotsObj.matrix[2][2] = 0;
			connectorsObj.connectorsHorizontal[1][1] = 1;
			connectorsObj.connectorsHorizontal[1][2] = 1;
			connectorsObj.connectorsVertical[1][1] = 1;
			connectorsObj.connectorsVertical[2][1] = 1;
			
			step = 1;
		}
		
		// tutorial is complete
		else if(step > 0) {
			
			resetPuzzle();
			MainGame.gameState = 4;
			MainGame.didTutorial = true;
		}
		
		canProceed = false;
	}
	
	
	
	// check if we can move to next step
	private boolean checkWin() {
				
		// second step: click 2 dots
		if(step == 1) {
			if(countClicks >= 2)
				return true;
			return false;
		}
		
		// first step: all dots must be colored
		for(int i = 0; i < dotsObj.matrix.length; i++) {
			for(int j = 0; j < dotsObj.matrix[i].length; j++) {
				if(dotsObj.matrix[i][j] == 0)
					return false;
			}
		}
		return true;
	}
	
	
	// reset all puzzle values to -1
	private void resetPuzzle() {
		
		setAllMatrixValues(dotsObj.matrix, -1);
		setAllMatrixValues(connectorsObj.connectorsDiagDown, -1);
		setAllMatrixValues(connectorsObj.connectorsDiagUp, -1);
		setAllMatrixValues(connectorsObj.connectorsHorizontal, -1);
		setAllMatrixValues(connectorsObj.connectorsVertical, -1);
	}
	
	// helper method for resetPuzzle
	private void setAllMatrixValues(int[][] matrix, int value) {
		
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = value;
			}
		}
	}
	
}