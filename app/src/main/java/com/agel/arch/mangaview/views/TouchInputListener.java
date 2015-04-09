package com.agel.arch.mangaview.views;

/**
 * Created by agel on 09/04/2015.
 */
import android.database.Observable;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.agel.arch.mangaview.data.ZoomControlType;
import com.agel.arch.mangaview.data.ZoomState;

public class TouchInputListener {
    //Event Observer
    public interface TouchObserver {
        void onTouchAction(ZoomState state);
    }
    //Observable manager
    private class TouchObservable extends Observable<TouchObserver> {
        public void notifyProgress(ZoomState state) {
            for (final TouchObserver observer : mObservers) {
                observer.onTouchAction(state);
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

    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getAction();
        Point touchCoord = new Point((int)event.getX(),(int)event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = touchCoord.x;
                mStartY = touchCoord.y;

                if(mState.getZoomState() == ZoomControlType.NONE)
                    mState.setZoomState(ZoomControlType.ZOOM);
                if(mState.getZoomState() == ZoomControlType.ZOOM)
                    mState.setCenter(touchCoord);
                if(mState.getZoomState() == ZoomControlType.PAN)
                {
                    if(!mState.getRectDst().contains(touchCoord.x, touchCoord.y))
                    {
                        mState.setZoomState(ZoomControlType.ZOOM);
                        mState.setCenter(touchCoord);
                    }
                    else
                        mState.setPanStart(touchCoord.x, touchCoord.y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mState.getZoomState() == ZoomControlType.ZOOM) {
                    mState.setSize(touchCoord.x,touchCoord.y);
                    observers.notifyProgress(mState);
                }
                else
                if(mState.getZoomState() == ZoomControlType.PAN )
                {
                    mState.setPan(touchCoord.x,touchCoord.y);
                    observers.notifyProgress(mState);
                }

                break;
            case MotionEvent.ACTION_UP :

                if(mState.getZoomState() == ZoomControlType.ZOOM)
                {
                    mState.setZoomState(ZoomControlType.PAN);
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
