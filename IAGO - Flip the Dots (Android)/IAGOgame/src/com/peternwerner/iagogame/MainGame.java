package com.peternwerner.iagogame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.storage.StorageManager;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author Peter Werner
 *
 * Controls pretty much every functional part of the game 
 * Its methods are called when the thread starts / at every tick
 */
public class MainGame {

	private static final String TAG = MainGame.class.getSimpleName();
		
	/*  Game State:
	 *  (negative values will enter a null state for 1 tick, then enter the corresponding positive value state
	 * 	0 -> Main Menu
	 *  1 -> Puzzle (solving - display puzzle)
	 *  2 -> Puzzle (solved  - display win screen)
	 *  3 -> Puzzle Selection Menu
	 *  4 -> Classic Mode Selection Menu
	 *  5 -> Random Puzzle (solving - display puzzle)
	 *  6 -> Random Puzzle (solved - display win screen)
	 *  7 -> Settings Menu
	 *  8 -> Tutorial Intro
	 */
	static int gameStatePrevious;
	static int nMax = 10;
	
	// PREFS
	static boolean didTutorial = false;
	static int[][] scoreListBest = new int[10][100];
	static int[][] scoreListStars = new int[10][100];
	static boolean soundEnabled = true;
	static int gameState = 0;
	static int PREF_pageCurrent = 1;
	static int n = 3;
	static int PREF_scoreNow = 0;
	static int[][] matrix_Storage = new int[nMax][nMax];
	static int[][] connectorsHorizontal_Storage = new int[nMax - 1][nMax];
	static int[][] connectorsVertical_Storage   = new int[nMax][nMax - 1];
	static int[][] connectorsDiagDown_Storage   = new int[nMax - 1][nMax - 1];
	static int[][] connectorsDiagUp_Storage     = new int[nMax - 1][nMax - 1];
	static boolean unlockAll = false;
	
	
	// asset manager
	static AssetManager assetManager;
	// media player
	public MediaPlayer mediaPlayer1, mediaPlayer2;
	
	// width and distance from top of screen for the square which the dots occupy, buffer space on side of screen
    static int widthSquare;
    static int distFromTopSquare;
    static int distBufferSide;
	// size of screen
	static int HEIGHT, WIDTH, WIDTHactual, scaleOffset;
	// size of ad
	static int adSize = MainActivity.adHeight;
	
	private Context context;
	
	// fonts
	static Typeface tf;
	// colors
	static int colorScheme = 1;
	static int colorStar = Color.rgb(255, 225, 0);
	static int[] colors;
	static int color_white;
	static int color_light;
	static int color_medium;
	static int color_dark;
	
	// list of puzzle files
	static String[][] levelList = new String[10][100];
	static int[] levelListSize = new int[10];
	static int levelIndex = 0;
	// name of current puzzle file
	static String levelName;
	// size of puzzle board
	// n is already defined as a PREF
	static int nPrev = 0;
	// score variables
	// scoreNow already defined as a PREF
	static int scoreNow = 0;
	static int scoreBest = 0;
	static int[] scoreRating = {0, 0, 0};
	private String stringComment = "Good job";
	// store page number for puzzle selection screen
	static int[] menuPage = new int[10];
	static int[] itemsPerPage = new int[10];
	
	// game class objects
	private Dots dotsObj = null;
	private Connectors connectorsObj = null;
	private PuzzleInterface puzzleGuiObj = null;
	private PuzzleMenu puzzleMenuObj = null;
	private MainMenu mainMenuObj = null;
	private ClassicMenu classicMenuObj = null;
	private FileManager fileManagerObj = null;
	private Settings settingsObj = null;
	private Tutorial tutorialObj = null;
	static ArrayList<MoveTracer> tracerList = null;

	// touch event variables
	boolean isPressing = false;
	float pressXdown = 0,	pressYdown = 0;
	float distTolerance;
	long timeLastDown = 0;
	
	private boolean firstUpdate = true;
	private boolean newRandom = false;
	// controls
	static boolean doLoadPuzzle = false;
	static boolean doSuppressReset = false;
	static boolean doSetColorScheme = false;
	static boolean doLoadRandomData = false;
	static boolean doRandomizePuzzle = false;
	static boolean doWinSound = false;
	static boolean doBack = false;
	
	
	
	/*
	 * run when the game first starts
	 */
	public void init(int _WIDTH, int _HEIGHT, AssetManager _assetManager) {		
			
		// Log.d(TAG, "Initializing main game object...");
		
		dotsObj = null;
		connectorsObj = null;
		puzzleGuiObj = null;
		puzzleMenuObj = null;
		mainMenuObj = null;
		classicMenuObj = null;
		fileManagerObj = null;
		settingsObj = null;
		tutorialObj = null;
		tracerList = null;
		
		// reset static controls
		doLoadPuzzle = false;		doLoadRandomData = false;
		doSetColorScheme = false;	doSuppressReset = false;
		doRandomizePuzzle = false;
		
		// set default game state to main menu
		gameState = 0;
		gameStatePrevious = 0;
		
		// initialize variables
		assetManager = _assetManager;
		WIDTH = _WIDTH;	HEIGHT = _HEIGHT;
		distBufferSide = (int) (WIDTH / 33.75);
		widthSquare = (int) (WIDTH - (2 * distBufferSide));		// replaced with scaling system
		distFromTopSquare = (int) (HEIGHT * (19.0 / 96.0));		// replaced with scaling system
		tracerList = new ArrayList<MoveTracer>();
		distTolerance = (float) (WIDTH / 15.0);
		
		// scale sizing stuff
		scaleSizing();
		
		for(int i = 0; i < menuPage.length; i++)
			menuPage[i] = 1;
		for(int i = 0; i < itemsPerPage.length; i++)
			itemsPerPage[i] = 0;
		
		// set colors
		setColorScheme(colorScheme);
		
		// create fontface
		tf =Typeface.createFromAsset(assetManager,"fonts/BebasNeue.ttf");
		
		// populate list of puzzle files
		// *** once all levels are ready to ship, replace this by populating lists within this code
		// *** (using the asset manager makes initially loading the application much too slow)
		// Log.d(TAG, "Level loading starting.");
		long timeStart = System.currentTimeMillis();
		/*
		for(int i = 1; i <= 5; i++) {
		
			try {
				String[] list = assetManager.list("levels/" + i);
				if(list.length > 0) {
					levelList[i] = list;
					levelListSize[i] = list.length;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
		// this will not work if level name or folder structure changes
		LevelListFiller listFiller = new LevelListFiller();
		listFiller.fillList();
		
		// Log.d(TAG, "Level loading complete.  Time elapsed: " + (System.currentTimeMillis() - timeStart) + " ms");
				
		// initialize main menu object
		mainMenuObj = new MainMenu();
		
	}
	
	
	
	// run once, right after init and after the variables have been loaded in from the prefs file
	public void initPrefState() {
		
		// Log.d(TAG, "INIT PREF STATE (): scoreNow: " + PREF_scoreNow + "; top left dot:" + matrix_Storage[0][0]);
		
		// we are in a puzzle 
		if(gameState == 1) {
			gameState = 3;
		}
		
		// we are in a random puzzle
		else if(gameState == 5) {
			gameState = 4;
		}
		
		// we are in the standard win screen
		else if(gameState == 2) {
			gameState = 3;
		}
		
		// we are in the random mode win screen
		else if(gameState == 6) {
			gameState = 4;
		}
		
		// we are in the tutorial
		else if(gameState == 8) {
			gameState = 0;
		}
		
		// Log.d(TAG, "MainGame: initPrefState() completed");
	}
	
	
	
	/* 
	 * run every time the thread updates
	 */
	public void update() {
				
		// update puzzle name
		levelName = levelList[n][levelIndex];
		
		// update best score if we're in puzzle mode / win mode
		if(gameState == 1  ||  gameState == 2)
			scoreBest = scoreListBest[n][levelIndex];
		
		// update color scheme
		if(doSetColorScheme)
			setColorScheme(colorScheme);
				
		// be extra sure we don't hold on to dot info when not in the puzzle state (to prevent weird glitches that I have no idea what is causing)
		if(gameState != 1  &&  gameState != 5)
			dotsObj = null;
		
		// Special case:
		// if the game state is -N, we enter state N after one tick of being in -N
		if(gameState < 0) {
			gameStatePrevious = gameState;
			gameState = -1 * gameState;
		}
		
		// we are in the main menu state
		else if(gameState == 0) {
			gameStatePrevious = gameState;
			adjustPageNumbers();
		}
		
		// we are in the standard puzzle mode state (not the win screen)
		// or we are in the random puzzle mode state (not the win screen)
		else if(gameState == 1  ||  gameState == 5) {

			// if we are in random mode, overwrite the levelname
			if(gameState == 5)
				levelName = "Random (" + n + "x" + n + ")";
				
			// we were not in the standard puzzle mode state last time we entered this loop
			if(gameStatePrevious != gameState  ||  dotsObj == null  ||  connectorsObj == null  ||  puzzleGuiObj == null  ||  fileManagerObj == null) {
				// Log.d(TAG, "Initializing puzzle game objects, state: " + gameState + "; prev state: " + gameStatePrevious);
				int gameStatePrevPrev = gameStatePrevious;
				gameStatePrevious = gameState;
				
				// re-initialize puzzle game objects and load puzzle
				dotsObj = new Dots();
				dotsObj.init();
				connectorsObj = new Connectors();
				connectorsObj.init();
				puzzleGuiObj = new PuzzleInterface();
				puzzleGuiObj.init(levelName);
				fileManagerObj = new FileManager();
				fileManagerObj.init(dotsObj, connectorsObj);
				
				// if we are in standard mode, load puzzle from file
				if(gameState == 1) {
					doLoadPuzzle = true;
				}
				else if(gameState == 5) {
					if(gameStatePrevPrev != 6)
						doRandomizePuzzle = true;
					doLoadRandomData = true;
				}
				
				if(gameState == 5)
					newRandom = true;
			}
			else {
				// check the win condition
				dotsObj.checkWin();
			}
			
			if(newRandom){
				newRandom = false;
				doLoadRandomData = true;
			}
			
			// load level (return to main menu if loading the level fails)
			if(doLoadPuzzle)
				loadPuzzle("levels/" + n + "/" + levelName);
			
			// if we are in random mode, check if we need to generate a new puzzle
			if(doRandomizePuzzle)
				randomizePuzzle();
			// if we are in random mode, check if we need to reset the puzzle
			if(doLoadRandomData) {
				loadRandomData();
				// if(firstUpdate)
					// scoreNow = PREF_scoreNow;
			}
			
			// update objects
			puzzleGuiObj.update();
		}
		
		// we are in the standard puzzle mode win screen state
		// or we are in the random puzzle mode win screen state
		else if(gameState == 2  ||  gameState == 6) {
			
			// we were not in this state last time we entered this loop
			if(gameStatePrevious != gameState) {
				gameStatePrevious = gameState;
								
				stringComment = randomComment();
			}
			
		}
		
		// we are in the standard puzzle selection menu
		else if(gameState == 3) {
			
			// reset level index so we don't attempt to load out of bounds
			levelIndex = 0;

			// if we were in the classic mode menu previously
			// adjust puzzle menu page numbers based on star counts
			if(gameStatePrevious == 4)
				adjustPageNumbers();
			
			// we were not in this state last time we entered this loop
			if(gameStatePrevious != gameState  ||  puzzleMenuObj == null) {
				gameStatePrevious = gameState;
				
				// create new puzzle selection menu 
				puzzleMenuObj = new PuzzleMenu();
				puzzleMenuObj.init();
				puzzleMenuObj.setPageCurrent(menuPage[n]);
			}
			puzzleMenuObj.update();
			
			// if we launch into this gamestate, adjust page number based on stored PREF
			if(firstUpdate) {
				// Log.d(TAG, "Initializing page number from PREFs (" + PREF_pageCurrent + ")");
				puzzleMenuObj.setPageCurrent(PREF_pageCurrent);
			}
			
			// record page number for PREFs
			if(puzzleMenuObj != null)
				PREF_pageCurrent = puzzleMenuObj.getPageCurrent();
		}
		
		// we are in the classic mode selection menu
		else if(gameState == 4) {

			// we were not in this state last time we entered this loop
			if(gameStatePrevious != gameState  ||  classicMenuObj == null) {
				gameStatePrevious = gameState;
				
				classicMenuObj = new ClassicMenu();
				classicMenuObj.init();
			}
			
		}
		
		// we are in the settings menu
		else if(gameState == 7) {

			// we were not in this state last time we entered this loop
			if(gameStatePrevious != gameState  ||  settingsObj == null) {
				gameStatePrevious = gameState;
				
				settingsObj = new Settings();
				settingsObj.init();
			}
			
		}
		
		// we are in tutorial mode
		else if(gameState == 8) {
			
			if(gameStatePrevious != gameState  ||  tutorialObj == null) {
				gameStatePrevious = gameState;
				
				tutorialObj = new Tutorial();
				dotsObj = new Dots();
				connectorsObj = new Connectors();
				
				tutorialObj.init(dotsObj, connectorsObj);
			}
			
		}
		
		else {
			gameStatePrevious = gameState;
		}
		
		// play sounds
		if(doWinSound  &&  soundEnabled) {
			playSounds(mediaPlayer2);
			doWinSound = false;
		}
		
		// respond to back button press
		if(doBack)
			backButton();
		
		firstUpdate = false;
	}
	
	
	
	/*
	 * paint things to the screen
	 */
	public boolean paint(Canvas canvas) {
		
		if (canvas == null)
			return false;
		
		// fill canvas background
		canvas.drawColor(color_white);
		
		// we are in the main menu state
		if(gameState == 0  &&  mainMenuObj != null) {
			mainMenuObj.paint(canvas);
		}
		// we are in the standard puzzle mode state (not the win screen)
		else if((gameState == 1  ||  gameState == 5)  &&  dotsObj != null  &&  connectorsObj != null  &&  puzzleGuiObj != null) {
			dotsObj.paintSquare(canvas);
			connectorsObj.paintConnectors(canvas);
			dotsObj.paintDots(canvas);
			puzzleGuiObj.paintRibbons(canvas);
			puzzleGuiObj.paintText(canvas);
			if(dotsObj.doPaintWinBurst)
				dotsObj.paintWinBurst(canvas);
			for(int i = 0; i < tracerList.size(); i++)
				tracerList.get(i).paint(canvas);
		}
		
		// we are in the standard puzzle mode's win screen
		else if((gameState == 2  ||  gameState == 6)  &&  puzzleGuiObj != null) {
			
			puzzleGuiObj.paintRibbons(canvas);
			puzzleGuiObj.paintWinRibbons(canvas);
			puzzleGuiObj.paintText(canvas);
			puzzleGuiObj.paintWinText(canvas, stringComment);
		}
		
		// we are in the standard puzzle selection menu
		else if(gameState == 3  &&  puzzleMenuObj != null) {

			puzzleMenuObj.paint(canvas);
		}
		
		// we are in the classic mode selection menu
		else if(gameState == 4  &&  classicMenuObj != null) {
			
			classicMenuObj.paint(canvas);
		}

		// we are in the classic mode selection menu
		else if(gameState == 7  &&  settingsObj != null) {
			
			settingsObj.paint(canvas);
		}
		
		// we are in tutorial mode
		else if(gameState == 8  &&  tutorialObj != null) {
			tutorialObj.paint(canvas);
		}
		
		return true;
	}
	

	
	
	/*
	 * handle touch events
	 */
	public boolean onTouchUp(float _x, float _y) {
		// (isPressing is still true during this call)
		
		double dist = Math.sqrt((_x - pressXdown) * (_x - pressXdown) + (_y - pressYdown) * (_y - pressYdown));
		
		// check if the user has swiped (only for puzzle menu)
		if(gameState == 3  &&  dist > distTolerance * 2)
			checkSwipe(_x, _y);
		
		// don't register click if the user moves their finger too much while it is down
		if(dist > distTolerance)
			return false;
		
		float x = pressXdown,	y = pressYdown;
				
		// we are in the main menu state
		if(gameState == 0  &&  mainMenuObj != null) {
			if(mainMenuObj.checkClick(x, y))
				playSounds(mediaPlayer1);
		}
		
		// we are in the standard puzzle mode state (not the win screen)
		// or we are in the random puzzle mode state
		if((gameState == 1  ||  gameState == 5)  &&  dotsObj != null) {
			
			// extra precaution to avoid the ghost dotTracer bug
			if(y >= distFromTopSquare  &&  y <= distFromTopSquare + widthSquare) {
				if(dotsObj.checkClick(x, y, connectorsObj, dotsObj))
					playSounds(mediaPlayer1);
			}
			else {
				if(puzzleGuiObj.checkClick(x, y))
					playSounds(mediaPlayer1);
			}
		}
		
		// we are in the standard puzzle mode's win screen
		// or we are in the random puzzle mode's win screen
		else if((gameState == 2  ||  gameState == 6)  &&  puzzleGuiObj != null) {
			if(puzzleGuiObj.checkClick(x, y))
				playSounds(mediaPlayer1);
		}
		
		// we are in the standard puzzle selection menu
		else if(gameState == 3  &&  puzzleMenuObj != null) {
			if(puzzleMenuObj.checkClick(x, y))
				playSounds(mediaPlayer1);
		}
		
		// we are in the classic mode selection menu
		else if(gameState == 4  &&  classicMenuObj != null) {
			if(classicMenuObj.checkClick(x, y))
				playSounds(mediaPlayer1);
		}
	
		// we are in the settings menu
		else if(gameState == 7  &&  settingsObj != null) {
			if(settingsObj.checkClick(x, y))
				playSounds(mediaPlayer1);
		}
		
		// we are in tutorial mode
		else if(gameState == 8  &&  tutorialObj != null) {
			if(tutorialObj.checkClick(x, y))
				playSounds(mediaPlayer1);
		}
		
		return true;
	}
	
	
	public boolean onTouchDown(float _x, float _y) {
		
		pressXdown = _x;
		pressYdown = _y;
		
		timeLastDown = System.currentTimeMillis();
		
		return true;
	}
	
	
	public void checkSwipe(float xUp, float yUp) {
		
		long timeTolerance = 450;	// max duration of a swipe in milliseconds
		
		if(pressYdown > distFromTopSquare  &&  pressYdown < distFromTopSquare + widthSquare  
			&&  puzzleMenuObj != null
			&&  System.currentTimeMillis() - timeLastDown < timeTolerance
			&&  Math.abs(yUp - pressYdown)  < distTolerance * 2
			){
				if(xUp < pressXdown)
					puzzleMenuObj.moveRight();
				else if(xUp > pressXdown)
					puzzleMenuObj.moveLeft();
			}
	}

	
	
	// load the current puzzle
	public void loadPuzzle(String filePath) {
		doLoadPuzzle = false;
		
		dotsObj.lastClick[0] = -1;
		dotsObj.lastClick[1] = -1;
		
		// load level (return to main menu if loading the level fails)
		InputStream inStream;
		try {
			inStream = assetManager.open(filePath);
			fileManagerObj.readFile(inStream);
		} catch (IOException e) {
			e.printStackTrace();
			gameState = 0;
		}
		
		// store this info for resets
		//if(!firstUpdate) {
			for(int i = 0; i < dotsObj.matrix.length; i++)
				for(int j = 0; j < dotsObj.matrix[i].length; j++)
					matrix_Storage[i][j] = dotsObj.matrix[i][j];
			for(int i = 0; i < connectorsObj.connectorsDiagDown.length; i++)
				for(int j = 0; j < connectorsObj.connectorsDiagDown[i].length; j++)
					connectorsDiagDown_Storage[i][j] = connectorsObj.connectorsDiagDown[i][j];
			for(int i = 0; i < connectorsObj.connectorsDiagUp.length; i++)
				for(int j = 0; j < connectorsObj.connectorsDiagUp[i].length; j++)
					connectorsDiagUp_Storage[i][j] = connectorsObj.connectorsDiagUp[i][j];
			for(int i = 0; i < connectorsObj.connectorsHorizontal.length; i++)
				for(int j = 0; j < connectorsObj.connectorsHorizontal[i].length; j++)
					connectorsHorizontal_Storage[i][j] = connectorsObj.connectorsHorizontal[i][j];
			for(int i = 0; i < connectorsObj.connectorsVertical.length; i++)
				for(int j = 0; j < connectorsObj.connectorsVertical[i].length; j++)
					connectorsVertical_Storage[i][j] = connectorsObj.connectorsVertical[i][j];
		//}
		
		// if we launch into a puzzle, load from stored PREF data
		// !! this is redundant code, check the update method for puzzle gamestate !!
		if(firstUpdate) {
			// Log.d(TAG, "Loading level from PREF data");
						
			// loadRandomData();			
		}
		
		if(puzzleMenuObj != null  &&  itemsPerPage[n] != 0)
			puzzleMenuObj.updatePageCurrent();
		
	}
	
	
	
	// return a random comment (string) based on the current score rating
	public String randomComment() {
		
		// text to display on the win screen (0, 1, 2, 3 => no stars, 1 star, 2 stars, 3 stars)
		String[][] winStrings = {{"Tragic", "Abysmal", "Dreadful", "Deplorable", "Appalling", "Really?"},
								{"Decent", "Satisfactory", "Acceptable", "Passable", "Getting there", "Good job", "Good work", "Nice job", "Not bad", "Pretty good"},
								{"Great job", "Great work", "Impressive", "Outstanding", "Splendid", "First-rate", "Awesome", "Delicious", },
								{"Swaggo!", "Legendary!", "Breathtaking!", "Genius!", "Incredible!", "Astounding!", "Spectacular!", "Magnificent!", "Amazing!"}};
		
		Random rn = new Random();
		int rating = 0;
		
		for(int i = 0; i < scoreRating.length; i++) {
			if(scoreNow <= scoreRating[i])
				rating++;
		}
		
		return winStrings[rating][Math.abs((rn.nextInt())) % winStrings[rating].length];
	}
	
	
	
	// set the color scheme
	public void setColorScheme(int input) {
		
		doSetColorScheme = false;
		
		// Earth Colors: muted red / brown / gold
		if(input == 1) {
			
			colors = new int[4];
			colors[0] = Color.rgb(18, 255, 202);
			colors[1] = Color.rgb(255, 163, 32);
			colors[2] = Color.rgb(50, 215, 50);
			colors[3] = Color.rgb(225, 50, 50);
			color_white = Color.rgb(254,250,245);
			color_light = Color.rgb(205,201,159);
			color_medium = Color.rgb(174,154,149);
			color_dark = Color.rgb(85,78,71);
		}
		
		// default to standard black and white with various dot colors
		else {
			
			colors = new int[4];
			colors[0] = Color.rgb(0, 255, 255);
			colors[1] = Color.rgb(255, 55, 55);
			colors[2] = Color.rgb(55, 255, 55);
			colors[3] = Color.rgb(255, 225, 0);
			color_white = Color.WHITE;
			color_light = Color.LTGRAY;
			color_medium = Color.GRAY;
			color_dark = Color.DKGRAY;
		}
	}
	
	
	
	// load random mode puzzle data from storage (when the puzzle is reset)
	public void loadRandomData() {
		doLoadRandomData = false;
		
		dotsObj.lastClick[0] = -1;
		
		if (dotsObj != null  &&  connectorsObj != null){
			dotsObj.loadRandom();
			connectorsObj.loadRandom();
			dotsObj.storeForReset();
			connectorsObj.storeForReset();
		}
		
		scoreNow = 0;
		
		// Log.d(TAG, "MainGame: Loaded random puzzle data");
	}
	
	
	
	
	// adjust initial page numbers based on star ratings (we cannot do this in init(), as the score lists are not yet updated)
	private void adjustPageNumbers() {	
		
		for(int i = 0; i < menuPage.length; i++) {
			
			int firstIncomplete = 0;
			for(int j = 0; j < scoreListStars[i].length; j++) {
				
				if(scoreListStars[i][j] == 0) {
					firstIncomplete = j;
					break;
				}
			}
			
			if(i == 2)
				menuPage[i] = 1 + (int) (firstIncomplete / 9);
			else
				menuPage[i] = 1 + (int) (firstIncomplete / 16);
			
		}
		
	}
	
	
	
	// dynamically determine the size variables based on aspect ratio
	private void scaleSizing() {
		
		// *** TESTING VALUES
		// HEIGHT *= .8;
		
		// default values
		distBufferSide = (int) (WIDTH / 33.75);
		widthSquare = (int) (WIDTH - (2 * distBufferSide));
		distFromTopSquare = (int) (HEIGHT * (19.0 / 96.0));
		WIDTHactual = WIDTH;

		// if our ratio is too small, we need to scale
		if(WIDTH + (distFromTopSquare * 2) > HEIGHT) {
			
			WIDTH = HEIGHT - (distFromTopSquare * 2);
			
			distBufferSide = (int) (WIDTH / 33.75);
			widthSquare = (int) (WIDTH - (2 * distBufferSide));
			distFromTopSquare = (int) (HEIGHT * (19.0 / 96.0));

			// Log.d(TAG, "Adjusted window sizing ");
		}
		
		scaleOffset = (WIDTHactual - WIDTH) / 2;
	}


	
	// respond to back button press
	public void backButton() {
		doBack = false;
		
		if(gameState == 4  ||  gameState == 7  ||  gameState == 8)
			gameState = 0;
		else if(gameState == 1  ||  gameState == 2)
			gameState = 3;
		else if(gameState == 3  ||  gameState == 5  ||  gameState == 6)
			gameState = 4;

	}
	
	
	
	// play a sound effect
	public void playSounds(MediaPlayer mp) {
		
		if(mp != null  &&  soundEnabled) {
			mp.start();
		}
	}
	
	
	// init media players
	public void initSounds() {

		//mediaPlayer1 = MediaPlayer.create(context, R.raw.button);
		//mediaPlayer2 = MediaPlayer.create(context, R.raw.sine);
		
	}
	
	
	
	// randomize puzzle
	public void randomizePuzzle() {
		doRandomizePuzzle = false;
	
		// randomize connectors
		connectorsObj.randomizeConnectors();
		connectorsObj.storeForReset();
		
		// randomly flip some dots
		dotsObj.randomizeDots(n + (2 * n) / 3, connectorsObj, dotsObj);
		dotsObj.storeForReset();
		
		// set default scores
		scoreBest = 0;
		scoreNow = 0;
		scoreRating[0] = 9999;	scoreRating[1] = 9999; scoreRating[2] = 9999;
		
		// if the puzzle is too easy, generate a new one
		
		int sumGreyDots = 0;
		
		for(int i = 0; i < matrix_Storage.length; i++){ 
			for(int j = 0; j < matrix_Storage[i].length; j++){ 
				sumGreyDots++;
			}
		}
		if (sumGreyDots < 2)
			randomizePuzzle();
		
	}


	
	// !! This method is only called when the app is destroyed !!
	// move all current puzzle data into storage matrices
	public void storePuzzle() {
		/*
		for(int i = 0; i < matrix_Storage.length; i++) {
			for(int j = 0; j < matrix_Storage[i].length; j++) {
				matrix_Storage[i][j] = dotsObj.matrix[i][j];
				
				if(matrix_Storage[i][j] != -1)
					// Log.d(TAG, "MainGame: Storing dot matrix: [" + i + "][" + j + "] = " + matrix_Storage[i][j]);
			}
		}
		
		for(int i = 0; i < connectorsDiagDown_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagDown_Storage[i].length; j++) {
				connectorsDiagDown_Storage[i][j] = connectorsObj.connectorsDiagDown[i][j];
			}
		}
		
		for(int i = 0; i < connectorsDiagUp_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagUp_Storage[i].length; j++) {
				connectorsDiagUp_Storage[i][j] = connectorsObj.connectorsDiagUp[i][j];
			}
		}
		
		for(int i = 0; i < connectorsHorizontal_Storage.length; i++) {
			for(int j = 0; j < connectorsHorizontal_Storage[i].length; j++) {
				connectorsHorizontal_Storage[i][j] = connectorsObj.connectorsHorizontal[i][j];
			}
		}
		
		for(int i = 0; i < connectorsVertical_Storage.length; i++) {
			for(int j = 0; j < connectorsVertical_Storage[i].length; j++) {
				connectorsVertical_Storage[i][j] = connectorsObj.connectorsVertical[i][j];
			}
		}
		*/
	}
	
	

	public void setContext(Context context) {
		this.context = context;
		initSounds();
	}
}
