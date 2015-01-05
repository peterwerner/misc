package com.peternwerner.iagogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * @author Peter Werner
 *
 * Temporarily highlights the most recent move, then fades out
 * 
 * This class has access to dots / connectors objects but should never modify them
 */
public class MoveTracer {
	
	// constants
	private double root2 = Math.sqrt(2); 
	
	// objects to reference
	private Dots dotsObj;
	private Connectors connectorsObj;
	
	// this' index in MainGame's list of MoveTracers
	private int index;
	
	private int i, j, n;
	
	// time duration variables
	private long timeStart,	timeNow;
	private long timeDelay = 400;			// time duration in milliseconds

	
	
	// constructor
	public MoveTracer(int _index, Dots _dotsObj, Connectors _connectorsObj, int _i, int _j) {
		
		i = _i;		j = _j;
		n = MainGame.n;
		index = _index;
		dotsObj = _dotsObj;
		connectorsObj = _connectorsObj;
		
		this.timeStart = System.currentTimeMillis();
	}
	
	
	
	// destructor
	public void destroy() {
		
		MainGame.tracerList.remove(index);
		
		for(int i = index; i < MainGame.tracerList.size(); i++) {
			
			MainGame.tracerList.get(i).decrementIndex();
		}
	}
	
	
	
	// paint the highlights
	public boolean paint(Canvas canvas) {
		
		this.timeNow = System.currentTimeMillis();
		
		// if time has expired OR we are not in the puzzle state, destroy this MoveTracer instance
		
		if(this.timeNow - this.timeStart >= timeDelay  ||  MainGame.gameState != 1) {
			this.destroy();
			return false;
		}
		
		// otherwise, paint the highlights
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(MainGame.color_white);
		int alpha = (int) (255 * (1.0 - ((float)(this.timeNow - this.timeStart) / timeDelay)));
		paint.setAlpha(alpha);
		
		getPaintObjects(canvas, paint);
		
		return true;
	}
	
	
	
	// select and paint all the dots / connectors we need
	public void getPaintObjects(Canvas canvas, Paint paint) {
				
		/*
		 * 	UNCOMMENT THE 'else' LINES FOR CONNECTIONS TO BE MADE REGARDLESS OF DISTANCE
		 *  RIGHT NOW, ONLY DOTS THAT ARE DIRECTLY CONNECTED CAN INFLUENCE EACH OTHER
		 */
		
		// check horizontal (to the right / +) connections, and flip dots accordingly
		boolean keepGoing = true;	
		int i2 = i,	j2 = j;
		while(keepGoing == true  &&  i2 < n - 1) {
			if(connectorsObj.connectorsHorizontal[i2][j2] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsHorizontal, i2, j2);
				paintDot(canvas, paint, i2 + 1, j2);
			}
			// else
				keepGoing = false;
			i2++;
		}
		// check horizontal (to the left / -) connections, and flip dots accordingly
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 > 0) {
			if(connectorsObj.connectorsHorizontal[i2 - 1][j2] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsHorizontal, i2, j2);
				paintDot(canvas, paint, i2 - 1, j2);
			}
			// else
				keepGoing = false;
			i2--;
		}
		// check vertical (in the down / + direction) connections
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  j2 < n - 1) {
			if(connectorsObj.connectorsVertical[i2][j2] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsVertical, i2, j2);
				paintDot(canvas, paint, i2, j2 + 1);
			}
			// else
				keepGoing = false;
			j2++;
		}
		// check vertical (in the up / - direction) connections
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  j2 > 0) {
			if(connectorsObj.connectorsVertical[i2][j2 - 1] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsVertical, i2, j2 - 1);
				paintDot(canvas, paint, i2, j2 - 1);
			}
			// else
				keepGoing = false;
			j2--;
		}
		// check diagonal down (in + direction) connections
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 < n - 1  &&  j2 < n - 1) {
			if(connectorsObj.connectorsDiagDown[i2][j2] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsDiagDown, i2, j2);
				paintDot(canvas, paint, i2 + 1, j2 + 1);
			}
			// else
				keepGoing = false;
			i2++;	j2++;
		}
		// check diagonal down (in - directions) connections
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 > 0  &&  j2 > 0) {
			if(connectorsObj.connectorsDiagDown[i2 - 1][j2 - 1] == 1){
				//paintConnector(canvas, paint, connectorsObj.connectorsDiagDown, i2 - 1, j2 - 1);
				paintDot(canvas, paint, i2 - 1, j2 - 1);
			}
			// else
				keepGoing = false;
			i2--;	j2--;
		}
		// check diagonal up (in + direction) connections
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 < n - 1  &&  j2 < n  &&  j2 > 0) {
			if(connectorsObj.connectorsDiagUp[i2][j2 - 1] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsDiagUp, i2, j2 - 1);
				paintDot(canvas, paint, i2 + 1, j2 - 1);
			}
			// else
				keepGoing = false;
			i2++;	j2--;
		}
		// check diagonal up (in - directions) connections
		keepGoing = true;	i2 = i;	j2 = j;
		while(keepGoing == true  &&  i2 > 0  &&  j2 >= 0  &&  i2 > 0) {
			if(connectorsObj.connectorsDiagUp[i2 - 1][j2] == 1) {
				//paintConnector(canvas, paint, connectorsObj.connectorsDiagUp, i2 - 1, j2);
				paintDot(canvas, paint, i2 - 1, j2 + 1);
			}
			// else
				keepGoing = false;
			i2--;	j2++;
		}
		paintDot(canvas, paint, i, j);

	}
	
	
	/*
	// paint a connector highlight
	public void paintConnector(Canvas canvas, Paint paint, int[][] array, int i2, int j2) {
		
		float hw = connectorsObj.connectorWidth / 2;	// half the width of a connector
		
		// paint horizontal connector
		if(array == connectorsObj.connectorsHorizontal) {
			int x = connectorsObj.distFromLeft + (connectorsObj.stepSize / 2) + (i2 * connectorsObj.stepSize);
			int y = connectorsObj.distFromTop  + (connectorsObj.stepSize / 2) + (j2 * connectorsObj.stepSize);
			if(connectorsObj.connectorsHorizontal[i2][j2] == 1)
				canvas.drawRect(x - hw, y - hw, connectorsObj.stepSize + x - hw, connectorsObj.connectorWidth + y - hw, paint);
		}
		
		// paint vertical connector
		else if(array == connectorsObj.connectorsVertical) {
			int x = connectorsObj.distFromLeft + (connectorsObj.stepSize / 2) + (i2 * connectorsObj.stepSize);
			int y = connectorsObj.distFromTop  + (connectorsObj.stepSize / 2) + (j2 * connectorsObj.stepSize);
			if(connectorsObj.connectorsVertical[i2][j2] == 1)
				canvas.drawRect(x - hw, y - hw, connectorsObj.connectorWidth + x - hw, connectorsObj.stepSize + y - hw, paint);
		}
		

		// paint diagonal up connector
		else if(array == connectorsObj.connectorsDiagUp) {
			hw /= root2;	// for the diagonal connectors we require the width * sin(45deg), cos(45deg)

			int x = connectorsObj.distFromLeft + (connectorsObj.stepSize / 2) + (i * connectorsObj.stepSize);
			int y = connectorsObj.distFromTop  + (connectorsObj.stepSize / 2) + (j * connectorsObj.stepSize);
			if(connectorsObj.connectorsDiagUp[i][j] == 1) {
				Path path = new Path();
				path.moveTo(x - hw, y - hw + connectorsObj.stepSize);
				path.lineTo(x - hw + connectorsObj.stepSize, y - hw);
				path.lineTo(x + hw + connectorsObj.stepSize, y + hw);
				path.lineTo(x + hw, y + hw + connectorsObj.stepSize);
				path.close();
				canvas.drawPath(path, paint);
			}
		}
		
		// paint diagonal down connector
		else if(array == connectorsObj.connectorsDiagDown) {
			hw /= root2;	// for the diagonal connectors we require the width * sin(45deg), cos(45deg)
			
			int x = connectorsObj.distFromLeft + (connectorsObj.stepSize / 2) + (i * connectorsObj.stepSize);
			int y = connectorsObj.distFromTop  + (connectorsObj.stepSize / 2) + (j * connectorsObj.stepSize);
			if(connectorsObj.connectorsDiagDown[i][j] == 1) {
				Path path = new Path();
				path.moveTo(x + hw, y - hw);
				path.lineTo(x + hw + connectorsObj.stepSize, y - hw + connectorsObj.stepSize);
				path.lineTo(x - hw + connectorsObj.stepSize, y + hw + connectorsObj.stepSize);
				path.lineTo(x - hw, y + hw);
				path.close();
				canvas.drawPath(path, paint);
			}
		}
		
	}
	*/
	
	
	
	// paint a dot highlight
	public boolean paintDot(Canvas canvas, Paint paint, int i2, int j2) {
		
		// make sure the dot is in bounds
		if(i2 > MainGame.n  ||  j2 > MainGame.n)
			return false;
		
		// paint highlight
		
		int x = dotsObj.distFromLeft + (dotsObj.stepSize / 2) + (i2 * dotsObj.stepSize);
		int y = dotsObj.distFromTop  + (dotsObj.stepSize / 2) + (j2 * dotsObj.stepSize);
						
		canvas.drawCircle(x, y, (float) (1.15 * dotsObj.dotRadius), paint);
		
		int colorCurrent = paint.getColor();
		int alphaCurrent = paint.getAlpha();
		
		// repaint colored dot over it
		
		paint.setAlpha(255);
		
		switch(dotsObj.matrix[i2][j2]) {
		case 0:	paint.setColor(dotsObj.colorBad);
				break;
		case 1: paint.setColor(dotsObj.colorGood);
				break;
		}
		
		canvas.drawCircle(x, y, (float) (dotsObj.dotRadius), paint);
		
		// revert to highlight color / alpha
		
		paint.setColor(colorCurrent);
		paint.setAlpha(alphaCurrent);
		
		return true;
	}	
	
	
	// decrement this' index by one
	public void decrementIndex() {
		
		index--;
	}
	
}
