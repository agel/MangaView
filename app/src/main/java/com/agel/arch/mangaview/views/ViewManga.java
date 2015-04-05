package com.agel.arch.mangaview.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.agel.arch.mangaview.data.ZoomControlType;
import com.agel.arch.mangaview.data.ZoomStateController;

public class ViewManga extends ImageView {

	public ViewManga(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
	}

	public ViewManga(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}

	public Bitmap mBitmap;
	private ZoomStateController mState;
	
	private boolean mShowPageNumer = false;
	private Point mCurrentPage = new Point(0,0);
	
	public void setCurrentPage(Point currentPage) {
		currentPage.x += 1;
		this.mCurrentPage = currentPage;
	}

	public void setShowPageNumer(boolean showPageNumer) {
		this.mShowPageNumer = showPageNumer;
	}

	private final Paint mBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Matrix matrix;
	private float[] mtxValues = {0,0,0,0,0,0,0,0,0};
		
	public ViewManga(Context context) {
		super(context);
		
		mTextPaint.setTextSize(40);	
		mTextPaint.setColor(Color.BLUE);
		mTextPaint.setAlpha(128);
	}

	public void setImage(Bitmap bmp) {
		
		if(mBitmap != null)
    		mBitmap.recycle();
		mBitmap = null;
		System.gc();
		
		mBitmap = bmp;		
		if(mState != null)
			mState.setZoomState(ZoomControlType.NONE);
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//TODO implement drawing to bitmap
		if(mBitmap != null)
		{
			canvas.drawBitmap(mBitmap,matrix,mBitmapPaint);
			
			if(mState != null)
				if(mState.getZoomState() != ZoomControlType.NONE && mtxValues[0] < 1)								
					canvas.drawBitmap(mBitmap,mState.getRectSrc(),mState.getRectDst(),mBitmapPaint);			
		}		
		if(mShowPageNumer)			
			canvas.drawText(mCurrentPage.x + "/" + mCurrentPage.y, 0, 50, mTextPaint);
	}
	
	public void setZoomState(ZoomStateController state) {
       mState = state;
    }
	
	public void setMatrix(Matrix mtx) {
		matrix = mtx;
		matrix.getValues(mtxValues);
	}
}
