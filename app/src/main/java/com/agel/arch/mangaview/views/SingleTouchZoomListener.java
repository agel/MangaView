package com.agel.arch.mangaview.views;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class SingleTouchZoomListener implements View.OnTouchListener {
    private final static String TAG = "SingleTouchZoomListener";
    public final static int ZOOM_STATE_NONE = 0;
    public final static int ZOOM_STATE_ZOOM = 1;
    public final static int ZOOM_STATE_PAN = 2;

    public interface ZoomStateChangedListener {
        void onZoomStateChanged(Rect screenPosition, Point screenPan);
    }

    private ZoomStateChangedListener listener = null;
    private int currentState = ZOOM_STATE_NONE;

    private Rect currentZoom = new Rect();
    private Point currentPan = new Point();
    private Point prevPan = new Point();

    private Point zoomCenter = new Point();
    private Point touchStart = new Point();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Point touchCoordinate = new Point((int)event.getX(),(int)event.getY());
        final int action = event.getAction();
        double distance = Math.sqrt(Math.pow(Math.abs(touchCoordinate.x - touchStart.x), 2) + Math.pow(Math.abs(touchCoordinate.y - touchStart.y), 2));

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchStart = new Point(touchCoordinate);

                if(currentState == ZOOM_STATE_NONE) {
                    currentState = ZOOM_STATE_ZOOM;
                    zoomCenter = new Point(touchCoordinate);
                } else if(currentZoom.contains(touchCoordinate.x, touchCoordinate.y)) {
                    if(currentState == ZOOM_STATE_ZOOM) {
                        currentState = ZOOM_STATE_PAN;
                    } else {
                        prevPan = new Point(currentPan);
                    }
                } else {
                    currentState = ZOOM_STATE_ZOOM;
                    clearRectangles();
                    zoomCenter = new Point(touchCoordinate);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == ZOOM_STATE_ZOOM) {
                    currentZoom.left = zoomCenter.x - Math.abs(zoomCenter.x - touchCoordinate.x);
                    currentZoom.top = zoomCenter.y - Math.abs(zoomCenter.y - touchCoordinate.y);
                    currentZoom.right = zoomCenter.x + Math.abs(zoomCenter.x - touchCoordinate.x);
                    currentZoom.bottom = zoomCenter.y + Math.abs(zoomCenter.y - touchCoordinate.y);
                } else if(currentState == ZOOM_STATE_PAN) {
                    currentPan.x = prevPan.x + touchStart.x - touchCoordinate.x;
                    currentPan.y = prevPan.y + touchStart.y - touchCoordinate.y;
                } else {
                    break;
                }
                if(distance > 5) {
                    listener.onZoomStateChanged(currentZoom, currentPan);
                }
                break;
            case MotionEvent.ACTION_UP :
                //TODO measure distance in dp
                if(distance < 5) {
                    currentState = ZOOM_STATE_NONE;
                    clearRectangles();
                }
                listener.onZoomStateChanged(currentZoom, currentPan);
                break;
        }
//        Log.d(TAG, String.format("Center: %s, Zoom: w%s h%s", zoomCenter, currentZoom.right - currentZoom.left, currentZoom.bottom - currentZoom.top));
        return true;
    }

    public void reset()
    {
        currentState = ZOOM_STATE_NONE;
        zoomCenter.x = 0;
        zoomCenter.y = 0;
        clearRectangles();
        listener.onZoomStateChanged(currentZoom, currentPan);
    }

    private void clearRectangles() {
        currentZoom.left = 0;
        currentZoom.top = 0;
        currentZoom.right = 0;
        currentZoom.bottom = 0;
        currentPan.x = 0;
        currentPan.y = 0;
        prevPan.x = 0;
        prevPan.y = 0;
    }

    public void setZoomStateListener(ZoomStateChangedListener listener) {
        this.listener = listener;
    }
}
