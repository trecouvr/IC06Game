package com.android.tutorial;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.android.angle.AngleActivity;
import com.android.angle.AngleCircleCollider;
import com.android.angle.AngleFont;
import com.android.angle.AngleObject;
import com.android.angle.AnglePhysicObject;
import com.android.angle.AnglePhysicsEngine;
import com.android.angle.AngleSegmentCollider;
import com.android.angle.AngleSprite;
import com.android.angle.AngleSpriteLayout;
import com.android.angle.AngleString;
import com.android.angle.AngleUI;
import com.android.angle.FPSCounter;

/**
 * Use some pseudo-phisyc
 * >Usando algo de pseudo-f�sica
 * 
 * 
 * @author Ivan Pajuelo
 * 
 */
public class Matthieu extends AngleActivity
{
	private MyDemo mDemo;
	private AngleObject mBoard;
	
   private final SensorEventListener mListener = new SensorEventListener() 
   {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
			{
				mDemo.setGravity(-10*event.values[0],10*event.values[1]);
				
				//mDemo.setTexte(Float.toString(event.values[0]));
				
				//Ridicule de vouloir fixer la gravité ici pour passer en emulateur car en emulateur on rentre pas dans cette fonction
				//mDemo.setGravity(0,100);
			}
		}
   };

	private SensorManager mSensorManager; 	

	private class Ball extends AnglePhysicObject
	{
		private AngleSprite mSprite;

		public Ball(AngleSpriteLayout layout)
		{
			super(0, 1);
			mSprite=new AngleSprite(layout);
			addCircleCollider(new AngleCircleCollider(0, 0, 29));
			mMass = 10;
			mBounce = 0.8f; // Coefficient of restitution (1 return all the energy)  >Coeficiente de restituci�n (1 devuelve toda la energia)
		}

		@Override
		public float getSurface()
		{
			return 29 * 2; // Radius * 2  >Radio * 2
		}

		@Override
		public void draw(GL10 gl)
		{
			mSprite.mPosition.set(mPosition);
			mSprite.draw(gl);
			//Draw colliders (beware calls GC)
			//>Dibujado de los lolisionadores (cuidado, llama al GC)
			//drawColliders(gl);
		}
		
		
	};

	private class MyDemo extends AngleUI
	{
		AngleSpriteLayout mBallLayout;
		AnglePhysicsEngine mPhysics;
		
		public MyDemo(AngleActivity activity)
		{
			super(activity);
			mBallLayout = new AngleSpriteLayout(mGLSurfaceView, 64, 64, R.drawable.ball, 0, 0, 128, 128);
			mPhysics=new AnglePhysicsEngine(20);
			mPhysics.mViscosity = 0f; // Air viscosity >Viscosidad del aire
			addObject(mPhysics);
			
			mBoard=addObject(new AngleObject());
			//The dashboard background
			AngleSpriteLayout slDash = new AngleSpriteLayout(mActivity.mGLSurfaceView, 320, 64, R.drawable.tilemap, 0, 32, 320, 64);
			AngleSprite mDash=(AngleSprite)mBoard.addObject(new AngleSprite (slDash));
			mDash.mPosition.set(160, 480-slDash.roHeight/2);
			mDash.mAlpha=0.5f;

			//Font and text
			mDemo.setTexte(1.0f);
			
			
			// Add 4 segment colliders to simulate walls
			//>A�adimos 2 colisionadores de segmento para simular las paredes
			AnglePhysicObject mWall = new AnglePhysicObject(1, 0);
			mWall.mPosition.set(160, 479);
			mWall.addSegmentCollider(new AngleSegmentCollider(-160, 0, 160, 0));
			mWall.mBounce = 0.5f;
			mPhysics.addObject(mWall); // Down wall
			
			mWall = new AnglePhysicObject(1, 0);
			mWall.mPosition.set(160, 0);
			mWall.addSegmentCollider(new AngleSegmentCollider(160, 0, -160, 0));
			mWall.mBounce = 0.5f;
			mPhysics.addObject(mWall); // Up wall
			
			mWall = new AnglePhysicObject(1, 0);
			mWall.mPosition.set(319, 240);
			mWall.addSegmentCollider(new AngleSegmentCollider(0, 240, 0, -240));
			mWall.mBounce = 0.5f;
			mPhysics.addObject(mWall); // Right wall
			
			mWall = new AnglePhysicObject(1, 0);
			mWall.mPosition.set(0, 240);
			mWall.addSegmentCollider(new AngleSegmentCollider(0, -240, 0, 240));
			mWall.mBounce = 0.5f;
			mPhysics.addObject(mWall); // Left wall
			
			mWall = new AnglePhysicObject(1, 0);
			mWall.mPosition.set(0, 100);
			mWall.addSegmentCollider(new AngleSegmentCollider(0, -100, 0, 100));
			mWall.mBounce = 1f;
			mPhysics.addObject(mWall); // Left wall
		}

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			//Pour l'emulateur
			mDemo.setGravity(-10,0);
			
			if (event.getAction()==MotionEvent.ACTION_DOWN)
			{
				if ((event.getX()>30)&&(event.getY()>30)&&(event.getX()<320-30)&&(event.getY()<480-30))
				{
					mDemo.setTexte(event.getX());
					Ball mBall = new Ball (mBallLayout);
					mBall.mPosition.set(event.getX(), event.getY());
					// Ensure that there isn't any ball in this place
					// >Nos aseguramos de que ninguna pelota ocupa esta posici�n
					for (int b = 0; b < mPhysics.count(); b++)
					{
						AngleObject O=mPhysics.childAt(b);
						if (O instanceof Ball)
							if (mBall.test((Ball)O))
								return true;
					}
					mPhysics.addObject(mBall);
				}
				return true;
			}
			return super.onTouchEvent(event);
		}

		public void setGravity(float x, float y)
		{
			mPhysics.mGravity.set(x,y);
		}
		
		public void setTexte(float x)
		{
			AngleFont fntCafe25 = new AngleFont(mActivity.mGLSurfaceView, 25, Typeface.createFromAsset(getAssets(),"cafe.ttf"), 222, 0, 0, 30, 200, 255, 255);
			mBoard.addObject(new AngleString(fntCafe25,Float.toString(x),160,440,AngleString.aCenter));
		}
		
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

      mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
      
		mGLSurfaceView.addObject(new FPSCounter());

		FrameLayout mMainLayout=new FrameLayout(this);
		mMainLayout.addView(mGLSurfaceView);
		setContentView(mMainLayout);
		
		mDemo=new MyDemo(this);
		setUI(mDemo);
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