package com.peternwerner.iagogame;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

import com.peternwerner.iagogame.MainGame;


/**
 * @author Peter Werner
 *
 * Handles the dots in the puzzle
 */
public class Dots {

	private static final String TAG = Dots.class.getSimpleName();
	
	// constants
	private int WIDTH;
	private int HEIGHT;
	private double root2 = Math.sqrt(2); 
	
	// dot variables
	private int widthThis;							// width of the square the dots occupy
	int distFromTop;						// distance from top of screen to top of the square the dots occupy
	int distFromLeft;						// distance from left of screen to left of the square the dots occupy
	private int n;									// matrix of dots is n X n (this is set when the Dots class is instantiated)
	private int nMax = 10;							// max value of n
	int[][] matrix = new int[nMax][nMax];			// the size of this array must >= max value of n
	int stepSize;							// space between origins of two dots
	int dotRadius;							// radius of a dot
	public int[] lastClick = {-1,-1};
	int colorGood	= MainGame.colors[MainGame.levelIndex % MainGame.colors.length];
	int colorBad 	= MainGame.color_medium;
	int colorOutline= MainGame.color_dark;
	
	// checkWin variables
	private static long timeStart, timeNow;
	private static long timeDelay = 850;	// delay time in milliseconds
	
	// checkWin controls
	boolean doDisableClick = false;
	boolean doCheckWin = true;
	boolean doPaintWinBurst = false;	
	
	
	
	// run when the object is first instantiated
	public void init() {
		
		// initialize class variables
		n = MainGame.n;
		WIDTH = MainGame.WIDTH;				HEIGHT = MainGame.HEIGHT;
		widthThis = MainGame.widthSquare;	distFromTop = MainGame.distFromTopSquare;
		distFromLeft = (int) ((WIDTH - widthThis) / 2);
		distFromLeft += MainGame.scaleOffset;
		stepSize = widthThis / n; 
		dotRadius = (int) (widthThis * (0.3) / n);
		
		// make sure our class object has been instantiated correctly
		if(n <= 0 || n > nMax) {
			// Log.d(TAG, "CRITICAL ERROR: n must be in range (0,10]");
		}
		
		// initialize the dots matrix (-1 is ignored, 0 is default bad dot, 1 will be good dot)
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				
				if(i < n && j < n)
					matrix[i][j] = 1;
				else
					matrix[i][j] = -1;
			}
		}
						
	}
	
	
	
	// run every time the thread updates
	public void update() {
		
	}
	
	
	/*
	 * functional methods
	 */
	
	
	// what we do when the user clicks on the screen...
	public boolean checkClick(float x, float y, Connectors _connectorsObj, Dots _dotsObj) {

		int i = -1, j = -1;
				
		// if clicking is disabled, return false
		if(doDisableClick)
			return false;
		
		// check if the click location is in the square which the dots occupy
		if(x >= distFromLeft && x <= WIDTH - distFromLeft && y >= distFromTop && y <= WIDTH + distFromTop) {
			
			// turn the coordinates into indices of the dot matrix, and flip the dot that exists at those indices
			i = (int) (x - distFromLeft) * n / widthThis;
			j = (int) (y - distFromTop) * n / widthThis;
			chooseDot(i, j, _connectorsObj, _dotsObj);

			// update score if user has clicked on a new dot (don't change score if the user clicks the same dot multiple times)
			if(i != lastClick[0]  ||  j != lastClick[1])
				MainGame.scoreNow++;
			lastClick[0] = i;
			lastClick[1] = j;
			
			return true;
		}
		
		return false;
	}
	
	// what we do when the user holds down on the screen...
	public boolean checkHold(float x, float y, Connectors _connectorsObj, Dots _dotsObj) {
	
		int i = -1, j = -1;
				
		// if clicking is disabled, return false
		if(doDisableClick)
			return false;
		
		// check if the click location is in the square which the dots occupy
		if(x >= distFromLeft && x <= WIDTH - distFromLeft && y >= distFromTop && y <= WIDTH + distFromTop) {
			
			// turn the coordinates into indices of the dot matrix, and flip the dot that exists at those indices
			i = (int) (x - distFromLeft) * n / widthThis;
			j = (int) (y - distFromTop) * n / widthThis;
			chooseDot(i, j, _connectorsObj, _dotsObj);
			
			// update score if user has clicked on a new dot (don't change score if the user clicks the same dot multiple times)
			if(i != lastClick[0]  ||  j != lastClick[1])
				MainGame.scoreNow++;
			lastClick[0] = i;
			lastClick[1] = j;
			
			return true;
		}
	
		return false;
	}
	
	
	
	// what we do with the dot the user selects...
	public void chooseDot(int i, int j, Connectors connectorsObj, Dots _dotsObj) {
		
		// flip this dot
		flipDot(i, j);
		
		// flip connected dots
		connectorsObj.flipConnectedDots(i, j, _dotsObj);
		
		// instantiate the tracer effect for this dot
		int index = MainGame.tracerList.size();
		MainGame.tracerList.add(new MoveTracer(index, _dotsObj, connectorsObj, i, j));
		
	}
	
	
	
	// flip one dot
	public void flipDot(int i, int j) {
		
		if(i >= 0 && j >= 0 && i < n && j < n) {
			
			switch(matrix[i][j]) {
			case 0:	matrix[i][j] = 1;
					break;
			case 1: matrix[i][j] = 0;
			}
		}
	}
	
	
	
	// check win condition and return boolean
	public boolean checkWin() {
				
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				
				// if we have any bad dots, return false (we have not won)
				if(matrix[i][j] == 0)
					return false;
			}
		}
		
		// only return true the first time the win condition is met
		if(!doCheckWin) {
			
			// play win sound
			if(MainGame.soundEnabled) {
				MainGame.doWinSound = true;
			}
			
			// move to the next state if it has been long enough since checkWin first returned true
			timeNow = System.currentTimeMillis();

			if(timeNow - timeStart >= timeDelay) {
				doPaintWinBurst = false;
				MainGame.doSuppressReset = false;
				
				// if we are in standard puzzle mode, show standard mode win screen
				if(MainGame.gameState == 1)
					MainGame.gameState = 2;
				// if we are in random puzzle mode, show random mode win screen
				if(MainGame.gameState == 5)
					MainGame.gameState = 6;
				
				// if we are in standard puzzle mode, record star rating...
				if(MainGame.gameState == 2  ||  MainGame.gameState == 1) {
					
					int starRating = 0;
					for(int i = 0; i < 3; i++) {
						if(MainGame.scoreNow <= MainGame.scoreRating[i])
							starRating++;
					}
					if(starRating > MainGame.scoreListStars[MainGame.n][MainGame.levelIndex])
						MainGame.scoreListStars[MainGame.n][MainGame.levelIndex] = starRating;
					
					// ... and record best amount of moves
					if(MainGame.scoreNow < MainGame.scoreBest  ||  MainGame.scoreBest == 0) {
						MainGame.scoreListBest[MainGame.n][MainGame.levelIndex] = MainGame.scoreNow;
					}
				}
			}
			
			return false;
		}
		doCheckWin = false;
		
		// if the win condition is met for the first time, store time stamp, disable clicking, paint screen burst, return true
		timeStart = System.currentTimeMillis();
		doDisableClick = true;
		doPaintWinBurst = true;
		MainGame.doSuppressReset = true;
		return true;
	}
	
	
	
	/*
	 * graphical methods
	 */
	
	
	// paint the square which the dots occupy
	public void paintSquare(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setColor(Color.rgb(200,200,200));
		canvas.drawRect(distFromLeft, distFromTop, distFromLeft + widthThis, distFromTop + widthThis, paint);
	}
	
	
	
	// paint the colored dots
	public void paintDots(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
								
				int x = distFromLeft + (stepSize / 2) + (i * stepSize);
				int y = distFromTop  + (stepSize / 2) + (j * stepSize);
								
				paint.setColor(colorOutline);
				if(matrix[i][j] != -1)
					canvas.drawCircle(x, y, (float) (1.15 * dotRadius), paint);
				
				switch(matrix[i][j]) {
				case 0:	paint.setColor(colorBad);
						break;
				case 1: paint.setColor(colorGood);
				}
				
				if(matrix[i][j] != -1)
					canvas.drawCircle(x, y, (float) (dotRadius), paint);
			}
		}
	}
	
	
	
	// paints the brief burst of color that occurs when the user wins
	public void paintWinBurst(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setColor(colorGood);
		
		int alpha = (int) (255 * (1.0 - ((float)(timeNow - timeStart) / timeDelay)));
		paint.setAlpha(alpha);
		
		canvas.drawRect(distFromLeft, distFromTop, distFromLeft + widthThis, distFromTop + widthThis, paint);
	}
	
	
	
	// randomize dots
	public void randomizeDots(int numMoves, Connectors _connectorsObj, Dots _dotsObj) {
		
		// reset all dots to '1' 
		// then randomly swap some to '0'
		
		Random rn = new Random();
		
		for(int i = 0; i < matrix.length  &&  i != -1; i++) {
			for(int j = 0; j < matrix[0].length  &&  j != -1; j++) {
				matrix[i][j] = 1;
			}
		}
		
		for(int k = 0; k < numMoves; k++) {
			int r1 = (int)(rn.nextInt() % n);
			int r2 = (int)(rn.nextInt() % n);
			chooseDot(Math.abs(r1), Math.abs(r2), _connectorsObj, _dotsObj);
		}
		
		// make sure at least one of the dots is bad
		boolean foundBad = false;
		
		for(int i = 0; i < matrix.length  &&  i != -1; i++) {
			for(int j = 0; j < matrix[0].length  &&  j != -1; j++) {
				
				if(matrix[i][j] == 0)
					foundBad = true;
			}
		}
		
		// if all the dots are good, randomly flip one dot
		if(foundBad == false) {
			int r1 = (int)(rn.nextInt() % n);
			int r2 = (int)(rn.nextInt() % n);
			chooseDot(Math.abs(r1), Math.abs(r2), _connectorsObj, _dotsObj);
		}
		
		// store this info for resets
		storeForReset();
		
		// randomize color
		MainGame.levelIndex = (int) Math.abs(rn.nextInt() % MainGame.colors.length);
	}
	
	
	// load random puzzle data
	public void loadRandom() {
		
		for(int i = 0; i < MainGame.matrix_Storage.length; i++)
			for(int j = 0; j < MainGame.matrix_Storage[i].length; j++)
				matrix[i][j] = MainGame.matrix_Storage[i][j];
	}
	
	
	
	// store this info for resets
	public void storeForReset() {
		
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				MainGame.matrix_Storage[i][j] = matrix[i][j];
	}
}
