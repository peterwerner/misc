package com.peternwerner.iagogame;

import com.peternwerner.iagogame.MainGame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.util.Log;

/*
 * Handles the GUI in puzzle mode
 */
public class PuzzleInterface {

	private static final String TAG = PuzzleInterface.class.getSimpleName();
	
	// constants
	private int WIDTH;
	private int HEIGHT;
	private int scaleOffset = MainGame.scaleOffset;
	private int WIDTHactual = MainGame.WIDTHactual;

	
	// class variables
	private int n;
	private int spaceTop;
	private int widthRibbon, widthScore, widthSquare;
	private int xPoints[][] = new int[7][4];	// [0][n] to [3][n] are reserved for the ribbons...
	private int yPoints[][] = new int[7][4];	// ...[4][n] to [6][n] are reserved for the win screen art
	private int xPtsRibbons[][] = new int[3][4];	// reserved for the win screen ribbons
	private int yPtsRibbons[][] = new int[3][4];
	int color = MainGame.color_dark;
	int colorGood = MainGame.colors[MainGame.levelIndex % MainGame.colors.length];
	private String levelName;
	
	// controls
	static boolean doSuppressReset;

	
	
	// run when the object is first instantiated
	public void init(String _levelName) {
		
		n = MainGame.n;						levelName = _levelName;
		WIDTH = MainGame.WIDTH;				HEIGHT = MainGame.HEIGHT;
		spaceTop = MainGame.distFromTopSquare;
		widthSquare = MainGame.widthSquare;
		widthRibbon = (int) (0.065625 * HEIGHT);
		widthScore = (int) (0.0520833 * HEIGHT);
		doSuppressReset = false;
		MainGame.doSuppressReset = false;
	
		int spacer = (int) (0.5 * (spaceTop - widthRibbon - widthScore));
		
		// Top ribbon
		xPoints[0][0] = 0;							yPoints[0][0] = spacer;
		xPoints[0][1] = WIDTHactual;				yPoints[0][1] = spacer;
		xPoints[0][2] = WIDTHactual;				yPoints[0][2] = widthRibbon + spacer;
		xPoints[0][3] = 0;							yPoints[0][3] = widthRibbon + spacer;
		
		// Sub ribbons
		xPoints[1][0] = 0;								yPoints[1][0] = spaceTop + widthSquare + spacer;
		xPoints[1][1] = (int) (0.424074 * WIDTHactual);	yPoints[1][1] = spaceTop + widthSquare + spacer;
		xPoints[1][2] = (int) (0.479629 * WIDTHactual);	yPoints[1][2] = widthRibbon + spaceTop + widthSquare + spacer;
		xPoints[1][3] = 0;								yPoints[1][3] = widthRibbon + spaceTop + widthSquare + spacer;
		xPoints[2][0] = WIDTHactual;					yPoints[2][0] = spaceTop + widthSquare + spacer;
		xPoints[2][1] = (int) (0.520370 * WIDTHactual);	yPoints[2][1] = spaceTop + widthSquare + spacer;
		xPoints[2][2] = (int) (0.575925 * WIDTHactual);	yPoints[2][2] = widthRibbon + spaceTop + widthSquare + spacer;
		xPoints[2][3] = WIDTHactual;					yPoints[2][3] = widthRibbon + spaceTop + widthSquare + spacer;

		// Bottom ribbon
		xPoints[3][0] = 0;							yPoints[3][0] = yPoints[2][2] + spacer;
		xPoints[3][1] = WIDTHactual;				yPoints[3][1] = yPoints[2][3] + spacer;
		xPoints[3][2] = WIDTHactual;				yPoints[3][2] = HEIGHT;
		xPoints[3][3] = 0;							yPoints[3][3] = HEIGHT;
		
		int buffer = MainGame.distBufferSide;
		int sizeSub = (MainGame.widthSquare - buffer * 2) / 3;
		// Middle win sub ribbon (menu button)
		xPtsRibbons[0][0] = (WIDTH - sizeSub) / 2;		yPtsRibbons[0][0] = spaceTop + MainGame.widthSquare + spacer;
		xPtsRibbons[0][1] = (WIDTH + sizeSub) / 2;		yPtsRibbons[0][1] = spaceTop + MainGame.widthSquare + spacer;
		xPtsRibbons[0][2] = (WIDTH + sizeSub) / 2;		yPtsRibbons[0][2] = widthRibbon + spaceTop + MainGame.widthSquare + spacer;
		xPtsRibbons[0][3] = (WIDTH - sizeSub) / 2;		yPtsRibbons[0][3] = widthRibbon + spaceTop + MainGame.widthSquare + spacer;
		// Side win sub ribbons (next and redo buttons)
		xPtsRibbons[1][0] = buffer;						yPtsRibbons[1][0] = yPtsRibbons[0][0];
		xPtsRibbons[1][1] = xPtsRibbons[0][0] - buffer;	yPtsRibbons[1][1] = yPtsRibbons[0][1];
		xPtsRibbons[1][2] = xPtsRibbons[0][0] - buffer;	yPtsRibbons[1][2] = yPtsRibbons[0][2];
		xPtsRibbons[1][3] = buffer;						yPtsRibbons[1][3] = yPtsRibbons[0][3];
		xPtsRibbons[2][0] = WIDTH - buffer;				yPtsRibbons[2][0] = yPtsRibbons[0][0];
		xPtsRibbons[2][1] = xPtsRibbons[0][1] + buffer;	yPtsRibbons[2][1] = yPtsRibbons[0][1];
		xPtsRibbons[2][2] = xPtsRibbons[0][1] + buffer;	yPtsRibbons[2][2] = yPtsRibbons[0][2];
		xPtsRibbons[2][3] = WIDTH - buffer;				yPtsRibbons[2][3] = yPtsRibbons[0][3];
				
		for(int i = 0; i < 3; i++) 
			for(int j = 0; j < xPtsRibbons[i].length; j++) 
				xPtsRibbons[i][j] += scaleOffset;
		
		spacer = (int)(.25 * widthRibbon);
		float scalar = ((float)widthSquare / WIDTH) * (float)(33.75 / 31.75);
		
		// Top color strip
		xPoints[4][0] = 0;				yPoints[4][0] = spaceTop;
		xPoints[4][1] = WIDTHactual;	yPoints[4][1] = spaceTop;
		xPoints[4][2] = WIDTHactual;	yPoints[4][2] = spaceTop + (int)(widthRibbon * scalar);
		xPoints[4][3] = 0;				yPoints[4][3] = spaceTop + (int)(widthRibbon * scalar);
		
		// Bottom color strip
		xPoints[5][0] = 0;				yPoints[5][0] = spaceTop + widthSquare - (int)(widthRibbon * scalar);
		xPoints[5][1] = WIDTHactual;	yPoints[5][1] = spaceTop + widthSquare - (int)(widthRibbon * scalar);
		xPoints[5][2] = WIDTHactual;	yPoints[5][2] = spaceTop + widthSquare;
		xPoints[5][3] = 0;				yPoints[5][3] = spaceTop + widthSquare;
		
		// Background rectangle
		xPoints[6][0] = 0;				yPoints[6][0] = spaceTop + (int)(widthRibbon * scalar) + spacer;
		xPoints[6][1] = WIDTHactual;	yPoints[6][1] = spaceTop + (int)(widthRibbon * scalar) + spacer;
		xPoints[6][2] = WIDTHactual;	yPoints[6][2] = spaceTop + widthSquare - ((int)(widthRibbon * scalar) + spacer);
		xPoints[6][3] = 0;				yPoints[6][3] = spaceTop + widthSquare - ((int)(widthRibbon * scalar) + spacer);
		
	}
	
	
	// run every time the thread updates
	public void update() {
		
		doSuppressReset = MainGame.doSuppressReset;
	}
	
	
	
	/*
	 * functional methods
	 */

	
	// what we do when the user clicks on the screen...
	public boolean checkClick(float x, float y) {
		
		if(y > Math.abs(yPoints[1][0])  &&  y < Math.abs(yPoints[1][2])) {
			
			// if the user clicks on the menu button
			if(!doSuppressReset  &&  (MainGame.gameState == 1  ||  MainGame.gameState == 5)  &&  x > 0  &&  x < Math.abs(xPoints[1][2])) {
				// Log.d(TAG, "Menu button clicked");
				
				if(MainGame.gameState == 1)
					MainGame.gameState = 3;
				else if(MainGame.gameState == 5)
					MainGame.gameState = 4;

				return true;
			}
			
			// if the user clicks on the reset button
			if(!doSuppressReset  &&  (MainGame.gameState == 1  ||  MainGame.gameState == 5)  &&  x < WIDTH  &&  x > xPoints[2][1]) {
				// Log.d(TAG, "Reset button clicked");
				
				// load puzzle from file if we are in standard puzzle mode
				if(MainGame.gameState == 1) {
					MainGame.doLoadPuzzle = true;
				}
				
				// load puzzle from temporarily stored data if we are in random mode
				if(MainGame.gameState == 5) {
					MainGame.doLoadRandomData = true;
				}
				
				return true;
			}
			
			// if the user clicks on the win -> redo button
			if((MainGame.gameState == 2  ||  MainGame.gameState == 6)  &&   x > 0  &&  x < Math.abs(xPtsRibbons[1][2])) {
				// Log.d(TAG, "Redo button clicked");
				
				// load puzzle from file if we are in standard puzzle mode
				if(MainGame.gameState == 2) {
					MainGame.gameState = 1;
					MainGame.doLoadPuzzle = true;
				}
				
				// load puzzle from temporarily stored data if we are in random mode
				if(MainGame.gameState == 6) {
					MainGame.gameState = 5;
					MainGame.doLoadRandomData = true;
				}

				return true;
			}
			
			// if the user clicks on the win -> next button
			if((MainGame.gameState == 2  ||  MainGame.gameState == 6)  &&  x < WIDTH  &&  x > xPtsRibbons[2][1]) {
				// Log.d(TAG, "Next button clicked");
				
				// move to next puzzle file if in standard mode
				if(MainGame.gameState == 2) {
					// move to next puzzle if one is available
					if(MainGame.levelIndex + 1 < MainGame.levelListSize[n]) {
						// Log.d(TAG, "Loading next level");
						MainGame.levelIndex++;
						MainGame.gameState = 1;
						MainGame.doLoadPuzzle = true;
					}
					// otherwise, return to puzzle menu
					else {
						MainGame.gameState = 3;
					}
				}
				
				// randomly generate a new puzzle if in random mode
				if(MainGame.gameState == 6){
					MainGame.doRandomizePuzzle = true;
					MainGame.doLoadRandomData = true;
					MainGame.gameState = 5;
				}

				return true;
			}
			
			// if the user clicks on the win -> menu button
			if((MainGame.gameState == 2  ||  MainGame.gameState == 6)  &&  x < xPtsRibbons[0][1]  &&  x > xPtsRibbons[0][0]) {
				// Log.d(TAG, "Menu button clicked");
				
				if(MainGame.gameState == 2)
					MainGame.gameState = 3;
				else if(MainGame.gameState == 6)
					MainGame.gameState = 4;

				return true;
			}
			
		}
		
		return false;
	}	
		
			
	
	/*
	 * graphical methods
	 */
	
	
	// paint the ribbons
	public void paintRibbons(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		
		for(int i = 0; i < 4; i++) {
			
			// don't paint sub ribbons if in win screen state
			if((MainGame.gameState == 2  ||  MainGame.gameState == 6)  &&  (i == 1  ||  i == 2))
				;
			else {
				Path path = new Path();
				path.moveTo(xPoints[i][0], yPoints[i][0]);
				
				for(int j = 1; j < 4; j++) {
					
					path.lineTo(xPoints[i][j], yPoints[i][j]);
				}
				path.close();
			
				canvas.drawPath(path, paint);
			}
		}
		
	}
	
	
	
	// paint the text on top of the ribbons
	public void paintText(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(MainGame.color_white);
		paint.setTypeface(MainGame.tf);
		paint.setTextScaleX((float) 1.15);
		
		// header text
		paint.setTextSize(widthRibbon);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(levelName, WIDTHactual / 2, (float) (yPoints[0][2] - (0.14 * widthRibbon)), paint);
		
		// puzzle mode text
		if(MainGame.gameState == 1  ||  MainGame.gameState == 5) {
			if(doSuppressReset)
				paint.setColor(MainGame.color_medium);
			canvas.drawText("Menu", (float) ((float) ((xPtsRibbons[1][0] + xPtsRibbons[1][1]) /2) + (2 * 0.14 * widthRibbon)), (float) (yPoints[1][2] - (0.14 * widthRibbon)), paint);
			canvas.drawText("Reset", WIDTHactual - (float) (xPoints[1][1] / 1.9), (float) (yPoints[1][2] - (0.14 * widthRibbon)), paint);
		}	
		
		// win screen text
		else if(MainGame.gameState == 2  ||  MainGame.gameState == 6) {
			canvas.drawText("Menu", (float) ((xPtsRibbons[0][0] + xPtsRibbons[0][1]) /2), (float) (yPtsRibbons[0][2] - (0.14 * widthRibbon)), paint);
			canvas.drawText("Redo", (float) ((xPtsRibbons[1][0] + xPtsRibbons[1][1]) /2 + (0.14 * widthRibbon)), (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
			canvas.drawText("Next", (float) ((xPtsRibbons[2][0] + xPtsRibbons[2][1]) /2 - (0.14 * widthRibbon)), (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
		}
			
		// score text
		paint.setColor(color);
		paint.setTextSize((float) (0.85 * widthRibbon));
		paint.setTextAlign(Paint.Align.LEFT);
		if(MainGame.gameState == 1  ||  MainGame.gameState == 2  ||  MainGame.gameState == 5  ||  MainGame.gameState == 6)
			canvas.drawText("Moves: " + MainGame.scoreNow, (WIDTHactual - widthSquare) / 2, spaceTop - (WIDTH - widthSquare) / 2, paint);
		
		paint.setTextAlign(Paint.Align.RIGHT);
		if(MainGame.gameState == 1  ||  MainGame.gameState == 2) {
			if(MainGame.scoreBest > 0)
				canvas.drawText("Best: " + MainGame.scoreBest, WIDTHactual - (WIDTHactual - widthSquare) / 2, spaceTop - (WIDTH - widthSquare) / 2, paint);
			else
				canvas.drawText("Best: --", WIDTHactual - (WIDTHactual - widthSquare) / 2, spaceTop - (WIDTH - widthSquare) / 2, paint);			
		}
		
	}
	
	
	
	// paint the win screen ribbons
	public void paintWinRibbons(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(colorGood);

		for(int i = 4; i < 7; i++) {
			
			if(i == 6)
				paint.setColor(color);
			
			Path path = new Path();
			path.moveTo(xPoints[i][0], yPoints[i][0]);
			
			for(int j = 1; j < 4; j++) {
				
				path.lineTo(xPoints[i][j], yPoints[i][j]);
			}
			path.close();
			
			canvas.drawPath(path, paint);
		}
		
		// paint the win screen sub ribbons
		paint.setColor(color);
		
		for(int i = 0; i < xPtsRibbons.length; i++) {
			
			Path path = new Path();
			path.moveTo(xPtsRibbons[i][0], yPtsRibbons[i][0]);
			
			for(int j = 1; j < 4; j++) {
				
				path.lineTo(xPtsRibbons[i][j], yPtsRibbons[i][j]);
			}
			path.close();
			
			canvas.drawPath(path, paint);
		}
		
		// now paint triangles over the sub ribbons to make them appear angled
		
		paint.setColor(MainGame.color_white);
		
		// left ribbon triangles
		Path path = new Path();
		path.moveTo(xPtsRibbons[1][0], yPtsRibbons[1][0]);
		path.lineTo(xPtsRibbons[1][0] + widthRibbon / 3, yPtsRibbons[1][0]);
		path.lineTo(xPtsRibbons[1][0], yPtsRibbons[1][0] + widthRibbon / 2);
		path.close(); 						   canvas.drawPath(path, paint);
		path = new Path();
		path.moveTo(xPtsRibbons[1][3], yPtsRibbons[1][3]);
		path.lineTo(xPtsRibbons[1][3] + widthRibbon / 3, yPtsRibbons[1][3]);
		path.lineTo(xPtsRibbons[1][3], yPtsRibbons[1][3] - widthRibbon / 2);
		path.close(); 						   canvas.drawPath(path, paint);
		// right ribbon triangles
		path = new Path();
		path.moveTo(xPtsRibbons[2][0], yPtsRibbons[2][0]);
		path.lineTo(xPtsRibbons[2][0] - widthRibbon / 3, yPtsRibbons[2][0]);
		path.lineTo(xPtsRibbons[2][0], yPtsRibbons[2][0] + widthRibbon / 2);
		path.close(); 						   canvas.drawPath(path, paint);
		path = new Path();
		path.moveTo(xPtsRibbons[2][3], yPtsRibbons[2][3]);
		path.lineTo(xPtsRibbons[2][3] - widthRibbon / 3, yPtsRibbons[2][3]);
		path.lineTo(xPtsRibbons[2][3], yPtsRibbons[2][3] - widthRibbon / 2);
		path.close(); 						   canvas.drawPath(path, paint);
	}
	
	
	
	// paint the text and stars for the win screen
	public void paintWinText(Canvas canvas, String s) {

		// paint a comment
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(MainGame.color_white);
		paint.setTypeface(MainGame.tf);
		paint.setTextSize((float) (1.2 * widthRibbon));
		paint.setTextScaleX((float) 1.15);
		paint.setTextAlign(Paint.Align.CENTER);

		canvas.drawText(s, WIDTHactual / 2, spaceTop + (int)(.73 * widthSquare), paint);
		
		// paint 3 stars
		
		paint.setColor(MainGame.color_white);
		
		int w = (int)((widthSquare / 3) * 0.8);
		int h = (int)(.95 * w);
		int spacer = (int)((WIDTH - (3 * w)) / 4);
		
		for(int i = 0; i < 3; i++) {
			if(MainGame.scoreNow > MainGame.scoreRating[i])
				paint.setColor(MainGame.color_medium);
			int x = (w * i) + (spacer * (i + 1));
			x += MainGame.scaleOffset;
			drawStar(canvas, paint, x, spaceTop + (int)(.27 * widthSquare), h, w);
		}
		
	}
	
	
	// draw a star with the given coordinates and size
	public void drawStar(Canvas canvas, Paint paint, int x, int y, int h, int w) {
		
		int[] xPts = new int[10], yPts = new int[10];
		
		xPts[0] = x + 0;				yPts[0] = y+ (int)(.383 * h);
		xPts[1] = x + (int)(.367 * w);	yPts[1] = y+ (int)(.361 * h);
		xPts[2] = x + (int)(0.50 * w);	yPts[2] = y+ 0;
		xPts[3] = x + (int)(.633 * w);	yPts[3] = y+ (int)(.361 * h);
		xPts[4] = x + w;				yPts[4] = y+ (int)(.383 * h);
		xPts[5] = x + (int)(.716 * w);	yPts[5] = y+ (int)(.625 * h);
		xPts[6] = x + (int)(.812 * w);	yPts[6] = y+ h;
		xPts[7] = x + (int)(0.50 * w);	yPts[7] = y+ (int)(.791 * h);
		xPts[8] = x + (int)(.188 * w);	yPts[8] = y+ h;
		xPts[9] = x + (int)(.284 * w);	yPts[9] = y+ (int)(.625 * h);	
		
		Path path = new Path();
		path.moveTo(xPts[0], yPts[0]);
		
		for(int i = 1; i < 10; i++) {
			
			path.lineTo(xPts[i], yPts[i]);
		}
		path.close();
		
		canvas.drawPath(path, paint);
	}
	
}
