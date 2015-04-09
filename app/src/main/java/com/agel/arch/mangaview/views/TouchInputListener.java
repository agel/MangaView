package com.agel.arch.mangaview.views;

/**
 * Created by agel on 09/04/2015.
 */
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.agel.arch.mangaview.data.ZoomControlType;
import com.agel.arch.mangaview.data.ZoomStateController;

public class TouchInputListener implements OnTouchListener {

    private ZoomStateController mState;
    private final int trembleThreshold = 4;
    private int mStartX;
    private int mStartY;


    public TouchInputListener() {
        super();
    }

    public void setZoomState(ZoomStateController state) {
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
                    mState.notifyObservers();
                }
                else
                if(mState.getZoomState() == ZoomControlType.PAN )
                {
                    mState.setPan(touchCoord.x,touchCoord.y);
                    mState.notifyObservers();
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

}
