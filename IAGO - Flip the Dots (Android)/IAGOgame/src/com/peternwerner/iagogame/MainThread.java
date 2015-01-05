package com.peternwerner.iagogame;

import com.peternwerner.iagogame.MainGame;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author Peter Werner
 * @author impaler
 *
 * The Main thread which contains the game loop. The thread must have access to 
 * the surface view and holder to trigger events every game tick.
 */
@SuppressLint("WrongCall")
public class MainThread extends Thread {
	
	private static final String TAG = MainThread.class.getSimpleName();
	
	// game object
	private MainGame mainGameObj;
	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private MainGamePanel gamePanel;
	
	private boolean firstWake = true;
	
	// asset manager
	AssetManager assetManager;

	static int nMax = 10;
	// preference file score / state variables
	public SharedPreferences PREF_scores_fromMainActivity;
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
	static boolean didTutorial = false;
	static boolean unlockAll = false;
	//static boolean lockMed = false, lockMedRand = false, lockHard = false, lockHardRand = false, lockGenius = false, lockGeniusRand = false;
	static long timeLast = 0;
	
	private long timeReset = 10 * 60 * 1000;
	
	private long timeLastSaved = System.currentTimeMillis();

	
	
	// flag to hold game state 
	private boolean running;
	public void setRunning(boolean running) {
		this.running = running;
	}

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel, AssetManager _assetManager) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
		this.assetManager = _assetManager;
		
		// Log.d(TAG, "Initializing main thread object...");
	}

	@Override
	public void run() {

		// initialize game object
		mainGameObj = new MainGame();
		mainGameObj.init(gamePanel.getWidth(), gamePanel.getHeight(), assetManager);
		
		// set main game score variables
		setScorePrefs();
		
		// set main game state variables (or don't if enough time has passed since we last closed the app)
		if(System.currentTimeMillis() - timeLast < timeReset) {
			setStatePrefs();
			mainGameObj.initPrefState();
		}

		
		Canvas canvas;
				
		// Log.d(TAG, "Starting game loop");
		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					
					// check if the activity is not paused / stopped
					if(MainGamePanel.doPaint()) {
						/*
						// if this is the first update since creation / resuming, reset the main game object
						if(firstWake) {
							firstWake = false;
							// Log.d(TAG, "Thread: initializing new main game object...");
							
							mainGameObj = new MainGame();
							mainGameObj.init(gamePanel.getWidth(), gamePanel.getHeight(), assetManager);
						}
						*/
						if(mainGameObj != null) {
							// update game state 
							mainGameObj.update();
						
							// paint
							mainGameObj.paint(canvas);
						}
						
						// save scores once every 60 seconds
						if(System.currentTimeMillis() - timeLastSaved  >  60 * 1000) {
							timeLastSaved = System.currentTimeMillis();
						    commitPrefScores(PREF_scores_fromMainActivity.edit());
						}
						
					}
					else {
						firstWake = true;
					}
				}
			} finally {
				// in case of an exception the surface is not left in 
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}	// end finally
		}
	}

	public MainGame getMainGameObj() {
		return mainGameObj;
	}
	
	
	// when the app starts, initialize main game score variables from stored preference files
	public void initPrefScores(SharedPreferences PREF_scores) {
		
		didTutorial = PREF_scores.getBoolean("didTutorial", false);
		
		/*
		lockGenius = PREF_scores.getBoolean("lockGenius", false);
		lockGeniusRand = PREF_scores.getBoolean("lockGeniusRand", false);
		lockHard = PREF_scores.getBoolean("lockHard", false);
		lockHardRand = PREF_scores.getBoolean("lockHardRand", false);
		lockMed = PREF_scores.getBoolean("lockMed", false);
		lockMedRand = PREF_scores.getBoolean("lockMedRand", false);
		*/
		unlockAll = PREF_scores.getBoolean("unlockAll", false);
		
		for(int i = 0; i < scoreListBest.length; i++) {
			for(int j = 0; j < scoreListBest[i].length; j++) {
				
				scoreListBest[i][j]  = PREF_scores.getInt("scoreListBest["+i+"]["+j+"]", 0);
				scoreListStars[i][j] = PREF_scores.getInt("scoreListStars["+i+"]["+j+"]", 0);
			}
		}

		// Log.d(TAG, "Read preferences: scores");
	}
	
	
	// when the app closes, store main game score variables for the next time the app starts
	public void commitPrefScores(SharedPreferences.Editor editor) {
		
		editor.putBoolean("didTutorial", MainGame.didTutorial);
		
		/*
		editor.putBoolean("lockGenius", MainGame.lockGenius);
		editor.putBoolean("lockGeniusRand", MainGame.lockGeniusRand);
		editor.putBoolean("lockHard", MainGame.lockHard);
		editor.putBoolean("lockHardRand", MainGame.lockHardRand);
		editor.putBoolean("lockMed", MainGame.lockMed);
		editor.putBoolean("lockMedRand", MainGame.lockMedRand);
		*/
		editor.putBoolean("unlockAll", MainGame.unlockAll);
		
		for(int i = 0; i < scoreListBest.length; i++) {
			for(int j = 0; j < scoreListBest[i].length; j++) {
				
				editor.putInt("scoreListBest["+i+"]["+j+"]",  MainGame.scoreListBest[i][j]);
				editor.putInt("scoreListStars["+i+"]["+j+"]", MainGame.scoreListStars[i][j]);
			}
		}
		
		editor.commit();
		// Log.d(TAG, "Commited preferences: scores");
	}
	
	
	// when the app starts, initialize main game state variables from stored preference files
	public void initPrefStates(SharedPreferences PREF_states) {
		
		timeLast = PREF_states.getLong("timeLast", 0);
		
		soundEnabled = PREF_states.getBoolean("soundEnabled", false);
		gameState = PREF_states.getInt("gameState", 0);
		PREF_pageCurrent = PREF_states.getInt("PREF_pageCurrent", 1);
		n = PREF_states.getInt("n", 3);
		PREF_scoreNow = PREF_states.getInt("PREF_scoreNow", 0);

		for(int i = 0; i < matrix_Storage.length; i++) {
			for(int j = 0; j < matrix_Storage[i].length; j++) {
				matrix_Storage[i][j] = PREF_states.getInt("matrix_Storage["+i+"]["+j+"]", 0);				
			}
		}
		
		for(int i = 0; i < connectorsDiagDown_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagDown_Storage[i].length; j++) {
				connectorsDiagDown_Storage[i][j] = PREF_states.getInt("connectorsDiagDown_Storage["+i+"]["+j+"]", 0);
			}
		}
		
		for(int i = 0; i < connectorsDiagUp_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagUp_Storage[i].length; j++) {
				connectorsDiagUp_Storage[i][j] = PREF_states.getInt("connectorsDiagUp_Storage["+i+"]["+j+"]", 0);
			}
		}
		
		for(int i = 0; i < connectorsHorizontal_Storage.length; i++) {
			for(int j = 0; j < connectorsHorizontal_Storage[i].length; j++) {
				connectorsHorizontal_Storage[i][j] = PREF_states.getInt("connectorsHorizontal_Storage["+i+"]["+j+"]", 0);
			}
		}
		
		for(int i = 0; i < connectorsVertical_Storage.length; i++) {
			for(int j = 0; j < connectorsVertical_Storage[i].length; j++) {
				connectorsVertical_Storage[i][j] = PREF_states.getInt("connectorsVertical_Storage["+i+"]["+j+"]", 0);
			}
		}
		
		// Log.d(TAG, "Read preferences: states");
	}
	
	
	// when the app closes, store main game state variables for the next time the app starts
	public void commitPrefStates(SharedPreferences.Editor editor) {
		
		editor.putLong("timeLast", System.currentTimeMillis());
		
		// in mainGame: move all puzzle data into storage matrices
		mainGameObj.storePuzzle();
		
		editor.putBoolean("soundEnabled", MainGame.soundEnabled);
		editor.putInt("gameState" , MainGame.gameState);
		editor.putInt("PREF_pageCurrent" , MainGame.PREF_pageCurrent);
		editor.putInt("n" , MainGame.n);
		/*
		editor.putInt("PREF_scoreNow" , MainGame.scoreNow);
		
		for(int i = 0; i < matrix_Storage.length; i++) {
			for(int j = 0; j < matrix_Storage[i].length; j++) {
				editor.putInt("matrix_Storage["+i+"]["+j+"]", MainGame.matrix_Storage[i][j]);
				
				if(MainGame.matrix_Storage[i][j] != -1)
					// Log.d(TAG, "Storing dot matrix: [" + i + "][" + j + "] = " + MainGame.matrix_Storage[i][j]);
			}
		}
		
		for(int i = 0; i < connectorsDiagDown_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagDown_Storage[i].length; j++) {
				editor.putInt("connectorsDiagDown_Storage["+i+"]["+j+"]", MainGame.connectorsDiagDown_Storage[i][j]);
			}
		}
		
		for(int i = 0; i < connectorsDiagUp_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagUp_Storage[i].length; j++) {
				editor.putInt("connectorsDiagUp_Storage["+i+"]["+j+"]", MainGame.connectorsDiagUp_Storage[i][j]);
			}
		}
		
		for(int i = 0; i < connectorsHorizontal_Storage.length; i++) {
			for(int j = 0; j < connectorsHorizontal_Storage[i].length; j++) {
				editor.putInt("connectorsHorizontal_Storage["+i+"]["+j+"]", MainGame.connectorsHorizontal_Storage[i][j]);
			}
		}
		
		for(int i = 0; i < connectorsVertical_Storage.length; i++) {
			for(int j = 0; j < connectorsVertical_Storage[i].length; j++) {
				editor.putInt("connectorsVertical_Storage["+i+"]["+j+"]", MainGame.connectorsVertical_Storage[i][j]);
			}
		}
		*/
				
		editor.commit();
		// Log.d(TAG, "Commited preferences: states");
	}
	
	
	// set state variables based on stored PREFs
	void setStatePrefs() {
		
		MainGame.soundEnabled = soundEnabled;
		MainGame.gameState = gameState;
		if(gameState == 3)
			MainGame.PREF_pageCurrent = PREF_pageCurrent;
		MainGame.n = n;
		MainGame.PREF_scoreNow = PREF_scoreNow;
		
		for(int i = 0; i < matrix_Storage.length; i++) {
			for(int j = 0; j < matrix_Storage[i].length; j++) {
				MainGame.matrix_Storage[i][j]  = matrix_Storage[i][j];
			}
		}
		// Log.d(TAG, "SetStatePrefs(): top left dot = " + matrix_Storage[0][0]);
		
		for(int i = 0; i < connectorsDiagDown_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagDown_Storage[i].length; j++) {
				MainGame.connectorsDiagDown_Storage[i][j]  = connectorsDiagDown_Storage[i][j];
			}
		}
		
		for(int i = 0; i < connectorsDiagUp_Storage.length; i++) {
			for(int j = 0; j < connectorsDiagUp_Storage[i].length; j++) {
				MainGame.connectorsDiagUp_Storage[i][j]  = connectorsDiagUp_Storage[i][j];
			}
		}
		
		for(int i = 0; i < connectorsHorizontal_Storage.length; i++) {
			for(int j = 0; j < connectorsHorizontal_Storage[i].length; j++) {
				MainGame.connectorsHorizontal_Storage[i][j]  = connectorsHorizontal_Storage[i][j];
			}
		}
		
		for(int i = 0; i < connectorsVertical_Storage.length; i++) {
			for(int j = 0; j < connectorsVertical_Storage[i].length; j++) {
				MainGame.connectorsVertical_Storage[i][j]  = connectorsVertical_Storage[i][j];
			}
		}
		
		// Log.d(TAG, "Inputted preferences: states");
	}
	
	
	
	// set score variables stored in PREFs
	void setScorePrefs() {
		
		for(int i = 0; i < scoreListBest.length; i++) {
			for(int j = 0; j < scoreListBest[i].length; j++) {
				MainGame.scoreListBest[i][j]  = scoreListBest[i][j];
				MainGame.scoreListStars[i][j] = scoreListStars[i][j];
			}
		}
		MainGame.didTutorial = didTutorial;
		
		MainGame.unlockAll = unlockAll;
		/*
		MainGame.lockMed = lockMed;
		MainGame.lockMedRand = lockMedRand;
		MainGame.lockHard = lockHard;
		MainGame.lockHardRand = lockHardRand;
		MainGame.lockGenius = lockGenius;
		MainGame.lockGeniusRand = lockGeniusRand;
		*/
	}

}
