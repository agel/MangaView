package com.agel.arch.mangaview.views;

import android.database.Observable;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.agel.arch.mangaview.data.ZoomState;

@Deprecated
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
        Point touchCoordinate = new Point((int)event.getX(),(int)event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = touchCoordinate.x;
                mStartY = touchCoordinate.y;

                //TODO switch to pan if touched inside zoom rectangle
//                if(mState.getZoomState() == ZoomState.ZOOM_STATE_ZOOM)
//                {
//                    mState.setZoomState(ZoomState.ZOOM_STATE_PAN);
//                }

                if(mState.getZoomState() == ZoomState.ZOOM_STATE_NONE)
                    mState.setZoomState(ZoomState.ZOOM_STATE_ZOOM);
                if(mState.getZoomState() == ZoomState.ZOOM_STATE_ZOOM)
                    mState.setCenter(touchCoordinate);
                if(mState.getZoomState() == ZoomState.ZOOM_STATE_PAN)
                {
                    if(!mState.getRectDst().contains(touchCoordinate.x, touchCoordinate.y))
                    {
                        mState.setZoomState(ZoomState.ZOOM_STATE_ZOOM);
                        mState.setCenter(touchCoordinate);
                    }
                    else
                        mState.setPanStart(touchCoordinate.x, touchCoordinate.y);
                }


                break;
            case MotionEvent.ACTION_MOVE:
                if (mState.getZoomState() == ZoomState.ZOOM_STATE_ZOOM) {
                    mState.setSize(touchCoordinate.x,touchCoordinate.y);
                    observers.onTouchAction();
                }
                else if(mState.getZoomState() == ZoomState.ZOOM_STATE_PAN)
                {
                    mState.setPan(touchCoordinate.x,touchCoordinate.y);
                    observers.onTouchAction();
                }
                break;
            case MotionEvent.ACTION_UP :


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
