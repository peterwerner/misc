package com.peternwerner.iagogame;



/*
 * @author: Peter Werner
 */

import java.io.File;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class MainActivity extends Activity {
	
    private AdView adView;
    private static final String AD_UNIT_ID = "ca-app-pub-5197429488263868/2316672636";
    
    LinearLayout layout;
	
	private static final String TAG = MainActivity.class.getSimpleName();
	private static AssetManager assetManager;
	
	public static int adHeight;
	
	// class objects
	MainGamePanel mainGamePanelObj;
	
	public static final String PREFS_NAME_scores = "MyPrefsFile_scores";
    public static final String PREFS_NAME_states = "MyPrefsFile_states";
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Log.d(TAG, "MainActivity: onCreate...");
		
		// Remove title bar and notification bar (this must come before adding any content)
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    // Force portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    layout = (LinearLayout) findViewById(R.id.linearLayout);
	    
		// add adView
	    createAd();
	    
	    adHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
	}
	
	@Override
	protected void onPause() {
				
		layout.removeViewAt(1);
        
        // Pause the game
		
        // Pause the AdView.
        if(adView != null)
        	adView.pause();
		
		super.onPause();
		// Log.d(TAG, "MainActivity: onPause...");
		
		MainGamePanel.doPaint(false);
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		// Log.d(TAG, "MainActivity: onResume...");
					
		// create asset manager
		assetManager = getAssets();
		
		// set MainGamePanel as the View
		mainGamePanelObj = new MainGamePanel(this, assetManager);
	    layout.addView(mainGamePanelObj, 1);
	    
        // Log.d(TAG, "View added");
                        
		MainGamePanel.doPaint(true);
		
		mainGamePanelObj.getThread().PREF_scores_fromMainActivity = getSharedPreferences(PREFS_NAME_scores, 0);
		
		// Load score preferences
	    SharedPreferences PREF_scores = getSharedPreferences(PREFS_NAME_scores, 0);
	    mainGamePanelObj.getThread().initPrefScores(PREF_scores);
	    // Load state preferences
	 	SharedPreferences PREF_states = getSharedPreferences(PREFS_NAME_states, 0);
	 	mainGamePanelObj.getThread().initPrefStates(PREF_states);
	 	
	    
        // Resume the AdView.
        if(adView != null)
        	adView.resume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// Log.d(TAG, "MainActivity: onStop...");
		
		// Input preferences
	    SharedPreferences PREF_scores = getSharedPreferences(PREFS_NAME_scores, 0);
	    SharedPreferences.Editor editor = PREF_scores.edit();
	    mainGamePanelObj.getThread().commitPrefScores(editor);
	    SharedPreferences PREF_states = getSharedPreferences(PREFS_NAME_states, 0);
	    editor = PREF_states.edit();
	    mainGamePanelObj.getThread().commitPrefStates(editor);
	   		
		MainGamePanel.doPaint(false);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Log.d(TAG, "MainActivity: onStart...");
		
		MainGamePanel.doPaint(true);
	}
	
	@Override
	protected void onDestroy() {
		
        // Destroy the AdView.
        if(adView != null)
			adView.destroy();
		
		super.onDestroy();
		// Log.d(TAG, "MainActivity: onDestroy...");
		
		SharedPreferences PREF_states = getSharedPreferences(PREFS_NAME_states, 0);
	    SharedPreferences.Editor editor = PREF_states.edit();
	    editor.clear();
	    editor.commit();
		
		MainGamePanel.doPaint(false);
	}
	
	
	
	public void createAd() {
		
	    // Create an ad.
	    adView = new AdView(this);
	    adView.setAdSize(AdSize.BANNER);
	    adView.setAdUnitId(AD_UNIT_ID);

	    adView.setBackgroundColor(Color.rgb(254,250,245));
	    
	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    layout.addView(adView, 0);
	    
	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        //.addTestDevice("3CBB7C2D3A769669025EA988390E6ED7")
	        .build();

	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event){ 
	    int action = MotionEventCompat.getActionMasked(event);
	    
	    mainGamePanelObj.getThread().getMainGameObj().setContext(getApplicationContext());
	    
	    float x = event.getX();
	    float y = event.getY() - adHeight;
	    
	    switch(action) {
		    case (MotionEvent.ACTION_DOWN) :
	            // Log.d(TAG,"Action was DOWN");
		    	mainGamePanelObj.getThread().getMainGameObj().isPressing = true;
		    	mainGamePanelObj.getThread().getMainGameObj().onTouchDown(x, y);
	            return true;
	            
	        case (MotionEvent.ACTION_MOVE) :
	            // Log.d(TAG,"Action was MOVE");
	            return true;
	            
	        case (MotionEvent.ACTION_UP) :
	            // Log.d(TAG,"Action was UP");
	        	mainGamePanelObj.getThread().getMainGameObj().onTouchUp(x, y);
	        	mainGamePanelObj.getThread().getMainGameObj().isPressing = false;
	            return true;
	            
	        case (MotionEvent.ACTION_CANCEL) :
	            // Log.d(TAG,"Action was CANCEL");
	            return true;
	            
	        case (MotionEvent.ACTION_OUTSIDE) :
	            // Log.d(TAG,"Movement occurred outside bounds of current screen element");
	            return true;      
	        default : 
	            return super.onTouchEvent(event);
	    }      
	    
	}
	
	
	@Override
	public void onBackPressed() {
		
		if(mainGamePanelObj.getThread().getMainGameObj() == null)
			super.onBackPressed();
		
		else if(mainGamePanelObj.getThread().getMainGameObj().gameState == 0)
			super.onBackPressed();
			
		else
			mainGamePanelObj.getThread().getMainGameObj().doBack = true;
		
	}
}
