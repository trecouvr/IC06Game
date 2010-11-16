package com.turlutu;

import android.graphics.Typeface;
import android.view.MotionEvent;

import com.android.angle.AngleActivity;
import com.android.angle.AngleFont;
import com.android.angle.AngleObject;
import com.android.angle.AngleSprite;
import com.android.angle.AngleSpriteLayout;
import com.android.angle.AngleString;
import com.android.angle.AngleUI;

public class MenuUI  extends AngleUI
{
	
	private AngleObject ogMenuTexts;
	
	private AngleString strPlay;
	private AngleString strHiScore;
	private AngleString strExit;
	private AngleString strOptions;

	private int mHiScore;

	public MenuUI(AngleActivity activity)
	{
		super(activity);
		ogMenuTexts = new AngleObject();

		
		addObject(new AngleSprite(160, 240, new AngleSpriteLayout(mActivity.mGLSurfaceView, 320, 480,  com.turlutu.R.drawable.bg_menu)));
		
		addObject(ogMenuTexts);

		AngleFont fntCafe=new AngleFont(mActivity.mGLSurfaceView, 25, Typeface.createFromAsset(mActivity.getAssets(),"cafe.ttf"), 222, 0, 0, 30, 200, 255, 255);
		
		strPlay = (AngleString) ogMenuTexts.addObject(new AngleString(fntCafe, "Play", 160, 160, AngleString.aCenter));
		strHiScore = (AngleString) ogMenuTexts.addObject(new AngleString(fntCafe, "Hi Score", 160, 210, AngleString.aCenter));
		strOptions = (AngleString) ogMenuTexts.addObject(new AngleString(fntCafe, "Options", 160, 260, AngleString.aCenter));
		strExit = (AngleString) ogMenuTexts.addObject(new AngleString(fntCafe, "Exit", 160, 390, AngleString.aCenter));
		
		//This is our structure right now:
		//---------------------------
		//mGLSurfaceView
		// >mTheGame
		// >mTheMenu
		//   >sprBackground
		//   >ogMenuTexts
		//     >strPlay
		//     >strHiScore
		//     >strExit
		//---------------------------
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			float eX = event.getX();
			float eY = event.getY();

			if (strPlay.test(eX, eY))
				((MainActivity) mActivity).setUI(((MainActivity) mActivity).mGame);
			if (strOptions.test(eX, eY))
				((MainActivity) mActivity).setUI(((MainActivity) mActivity).mOptions);
			else if (strExit.test(eX, eY))
				mActivity.finish();

			return true;
		}
		return false;
	}

	@Override
	public void onActivate()
	{
		if (((MainActivity) mActivity).mGame.mScore>mHiScore)
				mHiScore=((MainActivity) mActivity).mGame.mScore;
		strHiScore.set("Hi Score: "+mHiScore);
		super.onActivate();
	}
	
}