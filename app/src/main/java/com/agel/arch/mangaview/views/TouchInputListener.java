package com.agel.arch.mangaview.views;

import android.database.Observable;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.agel.arch.mangaview.data.ZoomState;

public class TouchInputListener implements OnTouchListener{
    //Event Observer
    public interface TouchObserver {
        void onTouchAction();
    }
    //Observable manager
    private class TouchObservable extends Observable<TouchObserver> {
        public void onTouchAction() {
            for (final TouchObserver observer : mObservers) {
                observer.onTouchAction();
            }
        }
    }

    private ZoomState mState;
    private final TouchObservable observers = new TouchObservable();
    private final int trembleThreshold = 4;
    private int mStartX;
    private int mStartY;


    public TouchInputListener() {
        super();
    }

    public void setZoomState(ZoomState state) {
        mState = state;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getAction();
        Point touchCoord = new Point((int)event.getX(),(int)event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = touchCoord.x;
                mStartY = touchCoord.y;

                if(mState.getZoomState() == ZoomState.ZOOM_STATE_NONE)
                    mState.setZoomState(ZoomState.ZOOM_STATE_ZOOM);
                if(mState.getZoomState() == ZoomState.ZOOM_STATE_ZOOM)
                    mState.setCenter(touchCoord);
                if(mState.getZoomState() == ZoomState.ZOOM_STATE_PAN)
                {
                    if(!mState.getRectDst().contains(touchCoord.x, touchCoord.y))
                    {
                        mState.setZoomState(ZoomState.ZOOM_STATE_ZOOM);
                        mState.setCenter(touchCoord);
                    }
                    else
                        mState.setPanStart(touchCoord.x, touchCoord.y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mState.getZoomState() == ZoomState.ZOOM_STATE_ZOOM) {
                    mState.setSize(touchCoord.x,touchCoord.y);
                    observers.onTouchAction();
                }
                else
                if(mState.getZoomState() == ZoomState.ZOOM_STATE_PAN )
                {
                    mState.setPan(touchCoord.x,touchCoord.y);
                    observers.onTouchAction();
                }

                break;
            case MotionEvent.ACTION_UP :

                if(mState.getZoomState() == ZoomState.ZOOM_STATE_ZOOM)
                {
                    mState.setZoomState(ZoomState.ZOOM_STATE_PAN);
                }
                break;
        }

        return true;
    }

    public void addChangeListener(final TouchObserver observer) {
        observers.registerObserver(observer);
    }

    public void removeChangeListener(final TouchObserver observer) {
        observers.unregisterObserver(observer);
    }
}
