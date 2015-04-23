package com.agel.arch.mangaview.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.agel.arch.mangaview.data.Settings;
import com.agel.arch.mangaview.data.ZoomState;

@Deprecated
public class ViewManga extends ImageView implements TouchInputListener.TouchObserver {

    private final ZoomState zoomState = new ZoomState(Settings.getInstance().ZoomFactor);
    private final TouchInputListener zoomListener = new TouchInputListener();

    private final Paint mBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Paint mDebugPaint = new Paint();
	public Bitmap mBitmap;

    private Rect viewDimensions = new Rect();
    private Rect imageDimensions = new Rect();
    private Rect dirtyRect = new Rect();
    private Matrix matrix = new Matrix();
    private float mtxValues[] = {0,0,0,0,0,0,0,0,0};

    public ViewManga(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ViewManga(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        zoomListener.setZoomState(zoomState);
        zoomListener.addChangeListener(this);

        setOnTouchListener(zoomListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        zoomListener.removeChangeListener(this);
        setOnTouchListener(null);
    }

    public void initViewDimensions() {
        getWindowVisibleDisplayFrame(viewDimensions);
    }

    public void setImage(Bitmap bmp) {

        //Calculate image displaying parameters
        imageDimensions.left = 0;
        imageDimensions.top = 0;
        imageDimensions.right = bmp.getWidth();
        imageDimensions.bottom = bmp.getHeight();

        matrix.setRectToRect(new RectF(imageDimensions), new RectF(viewDimensions), Matrix.ScaleToFit.CENTER);
        matrix.getValues(mtxValues);

        //Set bitmap
		if(mBitmap != null)
    		mBitmap.recycle();
		mBitmap = null;

		mBitmap = bmp;

        //Reset zoom
		zoomState.setZoomState(ZoomState.ZOOM_STATE_NONE);
        //Redraw
		invalidate();
	}

    public boolean hasImage() {
        return mBitmap != null;
    }

    @Override
	protected void onDraw(@NonNull Canvas canvas) {
		if(mBitmap != null)
		{
			canvas.drawBitmap(mBitmap,matrix,mBitmapPaint);

            if(zoomState.getZoomState() != ZoomState.ZOOM_STATE_NONE) {
                mDebugPaint.setColor(0xFFFF0000);
                canvas.drawRect(zoomState.getRectDst(), mDebugPaint);
            }
		}
	}

    @Override
    public void onTouchAction() {
        redraw();
    }

    public void redraw() {

        //Calculate zoom/pan and area needed to be redrawn
        dirtyRect.set(zoomState.getRectDst());

        if(zoomState.getZoomState() != ZoomState.ZOOM_STATE_NONE)
        {
            zoomState.calcRectangles(mtxValues,imageDimensions,viewDimensions);
        }

        dirtyRect.union(zoomState.getRectDst());

        boolean dirty = !dirtyRect.equals(new Rect(0, 0, 0, 0));

        if(dirty)
            invalidate(dirtyRect);
        else
            invalidate();
    }

    public void onConfigurationChanged() {
        getWindowVisibleDisplayFrame(viewDimensions);

        matrix.setRectToRect(new RectF(imageDimensions), new RectF(viewDimensions), Matrix.ScaleToFit.CENTER);
        matrix.getValues(mtxValues);

        invalidate();
    }

    public ZoomState getZoomState() {
        return zoomState;
    }
}
