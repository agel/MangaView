package com.agel.arch.mangaview.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

public class MangaImageView extends View {
    public Bitmap imageBitmap;
    private final Paint imagePaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private RectF viewDimensions = new RectF();
    private RectF imageDimensions = new RectF();
    private Matrix scaleMatrix = new Matrix();

    public MangaImageView(Context context) {
        super(context);
    }
    public MangaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MangaImageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public boolean hasImage() {
        return imageBitmap != null;
    }

    public void setImage(Bitmap bmp) {

        //Set new image dimensions
        imageDimensions.left = 0;
        imageDimensions.top = 0;
        imageDimensions.right = bmp.getWidth();
        imageDimensions.bottom = bmp.getHeight();

        //Calculate scaling matrix
        scaleMatrix.setRectToRect(imageDimensions, viewDimensions, Matrix.ScaleToFit.CENTER);

       //Cleanup memory from old bitmap
        if(imageBitmap != null)
            imageBitmap.recycle();

        //Set new
        imageBitmap = bmp;

        //Redraw
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if(imageBitmap != null)
        {
            canvas.drawBitmap(imageBitmap, scaleMatrix, imagePaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //If view dimensions have changed
        if(viewDimensions.left != l ||  viewDimensions.top != t
        || viewDimensions.right != r || viewDimensions.bottom != b) {
            //Save new view dimensions
            viewDimensions.left = l;
            viewDimensions.top = t;
            viewDimensions.right = r;
            viewDimensions.bottom = b;

            //If we have a bitmap ready
            if(imageBitmap != null) {
                //recalculate scaling matrix
                scaleMatrix.setRectToRect(imageDimensions, viewDimensions, Matrix.ScaleToFit.CENTER);
            }
        }
    }
}
