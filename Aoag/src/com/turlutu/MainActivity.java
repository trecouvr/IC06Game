package com.turlutu;




import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.FrameLayout;

import com.android.angle.AngleActivity;
import com.android.angle.AngleFont;
import com.android.angle.FPSCounter;
import com.turlutu.Bonus.TypeBonus;


public class MainActivity extends AngleActivity
{
	protected GameUI mGame;
	protected MenuUI mMenu;
	protected ScoresUI mScores;
	protected OptionsUI mOptions;
	protected InstructionsUI mInstructions;
	protected OnLineScoresUI mScoresOnLine;
	private boolean loaded = false;
	protected FrameLayout mMainLayout;
	protected int mSensibility;
	protected MainActivity mActivity;
	protected ProgressDialog  dialog;
	protected AngleFont fntGlobal, fntGlobal1;
	protected Vibrator mVibrator;
	private final SensorEventListener mListener = new SensorEventListener() 
	{
		//@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}
		
		//@Override
		public void onSensorChanged(SensorEvent event)
		{
			if (loaded & event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
			{
				mGame.mBall.mVelocity.mX = (-mOptions.mSensibility*2*event.values[0]);
				mOptions.mBall.mVelocity.mX = (-mOptions.mSensibility*2*event.values[0]);
				if (mGame.mTypeBonus == TypeBonus.CHANGEPHYSICS)
					mGame.mBall.mVelocity.mX = -mGame.mBall.mVelocity.mX;
			}
		}
	};
	private SensorManager mSensorManager; 	

	   
	public MainActivity()
	{
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.i("MainActivity", "START");
		
		mActivity = this;
		new Thread() 
		{
			@Override 
			public void run() 
			{
				Looper.prepare();
		        dialog = ProgressDialog.show(mActivity, "", 
                        "Chargement en cours, veuillez patienter...", true);
				Looper.loop();
			}
		}.start();
		super.onCreate(savedInstanceState);
		
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
		mVibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);   
		
        // a commenté dans la version finale (pour voir la fluidité du jeu)      
		mGLSurfaceView.addObject(new FPSCounter());
		
		fntGlobal = new AngleFont(this.mGLSurfaceView, 25, Typeface.createFromAsset(this.getAssets(),"cafe.ttf"), 222, 0, 0, 30, 30, 30, 255);
		// Text of instructions
		fntGlobal1 = new AngleFont(this.mGLSurfaceView, 17, Typeface.createFromAsset(this.getAssets(),"cafe.ttf"), 222, 0, 0, 30, 30, 30, 255);
		mMainLayout=new FrameLayout(this);
		mMainLayout.addView(mGLSurfaceView);
		setContentView(mMainLayout);

		new Thread() 
		{
			@Override 
			public void run() 
			{
				load();
			}
		}.start();

		
		Log.i("MainActivity", "FIN");
	}
	
	public void load()
	{
		Log.i("MainActivity", "Load() start");
		mOptions=new OptionsUI(this);
		mInstructions= new InstructionsUI(this);
		mScores=new ScoresUI(this);
		mScoresOnLine = new OnLineScoresUI(this);
		mGame=new GameUI(this);
		mGame.setGravity(0f,10f);
		mMenu=new MenuUI(this);
		setUI(mMenu);
		Log.i("MainActivity", "Load() fin");
		loaded = true;
	}

	//Overload onPause and onResume to enable and disable the accelerometer
	//Sobrecargamos onPause y onResume para activar y desactivar el aceler�metro
	@Override
	protected void onPause()
	{
      mSensorManager.unregisterListener(mListener); 
      
      super.onPause();
	}


	@Override
	protected void onResume()
	{
      mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST); 		

		super.onResume();
	}
	
}
