package com.peternwerner.iagogame;

import com.peternwerner.iagogame.MainGame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.util.Log;

public class PuzzleMenu {

	private static final String TAG = PuzzleMenu.class.getSimpleName();
	
	// constants
	private int WIDTH;
	private int HEIGHT;
	private int WIDTHactual = MainGame.WIDTHactual;

	
	private String[] categoryNames = {"Tutorial", "ERROR", "Novice (2x2)", "Medium (3x3)", "Expert (4x4)", "Genius (5x5)", "Genius (6x6)", "Genius (7x7)", "Genius (8x8)", "Genius (9x9)"};
	
	// class variables
	private int numX, numY, size, buffer;
	private int transformX = 0, transformXprev = 0;
	private int spaceTop, spaceBottom, widthRibbon, widthPages;
	private int color = MainGame.color_dark;
	private int color2 = MainGame.color_medium;
	private int colorStarGood = MainGame.colorStar;
	private String categoryName;
	private int[] scores;
	
	private boolean scrolling = true;
	private int speedScroll = 80;
	private int pageTotal, pageCurrent = 1;
	
	private boolean enablePrev = false;
	private boolean enableNext = false;
	
	// controls
	private boolean doScrollLeft = false;
	private boolean doScrollRight = false;

	// paths
	private int xPtsRibbons[][] = new int[5][4];
	private int yPtsRibbons[][] = new int[5][4];
	private int[][] xPtsSquares = new int[MainGame.levelList[MainGame.n].length][4];
	private int[][] yPtsSquares = new int[MainGame.levelList[MainGame.n].length][4];

	
	
	// run when the object is first instantiated
	public void init() {
		
		// initialize variables
		
		WIDTH = MainGame.WIDTH;		HEIGHT = MainGame.HEIGHT;
		spaceTop = MainGame.distFromTopSquare - (int) (0.0520833 * HEIGHT);	// subtract width of puzzle mode scoreboard
		spaceBottom = spaceTop + MainGame.adSize;
		widthRibbon = (int) (0.065625 * HEIGHT);
		widthPages = (int) (0.0520833 * HEIGHT);
		buffer = MainGame.distBufferSide;
		
		scores = new int[MainGame.levelListSize[MainGame.n]];
		categoryName = categoryNames[MainGame.n];
		
		numX = 4;
		if(MainGame.n == 2)
			numX = 3;
		size = (MainGame.widthSquare - (buffer) * (numX - 1)) / numX;
		numY = (HEIGHT - spaceBottom - spaceTop) / (size + MainGame.distBufferSide);
		pageTotal = scores.length / (numX * numY);
		if(scores.length % (numX * numY) != 0)
			pageTotal++;
		
		int spacer = (int) (0.5 * (spaceTop - widthRibbon));
		
		// Top ribbon
		xPtsRibbons[0][0] = 0;							yPtsRibbons[0][0] = spacer;
		xPtsRibbons[0][1] = WIDTHactual;				yPtsRibbons[0][1] = spacer;
		xPtsRibbons[0][2] = WIDTHactual;				yPtsRibbons[0][2] = widthRibbon + spacer;
		xPtsRibbons[0][3] = 0;							yPtsRibbons[0][3] = widthRibbon + spacer;
		
		int sizeSub = (MainGame.widthSquare - buffer * 2) / 3;
		// Middle sub ribbon (menu button)
		xPtsRibbons[4][0] = (WIDTH - sizeSub) / 2;		yPtsRibbons[4][0] = spaceTop + MainGame.widthSquare + spacer + widthPages;
		xPtsRibbons[4][1] = (WIDTH + sizeSub) / 2;		yPtsRibbons[4][1] = spaceTop + MainGame.widthSquare + spacer + widthPages;
		xPtsRibbons[4][2] = (WIDTH + sizeSub) / 2;		yPtsRibbons[4][2] = widthRibbon + spaceTop + MainGame.widthSquare + spacer + widthPages;
		xPtsRibbons[4][3] = (WIDTH - sizeSub) / 2;		yPtsRibbons[4][3] = widthRibbon + spaceTop + MainGame.widthSquare + spacer + widthPages;
		// Side sub ribbons (previous and next buttons)
		xPtsRibbons[1][0] = buffer;						yPtsRibbons[1][0] = yPtsRibbons[4][0];
		xPtsRibbons[1][1] = xPtsRibbons[4][0] - buffer;	yPtsRibbons[1][1] = yPtsRibbons[4][1];
		xPtsRibbons[1][2] = xPtsRibbons[4][0] - buffer;	yPtsRibbons[1][2] = yPtsRibbons[4][2];
		xPtsRibbons[1][3] = buffer;						yPtsRibbons[1][3] = yPtsRibbons[4][3];
		xPtsRibbons[2][0] = WIDTH - buffer;				yPtsRibbons[2][0] = yPtsRibbons[4][0];
		xPtsRibbons[2][1] = xPtsRibbons[4][1] + buffer;	yPtsRibbons[2][1] = yPtsRibbons[4][1];
		xPtsRibbons[2][2] = xPtsRibbons[4][1] + buffer;	yPtsRibbons[2][2] = yPtsRibbons[4][2];
		xPtsRibbons[2][3] = WIDTH - buffer;				yPtsRibbons[2][3] = yPtsRibbons[4][3];
		
		for(int i = 1; i <= 4; i++) {
			if(i != 3) {
				for(int j = 0; j < xPtsRibbons[i].length; j++) {
					
					xPtsRibbons[i][j] += MainGame.scaleOffset;
				}
			}
		}
		
		// Bottom ribbon
		xPtsRibbons[3][0] = 0;							yPtsRibbons[3][0] = yPtsRibbons[1][2] + spacer;
		xPtsRibbons[3][1] = WIDTHactual;				yPtsRibbons[3][1] = yPtsRibbons[1][3] + spacer;
		xPtsRibbons[3][2] = WIDTHactual;				yPtsRibbons[3][2] = HEIGHT;
		xPtsRibbons[3][3] = 0;							yPtsRibbons[3][3] = HEIGHT;

		// update global page counter
		MainGame.itemsPerPage[MainGame.n] = numX * numY;

		updateTransformX();
	}
	
	
	
	// run every time the thread updates
	public void update() {		

		if(!scrolling)
			updateTransformX();
		
		// set next and previous booleans
		if(pageCurrent > 1  &&  !scrolling)
			enablePrev = true;
		else
			enablePrev = false;
		if(pageTotal - pageCurrent > 0  &&  !scrolling)
			enableNext = true;
		else
			enableNext = false;
		
		// create level squares path points
		int k = 0, page = 0, distLeft = (WIDTH - MainGame.widthSquare) / 2;
		distLeft += MainGame.scaleOffset;
		
		while(k < xPtsSquares.length) {
			for(int j = 0; j < numY; j++) {
				for(int i = 0; i < numX && k < xPtsSquares.length; i++) {
					xPtsSquares[k][0] = distLeft + (i * (size + buffer)) + (page * WIDTH) + transformX;
					yPtsSquares[k][0] = spaceTop + (j * (size + buffer));
					xPtsSquares[k][1] = distLeft + (i * (size + buffer)) + size + (page * WIDTH) + transformX;
					yPtsSquares[k][1] = spaceTop + (j * (size + buffer));
					xPtsSquares[k][2] = distLeft + (i * (size + buffer)) + size + (page * WIDTH) + transformX;
					yPtsSquares[k][2] = spaceTop + (j * (size + buffer)) + size;
					xPtsSquares[k][3] = distLeft + (i * (size + buffer)) + (page * WIDTH) + transformX;
					yPtsSquares[k][3] = spaceTop + (j * (size + buffer)) + size;
	
					k++;
				}
			}
			page++;
		}
		
		scrolling = false; 
		
		// shift to previous page if necessary
		if(doScrollLeft) {
			scrollLeft();
		}
		// shift to next page if necessary
		if(doScrollRight) {
			scrollRight();
		}
		scrolling = doScrollLeft || doScrollRight;
		
		// if we are not scrolling, store transformX
		if(!scrolling) {
			transformXprev = transformX;
		}
		
		MainGame.menuPage[MainGame.n] = pageCurrent;
		
		// make sure we are not in an invalid page
		if(!scrolling  &&  pageCurrent < 1)
			pageCurrent = 1;
		if(!scrolling  &&  pageCurrent > pageTotal)
			pageCurrent = 1;
	}
	
	
	
	// shift to previous page
	public void scrollLeft() {

		if(Math.abs(transformX - transformXprev) >= WIDTH - speedScroll) {
			scrolling = false;
			doScrollLeft = false;
			pageCurrent--;
			updateTransformX();
		}
		else if(doScrollLeft) {
			scrolling = true;
			transformX += speedScroll;
		}
	}
	
	// shift to next page
	public void scrollRight() {

		if(Math.abs(transformX - transformXprev) >= WIDTH - speedScroll) {
			scrolling = false;
			doScrollRight = false;
			pageCurrent++;
			updateTransformX();
		}
		else if(doScrollRight) {
			scrolling = true;
			transformX -= speedScroll;
		}
	}
	
	
	
	// what we do when the user clicks on the screen...
	public boolean checkClick(float x, float y) {
		
		// handle button presses
		
		if(y > Math.abs(yPtsRibbons[1][0])  &&  y < Math.abs(yPtsRibbons[1][2])) {
			
			// if the user clicks on the previous button
			if(enablePrev  &&   x > 0  &&  x < Math.abs(xPtsRibbons[1][2])) {
				// Log.d(TAG, "Previous button clicked");
				
				doScrollLeft = true;
				
				return true;
			}
			
			// if the user clicks on the next button
			if(enableNext  &&  x < WIDTH  &&  x > xPtsRibbons[2][1]) {
				// Log.d(TAG, "Next button clicked");
				
				doScrollRight = true;
				
				return true;
			}
			
			// if the user clicks on the menu button
			if(x < xPtsRibbons[4][1]  &&  x > xPtsRibbons[4][0]) {
				// Log.d(TAG, "Menu button clicked");
				
				MainGame.gameState = 4;

				return true;
			}
		}
		
		// load selected puzzle
		
		int x2 = (int) (x - (WIDTH - MainGame.widthSquare) / 2);
		int y2 = (int) (y - spaceTop);
		
		if(y2 > 0  &&  y2 < MainGame.widthSquare) {
			if(x2 > 0  &&  x2 < MainGame.widthSquare) {
				
				int i = (numX * x2 / MainGame.widthSquare);
				int j = (numY * y2 / MainGame.widthSquare);
								
				int index = (i) + (j * numX) + (numX * numY * (pageCurrent - 1));
				
				if(index < MainGame.levelListSize[MainGame.n]) {
					// Log.d(TAG, "Level index changed to: " + MainGame.levelIndex);
					MainGame.levelIndex = index;
					MainGame.gameState = -1;
					return true;
				}
			}
		}
		
		return false;
	}	
		
	
	
	/*
	 * Graphical methods
	 */
	
	
	// paint everything
	public void paint(Canvas canvas) {

		paintRibbons(canvas);
		paintLevels(canvas);
		paintText(canvas);
	
	}
	
	
	
	// paint the ribbons
	public void paintRibbons(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
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
	
	
	
	// paint the squares
	private void paintLevels(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		for(int i = 0; i < xPtsSquares.length; i++) {
			
			// only paint if the square is visible
			if(xPtsSquares[i][0] >= 0 - size  &&  xPtsSquares[i][0] <= WIDTHactual) {
				
				// paint the square
				
				Path path = new Path();
				path.moveTo(xPtsSquares[i][0], yPtsSquares[i][0]);
				
				for(int j = 1; j < 4; j++) {
					
					path.lineTo(xPtsSquares[i][j], yPtsSquares[i][j]);
				}
				path.close();
				
				if((i / numX) % numX == 0)
					paint.setColor(MainGame.color_medium);
				else if((i / numX) % numX == numX - 1)
					paint.setColor(MainGame.color_medium);
				else
					paint.setColor(MainGame.color_medium);
				
				canvas.drawPath(path, paint);
				
				/*
				// paint a triangle at the top left corner of the square, to give it an angled look
				Path path2 = new Path();
				path2.moveTo(xPtsSquares[i][0], yPtsSquares[i][0]);
				path2.lineTo(xPtsSquares[i][0] + (size / 6), yPtsSquares[i][0]);
				path2.lineTo(xPtsSquares[i][0], yPtsSquares[i][0] + (size / 6));
				path2.close();
				
				paint.setColor(MainGame.color_white);
				canvas.drawPath(path2, paint);
				*/
				
				// paint the text and stars for that square
				
				int _score = MainGame.scoreListStars[MainGame.n][i];
				paintTextLevel(canvas, xPtsSquares[i][0], yPtsSquares[i][0], size, i, _score);
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
		
		paint.setTextSize(widthRibbon);
		
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(categoryName, WIDTHactual / 2, (float) (yPtsRibbons[0][2] - (0.14 * widthRibbon)), paint);
		
		canvas.drawText("Menu", (float) ((xPtsRibbons[4][0] + xPtsRibbons[4][1]) /2), (float) (yPtsRibbons[4][2] - (0.14 * widthRibbon)), paint);

		if(enablePrev == false)
			paint.setColor(color2);
		canvas.drawText("Prev", (float) ((xPtsRibbons[1][0] + xPtsRibbons[1][1]) /2 + (0.14 * widthRibbon)), (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
		
		paint.setColor(MainGame.color_white);
		
		if(enableNext == false)
			paint.setColor(color2);
		canvas.drawText("Next",(float) ((xPtsRibbons[2][0] + xPtsRibbons[2][1]) /2 - (0.14 * widthRibbon)), (float) (yPtsRibbons[1][2] - (0.14 * widthRibbon)), paint);
		
		paint.setColor(color);
		paint.setTextSize((float) (0.85 * widthRibbon));
		
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText("(" + pageCurrent + "/"+ pageTotal + ")", WIDTHactual / 2, spaceTop + MainGame.widthSquare + widthRibbon, paint);
	
	}
	
	
	
	// paint the level square elements for one level square
	public void paintTextLevel(Canvas canvas, int x, int y, int _size, int _name, int _score) {
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(MainGame.color_white);
		
		// paint the level name
		
		paint.setTypeface(MainGame.tf);
		paint.setTextSize((float) (size * 0.6));
		paint.setTextAlign(Paint.Align.CENTER);
		if(_name < 10)
			canvas.drawText("0" + _name, x + (_size / 2), (float) (y + (.60 * _size)), paint);
		else
			canvas.drawText("" + _name, x + (_size / 2), (float) (y + (.60 * _size)), paint);
		
		// paint the stars
		
		paint.setColor(colorStarGood);
		paint.setAlpha(140);
		
		int w = (int)((_size / 3) * 0.8);
		int h = (int)(.95 * w);
		int spacer = (int)((_size - (3 * w)) / 4);
		
		for(int i = 0; i < 3; i++) {
			if(i >= _score) {
				paint.setColor(MainGame.color_white);
				paint.setAlpha(80);
			}
			int x2 = (w * i) + (spacer * (i + 1));
			drawStar(canvas, paint, x + x2, y + _size - h - spacer, h, w);
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



	// update global and local page counter when we step in and out of this game state
	public void updatePageCurrent() {
		
		// update page global page counter for use by the puzzle selection menu
		MainGame.menuPage[MainGame.n] = 1 + MainGame.levelIndex / MainGame.itemsPerPage[MainGame.n];
			
		// Log.d(TAG, "MainGame.menuPage = " + MainGame.menuPage + "; " + MainGame.levelIndex);
		
		pageCurrent = MainGame.menuPage[MainGame.n];
		
		if(pageCurrent < 1  ||  pageCurrent > pageTotal)
			pageCurrent = 1;
		
		updateTransformX();
	}
	
	
	
	// update the transformX when not scrolling
	public void updateTransformX() {
		
		transformX = -1 * WIDTH * (pageCurrent - 1);
	}



	public int getPageCurrent() {
		return pageCurrent;
	}



	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}
	
	
	public void moveLeft() {
		if(!scrolling  &&  pageCurrent > 1)
			doScrollLeft = true;
	}
	
	public void moveRight() {
		if(!scrolling  &&  pageCurrent < pageTotal)
			doScrollRight = true;
	}
	
}
