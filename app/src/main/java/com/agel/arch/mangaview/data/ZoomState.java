package com.agel.arch.mangaview.data;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

public class ZoomState {
    public static final String TAG = "ZoomState";
    public final static int ZOOM_STATE_NONE = 0;
    public final static int ZOOM_STATE_ZOOM = 1;
    public final static int ZOOM_STATE_PAN = 2;

    private int mState = ZoomState.ZOOM_STATE_NONE;
	private int mSizeX = 0;
	private int mSizeY = 0;
	private int mPanX = 0;
	private int mPanY = 0;
	private int mPrevPanPointX = 0;
	private int mPrevPanPointY = 0;
	private int mZoomFactor;
	private Point mDispCenter;
	private PointF mZoomCenter;
	private Rect mZoomSrc = new Rect();
	private Rect mZoomDst = new Rect();
	
	public ZoomState(int zoomF) {
		mZoomFactor = zoomF;
	}

	public void setZoomState(int state) {
        if (mState == state) return;

        mState = state;
        if (state == ZOOM_STATE_NONE) {
            mZoomSrc = new Rect();
            mZoomDst = new Rect();
        }

        Log.d(TAG, "Zoom state changed: " + Integer.toString(state));
    }
	
	public int getZoomState() {
		return mState;
	}
	
	public void setCenter(Point touchCoord) {
		mDispCenter = new Point(touchCoord);
		mZoomCenter = new PointF(touchCoord);
		mPanX = 0;
		mPanY = 0;
	}
	
	public void setPan(int x, int y) {
		if (Math.abs(mPrevPanPointX - x) > 5 || Math.abs(mPrevPanPointY - y) > 5) {
			mPanX = mPrevPanPointX - x;
			mPanY = mPrevPanPointY - y;
			mPrevPanPointX = x;
			mPrevPanPointY = y;
		}
	}
	
	public void setPanStart(int x, int y) {
		 mPrevPanPointX = x;
		 mPrevPanPointY = y;
	}
		
	public Rect getRectSrc() {		
		return mZoomSrc;
	}
	
	public Rect getRectDst() {		
		return mZoomDst;
	}
	
	public void setSize(int sizeX, int sizeY) {
        if (sizeX != mSizeX || sizeY != mSizeY) {
            mSizeX = sizeX;
            mSizeY = sizeY;
        }
    }	
		
	public void calcRectangles(float mtxValues[], Rect img, Rect scr) {
				
		if(mState == ZOOM_STATE_ZOOM)
		{		
			mZoomDst.left = (mDispCenter.x - Math.abs(mDispCenter.x - mSizeX));
			mZoomDst.top = (mDispCenter.y - Math.abs(mDispCenter.y - mSizeY));
			mZoomDst.right = (mDispCenter.x + Math.abs(mDispCenter.x - mSizeX));
			mZoomDst.bottom = (mDispCenter.y + Math.abs(mDispCenter.y - mSizeY));
			
			//Enforcing Zoom limits
			if(mZoomDst.left < 0)
				mZoomDst.left = 0;
						
			if(mZoomDst.top < 0)			
				mZoomDst.top = 0;
					
			if(mZoomDst.right > scr.right)			
				mZoomDst.right = scr.right;
				
			if(mZoomDst.bottom > scr.bottom)			
				mZoomDst.bottom = scr.bottom;			
		}
		
		mZoomCenter.offset(mPanX * mtxValues[0], mPanY * mtxValues[0]);
		
		float centX = ((mZoomCenter.x - mtxValues[2]) / mtxValues[0]);
		float centY = ((mZoomCenter.y - mtxValues[5]) /  mtxValues[0]);
		float sizeX = (mZoomDst.right - mZoomDst.left) / 2;
		float sizeY = (mZoomDst.bottom - mZoomDst.top) / 2;
			
		//ZoomFactor adjusted by user 		
		sizeX = (float) (sizeX  * 0.5 / (mtxValues[0] * (mZoomFactor/100.0)));
		sizeY = (float) (sizeY  * 0.5 / (mtxValues[0] * (mZoomFactor/100.0)));
		
		mZoomSrc.left = (int) (centX - sizeX);
		mZoomSrc.top = (int) (centY - sizeY);
		mZoomSrc.right = (int) (centX + sizeX);
		mZoomSrc.bottom = (int) (centY + sizeY);
		
		//Enforcing pan limits
		if(mZoomSrc.left < 0)
		{
			mZoomSrc.left = 0;
			mZoomSrc.right = (int) (sizeX * 2);
			mZoomCenter.x = (sizeX * mtxValues[0] + mtxValues[2]);
		}
		if(mZoomSrc.top < 0)
		{
			mZoomSrc.top = 0;
			mZoomSrc.bottom = (int) (sizeY * 2);
			mZoomCenter.y = (sizeY * mtxValues[0] + mtxValues[5]);
		}

		if(mZoomSrc.right > img.right)
		{
			mZoomSrc.right = img.right;
			mZoomSrc.left = (int) (img.right - 2 * sizeX);			
			mZoomCenter.x = ((img.right - sizeX) * mtxValues[0] + mtxValues[2]);
		}

		if(mZoomSrc.bottom > img.bottom)
		{
			mZoomSrc.bottom = img.bottom;
			mZoomSrc.top = (int) (img.bottom - 2 * sizeY);			
			mZoomCenter.y = ((img.bottom - sizeY) * mtxValues[0] + mtxValues[5]);
		}	
	}

	public void setZoomFactor(int mZoomFactor) {
		this.mZoomFactor = mZoomFactor;		
	}

	public int getZoomFactor() {
		return mZoomFactor;
	}
}
