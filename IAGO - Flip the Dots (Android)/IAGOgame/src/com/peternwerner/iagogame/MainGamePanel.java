package com.peternwerner.iagogame;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author Peter Werner
 * @author impaler
 * This is the main surface that handles the ontouch events and draws
 * the image to the screen.
 */
public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	
	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private MainThread thread;
	
	private static boolean doPaintBool = true;

	public MainGamePanel(Context context, AssetManager assetManager) {
		super(context);
				
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create the game loop thread
		thread = new MainThread(getHolder(), this, assetManager);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created
		// Log.d(TAG, "Surface is being created");
		
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Log.d(TAG, "Surface is being destroyed");
				
	}
	
	
	
	public static boolean doPaint() {
		return doPaintBool;
	}
	public static void doPaint(boolean doPaint) {
		doPaintBool = doPaint;
	}
	
	public MainThread getThread() {
		return thread;
	}
	
}
