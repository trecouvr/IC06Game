package com.turlutu;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.android.angle.AnglePhysicObject;
import com.android.angle.AngleSound;
import com.android.angle.AngleSprite;

/**
 * extends AnglePhysicObject
 * 
 * @author Matthieu,Thomas
 *
 */
class Bonus extends AnglePhysicObject
{
	protected boolean mUsed;
	protected float mRadius;
	protected enum TypeBonus {	
		MOREJUMP, // 0 => Ball.jump()
		LESSJUMP, // 1 => Ball.jump()
		DISABLECHANGECOLOR, // 2 => Ball.changeColorLeft/Right()	
		ALLPLATEFORME, //3 => MyPhysicsEngine.kynetics()
		CHANGEPHYSICS, // 4 => MainActivity.onSensorChange() & GameUI.onTcouhEvent
		ADDSCORE,// 5=> GameUI.setBonus(..)
		BONUSX, //6  INACTIF
		BONUSINTERO, //7 INACTIF
		LIFE,// 8 => addlife
		NONE}; // 9 => none

	private GameUI mGame;
	private AngleSound sndTouch;
	private boolean mustdraw = true;
	private AngleSprite mSprite;
	private int mType = 0;
	
	static int radius = 16;
	static int nbtype = 49;
	static TypeBonus[] mapTypeBonus = {	
		TypeBonus.MOREJUMP, // 0
		TypeBonus.LESSJUMP, // 1
		TypeBonus.DISABLECHANGECOLOR, // 2
		TypeBonus.ALLPLATEFORME, // 3
		TypeBonus.CHANGEPHYSICS, // 4
		TypeBonus.ADDSCORE, // 5
		TypeBonus.BONUSX, // 6
		TypeBonus.BONUSINTERO, // 7
		TypeBonus.LIFE, //8
		TypeBonus.NONE};// 9
	static float[] timesActionBonus = {	4, // 0 MORE JUMP
		4, // LESSJUMP
		4, // DISABLE CHANGE COLOR
		6, // ALL PLATEFORME
		6, // CHANGE PHYSICS
		0, // ADDSCORE
		0, // INACTIF
		0, // INACTIF
		0, // ADDLIFE
		0}; // NONE 
	static int[] bonusOrder = { 
		0, 						// MOREJUMP
			1,					// LESSJUMP
			1,
		0,
				5,				// ADDSCORE
				5,
				5,
		0,	1,
					2,			// DISABLE CHANGE COLOR
					2,
					2,
					2,
		0,	1,	5,
						3,		// ALL PLATEFORME
						3,
						3,
						3,
						3,
		0,	1,	5,	2,
							4,	//CHANGEPHYSICS
							4,
							4,
							4,
							4,
							4,
		0,	1,	5,	2,	3,
								8, //LIFE
								8,
								8,
								8,
								8,
								8,
								8,
		0,	1,	5,	2,	3,	4
	};
	
	public Bonus(GameUI game, int d) // d compris entre 0 et 100 (difficulté croissante)
	{
		super(0, 1);
		mGame = game;
		mUsed = false;
		int range = (int) ((float) d / 100.f * nbtype);
		if(range>nbtype) {
			range = nbtype;
		}
		mType = bonusOrder[(int) (Math.random() * range)];
		Log.i("Strategie", "Bmax : "+ ( (float) d / 100.f * nbtype));
		sndTouch = mGame.sndBonus[mType];
		mSprite=new AngleSprite(mGame.mBonusLayout[mType]);
		addCircleCollider(new BallCollider(0, 0, radius));

	}
	
	
	/**
	 * @return surface
	 */
	@Override
	public float getSurface()
	{
		return (float) (mRadius * mRadius * 3.14);
	}

	/**
	 * Draw the sprite and/or colliders
	 * 
	 * @param gl
	 */
	@Override
	public void draw(GL10 gl)
	{
		if(mustdraw) {
			mSprite.mPosition.set(mPosition);
			mSprite.draw(gl);
			//drawColliders(gl,1f,0f,0f);
		}
	}
	
	public BallCollider collider()
	{
		return (BallCollider) mCircleColliders[0];
	}

	protected void onDie()
	{
		if (mUsed)
		{
			Log.i("Bonus", "Bonus onDie debut");
			Log.i("Bonus", "TypeBonus : "+mapTypeBonus[mType]+" "+mType);
			sndTouch.play(((MainActivity)mGame.mActivity).mOptions.mVolume / 100,false);
			if(mapTypeBonus[mType] == TypeBonus.LIFE) {
				// Ajout d'une vie
				mGame.mLife.add();
			} else if (mapTypeBonus[mType] == TypeBonus.ADDSCORE) {
				// Ajout de score
				mGame.mScore += 600;
			} else {
				mGame.setBonus(mapTypeBonus[mType],timesActionBonus[mType]);
			}
			Log.i("Bonus", "Bonus onDie fin");
		}
	}
	
};
