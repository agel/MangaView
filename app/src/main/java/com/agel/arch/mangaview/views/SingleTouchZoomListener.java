package com.agel.arch.mangaview.views;

import android.database.Observable;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.agel.arch.mangaview.data.ZoomState;

import java.io.PipedReader;

public class SingleTouchZoomListener implements View.OnTouchListener {
    public final static int ZOOM_STATE_NONE = 0;
    public final static int ZOOM_STATE_ZOOM = 1;
    public final static int ZOOM_STATE_PAN = 2;

    public interface ZoomStateChangedListener {
        void onZoomStateChanged(Rect screenPosition, Point screenPan);
    }

    //Observable manager
    private class TouchObservable extends Observable<ZoomStateChangedListener> {
        public void onTouchAction(Rect screenPosition, Point currentPan) {
            for (final ZoomStateChangedListener observer : mObservers) {
                observer.onZoomStateChanged(screenPosition, currentPan);
            }
        }
    }

    private TouchObservable observer = new TouchObservable();
    private int currentState = ZOOM_STATE_NONE;
    private Rect currentZoom = new Rect();
    private Point currentPan = new Point();
    private Point zoomCenter = new Point();
    private Point touchStart = new Point();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Point touchCoordinate = new Point((int)event.getX(),(int)event.getY());
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchStart = new Point(touchCoordinate);

                if(currentState == ZOOM_STATE_NONE) {
                    currentState = ZOOM_STATE_ZOOM;
                    zoomCenter = new Point(touchCoordinate);
                } else if(currentState == ZOOM_STATE_ZOOM && currentZoom.contains(touchStart.x, touchStart.y)) {
                    currentState = ZOOM_STATE_PAN;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == ZOOM_STATE_ZOOM) {
                    currentZoom.left = zoomCenter.x - Math.abs(zoomCenter.x - touchCoordinate.x);
                    currentZoom.top = zoomCenter.y - Math.abs(zoomCenter.y - touchCoordinate.y);
                    currentZoom.right = zoomCenter.x + Math.abs(zoomCenter.x - touchCoordinate.x);
                    currentZoom.bottom = zoomCenter.y + Math.abs(zoomCenter.y - touchCoordinate.y);

//                    //Enforcing Zoom limits
//                    if(currentZoom.left < 0)
//                        currentZoom.left = 0;
//
//                    if(currentZoom.top < 0)
//                        currentZoom.top = 0;
//
//                    if(currentZoom.right > scr.right)
//                        currentZoom.right = scr.right;
//
//                    if(currentZoom.bottom > scr.bottom)
//                        currentZoom.bottom = scr.bottom;


                } else if(currentState == ZOOM_STATE_PAN) {
                    currentPan.offset(touchStart.x - touchCoordinate.x, touchStart.y - touchCoordinate.y);
                } else {
                    break;
                }

                observer.onTouchAction(currentZoom, currentPan);

                break;
            case MotionEvent.ACTION_UP :

//                Math.sqrt(Math.pow(clickX - centerX, 2) + Math.pow(clickY - centerY, 2));
                break;
        }

        return true;
    }

    public void addZoomStateListener(ZoomStateChangedListener listener) {
        observer.registerObserver(listener);
    }

    public void removeZoomStateListener(ZoomStateChangedListener  listener) {
        observer.unregisterObserver(listener);
    }
}
