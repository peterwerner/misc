package com.peternwerner.iagogame;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.peternwerner.iagogame.MainGame;

/**
 * @author Peter Werner
 *
 * Handles the connectors in the puzzle
 */
public class Connectors {

	private static final String TAG = Connectors.class.getSimpleName();
	
	// constants
	private int WIDTH;
	private int HEIGHT;
	private double root2 = Math.sqrt(2); 
	
	// dot variables
	private int dotRadius;							// radius of a dot
	int stepSize;							// space between origins of two dots
	
	// connector variables
	private int widthSquare;						// width of the square the dots occupy
	int distFromTop;						// distance from top of screen to top of the square the dots occupy
	int distFromLeft;						// distance from left of screen to left of the square the dots occupy
	private int n;									// matrix of dots is n X n (this is set when the Dots class is instantiated)
	private int nMax = 10;							// max value of n
	int[][] connectorsHorizontal = new int[nMax - 1][nMax];
	int[][] connectorsVertical   = new int[nMax][nMax - 1];
	int[][] connectorsDiagDown   = new int[nMax - 1][nMax - 1];
	int[][] connectorsDiagUp     = new int[nMax - 1][nMax - 1];
	private int colorConnectors = MainGame.color_dark;
	int connectorWidth;
	
	
	
	// run when the object is first instantiated
	public void init() {
		
		// initialize class variables
		n = MainGame.n;
		WIDTH = MainGame.WIDTH;				HEIGHT = MainGame.HEIGHT;
		widthSquare = MainGame.widthSquare;	distFromTop = MainGame.distFromTopSquare;
		distFromLeft = (int) ((WIDTH - widthSquare) / 2);
		distFromLeft += MainGame.scaleOffset;
		dotRadius = (int) (widthSquare * (0.3) / n);
		stepSize = (int) (widthSquare / n);
		connectorWidth = 2 * (int)(dotRadius / 5);
		
		// make sure our class object has been instantiated correctly
		if(n <= 0 || n > nMax) {
			// Log.d(TAG, "CRITICAL ERROR: n must be in range (0,10]");
		}
		
		// initialize all the connector matrices (-1 is ignored, 0 is default unconnected, 1 will be connected)
		for(int i = 0; i < connectorsHorizontal.length; i++) {
			for(int j = 0; j < connectorsHorizontal[0].length; j++) {
				if(i < n - 1 && j < n)
					connectorsHorizontal[i][j] = 0;
				else
					connectorsHorizontal[i][j] = -1;
			}
		}
		for(int i = 0; i < connectorsVertical.length; i++) {
			for(int j = 0; j < connectorsVertical[0].length; j++) {
				if(i < n && j < n - 1)
					connectorsVertical[i][j] = 0;
				else
					connectorsVertical[i][j] = -1;
			}
		}
		for(int i = 0; i < connectorsDiagDown.length; i++) {
			for(int j = 0; j < connectorsDiagDown[0].length; j++) {
				if(i < n - 1 && j < n - 1) {
					connectorsDiagDown[i][j] = 0;
					connectorsDiagUp[i][j]   = 0;
				}
				else {
					connectorsDiagDown[i][j] = -1;
					connectorsDiagUp[i][j]   = -1;
				}
			}
		}

	}
	
	
	// run every time the thread updates
	public void update() {
		
	}
	
	
	/*
	 * functional methods
	 */
	
	
	// flip dots connected to a given dot
	public void flipConnectedDots(int i, int j, Dots dotsObj) {
				
		/*
		 * 	UNCOMMENT THE 'else' LINES FOR CONNECTIONS TO BE MADE REGARDLESS OF DISTANCE
		 *  RIGHT NOW, ONLY DOTS THAT ARE DIRECTLY CONNECTED CAN INFLUENCE EACH OTHER
		 */
		
		// check horizontal (to the right / +) connections, and flip dots accordingly
		boolean keepGoing = true;	
		int i2 = i,	j2 = j;
		while(keepGoing == true  &&  i2 < n - 1) {
			if(connectorsHorizontal[i2][j2] == 1)
				dotsObj.flipDot(i2 + 1, j2);
			// else
				keepGoing = false;
			i2++;
		}
		// check horizontal (to the left / -) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 > 0) {
			if(connectorsHorizontal[i2 - 1][j2] == 1)
				dotsObj.flipDot(i2 - 1, j2);
			// else
				keepGoing = false;
			i2--;
		}
		// check vertical (in the down / + direction) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  j2 < n - 1) {
			if(connectorsVertical[i2][j2] == 1)
				dotsObj.flipDot(i2, j2 + 1);
			// else
				keepGoing = false;
			j2++;
		}
		// check vertical (in the up / - direction) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  j2 > 0) {
			if(connectorsVertical[i2][j2 - 1] == 1)
				dotsObj.flipDot(i2, j2 - 1);
			// else
				keepGoing = false;
			j2--;
		}
		// check diagonal down (in + direction) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 < n - 1  &&  j2 < n - 1) {
			if(connectorsDiagDown[i2][j2] == 1)
				dotsObj.flipDot(i2 + 1, j2 + 1);
			// else
				keepGoing = false;
			i2++;	j2++;
		}
		// check diagonal down (in - directions) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 > 0  &&  j2 > 0) {
			if(connectorsDiagDown[i2 - 1][j2 - 1] == 1)
				dotsObj.flipDot(i2 - 1, j2 - 1);
			// else
				keepGoing = false;
			i2--;	j2--;
		}
		// check diagonal up (in + direction) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 < n - 1  &&  j2 < n  &&  j2 > 0) {
			if(connectorsDiagUp[i2][j2 - 1] == 1)
				dotsObj.flipDot(i2 + 1, j2 - 1);
			// else
				keepGoing = false;
			i2++;	j2--;
		}
		// check diagonal up (in - directions) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 > 0  &&  j2 >= 0  &&  i2 > 0) {
			if(connectorsDiagUp[i2 - 1][j2] == 1)
				dotsObj.flipDot(i2 - 1, j2 + 1);
			// else
				keepGoing = false;
			i2--;	j2++;
		}
	}
	
	
	
	/*
	 * graphical methods
	 */
	
	
	// paint the connectors between dots
	public void paintConnectors(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		float hw = connectorWidth / 2;	// half the width of a connector
		paint.setColor(colorConnectors);
		
		// paint horizontal connectors
		for(int i = 0; i < connectorsHorizontal.length; i++) {
			for(int j = 0; j < connectorsHorizontal[0].length; j++) {

				int x = distFromLeft + (stepSize / 2) + (i * stepSize);
				int y = distFromTop  + (stepSize / 2) + (j * stepSize);
				if(connectorsHorizontal[i][j] == 1)
					canvas.drawRect(x - hw, y - hw, stepSize + x - hw, connectorWidth + y - hw, paint);
			}	
		}
		// paint vertical connectors
		for(int i = 0; i < connectorsVertical.length; i++) {
			for(int j = 0; j < connectorsVertical[0].length; j++) {

				int x = distFromLeft + (stepSize / 2) + (i * stepSize);
				int y = distFromTop  + (stepSize / 2) + (j * stepSize);
				if(connectorsVertical[i][j] == 1)
					canvas.drawRect(x - hw, y - hw, connectorWidth + x - hw, stepSize + y - hw, paint);
			}	
		}
		
		hw /= root2;	// for the diagonal connectors we require the width * sin(45deg), cos(45deg)
		
		
		// paint diagonal down connectors
		for(int i = 0; i < connectorsDiagDown.length; i++) {
			for(int j = 0; j < connectorsDiagDown[0].length; j++) {

				int x = distFromLeft + (stepSize / 2) + (i * stepSize);
				int y = distFromTop  + (stepSize / 2) + (j * stepSize);
				if(connectorsDiagDown[i][j] == 1) {
					Path path = new Path();
					path.moveTo(x + hw, y - hw);
					path.lineTo(x + hw + stepSize, y - hw + stepSize);
					path.lineTo(x - hw + stepSize, y + hw + stepSize);
					path.lineTo(x - hw, y + hw);
					path.close();
					canvas.drawPath(path, paint);
				}
			}	
		}
		// paint diagonal up connectors
		for(int i = 0; i < connectorsDiagUp.length; i++) {
			for(int j = 0; j < connectorsDiagUp[0].length; j++) {

				int x = distFromLeft + (stepSize / 2) + (i * stepSize);
				int y = distFromTop  + (stepSize / 2) + (j * stepSize);
				if(connectorsDiagUp[i][j] == 1) {
					Path path = new Path();
					path.moveTo(x - hw, y - hw + stepSize);
					path.lineTo(x - hw + stepSize, y - hw);
					path.lineTo(x + hw + stepSize, y + hw);
					path.lineTo(x + hw, y + hw + stepSize);
					path.close();
					canvas.drawPath(path, paint);
				}
			}	
		}
		
	}
	
	

	// randomly generate connectors
	public void randomizeConnectors() {
		
		// reset every '1' connector to '0'
		// then randomly decide if it should become a '1'
				
		Random rn = new Random();
		
		for(int i = 0; i < connectorsHorizontal.length; i++) {
			for(int j = 0; j < connectorsHorizontal[0].length; j++) {
				if(connectorsHorizontal[i][j] == 1)
					connectorsHorizontal[i][j] = 0;
				if(rn.nextInt() % 2 == 0  &&  i < n - 1  &&  j < n)
					connectorsHorizontal[i][j] = 1;
			}
		}
		for(int i = 0; i < connectorsVertical.length; i++) {
			for(int j = 0; j < connectorsVertical[0].length; j++) {
				if(connectorsVertical[i][j] == 1)
					connectorsVertical[i][j] = 0;
				if(rn.nextInt() % 2 == 0  &&  i < n  &&  j < n - 1)
					connectorsVertical[i][j] = 1;
			}
		}
		for(int i = 0; i < connectorsDiagDown.length; i++) {
			for(int j = 0; j < connectorsDiagDown[0].length; j++) {
				if(connectorsDiagDown[i][j] == 1)
					connectorsDiagDown[i][j] = 0;
				if(rn.nextInt() % 4 == 0  &&  i < n - 1  &&  j < n - 1)
					connectorsDiagDown[i][j] = 1;
			}
		}
		for(int i = 0; i < connectorsDiagUp.length; i++) {
			for(int j = 0; j < connectorsDiagUp[0].length; j++) {
				if(connectorsDiagUp[i][j] == 1)
					connectorsDiagUp[i][j] = 0;
				if(rn.nextInt() % 4 == 0  &&  i < n - 1  &&  j < n - 1)
					connectorsDiagUp[i][j] = 1;
			}
		}
		
		// store this information for resets
		storeForReset();
	}
	
	
	// load random puzzle data
	public void loadRandom() {
		
		for(int i = 0; i < MainGame.connectorsDiagUp_Storage.length; i++)
			for(int j = 0; j < MainGame.connectorsDiagUp_Storage[i].length; j++)
				connectorsDiagUp[i][j] = MainGame.connectorsDiagUp_Storage[i][j];
		for(int i = 0; i < MainGame.connectorsDiagDown_Storage.length; i++)
			for(int j = 0; j < MainGame.connectorsDiagDown_Storage[i].length; j++)
				connectorsDiagDown[i][j] = MainGame.connectorsDiagDown_Storage[i][j];
		for(int i = 0; i < MainGame.connectorsHorizontal_Storage.length; i++)
			for(int j = 0; j < MainGame.connectorsHorizontal_Storage[i].length; j++)
				connectorsHorizontal[i][j] = MainGame.connectorsHorizontal_Storage[i][j];
		for(int i = 0; i < MainGame.connectorsVertical_Storage.length; i++)
			for(int j = 0; j < MainGame.connectorsVertical_Storage[i].length; j++)
				connectorsVertical[i][j] = MainGame.connectorsVertical_Storage[i][j];
	}
	
	
	// store this information for resets
	public void storeForReset() {
		
		for(int i = 0; i < connectorsDiagUp.length; i++)
			for(int j = 0; j < connectorsDiagUp[i].length; j++)
				MainGame.connectorsDiagUp_Storage[i][j] = connectorsDiagUp[i][j];
		for(int i = 0; i < connectorsDiagDown.length; i++)
			for(int j = 0; j < connectorsDiagDown[i].length; j++)
				MainGame.connectorsDiagDown_Storage[i][j] = connectorsDiagDown[i][j];
		for(int i = 0; i < connectorsHorizontal.length; i++)
			for(int j = 0; j < connectorsHorizontal[i].length; j++)
				MainGame.connectorsHorizontal_Storage[i][j] = connectorsHorizontal[i][j];
		for(int i = 0; i < connectorsVertical.length; i++)
			for(int j = 0; j < connectorsVertical[i].length; j++)
				MainGame.connectorsVertical_Storage[i][j] = connectorsVertical[i][j];
	}
		
		
	
}
