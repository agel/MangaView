package com.agel.arch.mangaview.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.data.Gestures;
import com.agel.arch.mangaview.data.Settings;
import com.agel.arch.mangaview.models.FsModelFragment;
import com.agel.arch.mangaview.models.ImageModelFragment;
import com.agel.arch.mangaview.views.MangaImageView;
import com.agel.arch.mangaview.views.SingleTouchZoomListener;

public class MangaViewActivity extends Activity implements ImageModelFragment.ImageLoadObserver, SingleTouchZoomListener.ZoomStateChangedListener {
    private static final String TAG = "MangaViewActivity";
    public static final String ImagePath = "Path";

    //Members
    private final RotateAnimation animationForward;
    private final RotateAnimation animationBackward;

    private ImageModelFragment modelFragment;
    private RelativeLayout loadingLayout;
    private TextView loadingCurrentFile;
    private MangaImageView mangaView;

    private boolean exitPlanned;
    private UiThrottleTask throttleTask;
    private volatile boolean isLoading;
    private ImageView loadingSpinner;

    private SingleTouchZoomListener touchListener;


    public MangaViewActivity() {
        animationForward = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationForward.setInterpolator(new LinearInterpolator());
        animationForward.setRepeatCount(Animation.INFINITE);
        animationForward.setDuration(1000);


        animationBackward = new RotateAnimation(350f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationBackward.setInterpolator(new LinearInterpolator());
        animationBackward.setRepeatCount(Animation.INFINITE);
        animationBackward.setDuration(1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_manga_view);

        int fullScreenFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        fullScreenFlags ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        fullScreenFlags ^= View.SYSTEM_UI_FLAG_LOW_PROFILE;

        if(android.os.Build.VERSION.SDK_INT >= 19) {
            fullScreenFlags ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        final View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(fullScreenFlags);

        loadingLayout = (RelativeLayout)findViewById(R.id.layout_loading);
        loadingCurrentFile = (TextView) findViewById(R.id.viewer_txtPosition);
        loadingSpinner = (ImageView)findViewById(R.id.viewer_spinner);

        mangaView = (MangaImageView)findViewById(R.id.view_manga);

        //Get model
        modelFragment = (ImageModelFragment) getFragmentManager().findFragmentByTag(ImageModelFragment.TAG);
        if(modelFragment == null) {
            modelFragment = new ImageModelFragment();
            if(!modelFragment.init(getIntent().getStringExtra(ImagePath)))
            {
                Settings.makeToast(this, this.getString(R.string.msg_file_not_exist));
                finish();
                return;
            }
            getFragmentManager().beginTransaction().add(modelFragment, FsModelFragment.TAG).commit();
        }
        modelFragment.addChangeListener(this);

        touchListener = new SingleTouchZoomListener();
        mangaView.setOnTouchListener(touchListener);
        touchListener.addZoomStateListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            //TODO investigate lifecycle fuckup
            if(touchListener != null) {
                modelFragment.removeChangeListener(this);
                touchListener.removeZoomStateListener(this);
            }
            if(isFinishing()) {
                modelFragment.shutdown();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!mangaView.hasImage()) {
            modelFragment.loadCurrent(mangaView.getViewDimensions());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(Settings.getInstance().mKeySetting.get(keyCode) > 0)
        {
            exitPlanned = this.processAction(Settings.getInstance().mKeySetting.get(keyCode));
            return true;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (Settings.getInstance().mKeySetting.get(keyCode) > 0)
        {
            if (exitPlanned)
            {
                finish();
            }
            return true;
        }
        else
            return super.onKeyUp(keyCode, event);
    }

    private boolean processAction(int action) {
        switch(action)
        {
            case Gestures.ACTION_NEXT:
                if(loadingSpinner.getAnimation() != animationForward) {
                    loadingSpinner.setAnimation(animationForward);
                }
                return modelFragment.loadNext(mangaView.getViewDimensions());
            case Gestures.ACTION_BACK:
                if(loadingSpinner.getAnimation() != animationBackward) {
                    loadingSpinner.setAnimation(animationBackward);
                }
                return modelFragment.loadPrevious(mangaView.getViewDimensions());
            default:
                break;
        }
        return false;
    }

    @Override
    public void onImageReady(Bitmap bitmap) {
        mangaView.setImage(bitmap);
    }

    @Override
    public void onZoomedImageReady(Rect rectangle, Bitmap bitmap) {
        mangaView.setZoomImage(rectangle, bitmap);
    }

    @Override
    public void onZoomStateChanged(Rect screenPosition, Point screenPan) {
        if(!screenPosition.isEmpty()) {
            modelFragment.loadZoomed(mangaView.getViewDimensions(), screenPosition, screenPan);
        } else {
            mangaView.setZoomImage(screenPosition, null);
        }
    }

    @Override
    public void onLoadingChanged(boolean loading, final String currentPath) {
        isLoading = loading;
        if (loading) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isLoading) {
                        if (loadingLayout.getVisibility() != View.VISIBLE) {
                            loadingLayout.setVisibility(View.VISIBLE);
                        }
                        loadingCurrentFile.setText(currentPath);
                    }
                }
            });
        } else {
            synchronized (MangaViewActivity.this) {
                if (throttleTask == null) {
                    throttleTask = new UiThrottleTask();
                    throttleTask.execute();
                }
            }
        }
    }

    private class UiThrottleTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Hack to avoid loading flickering on fast phones
                int repeat = 0;
                while (isLoading || repeat < 3) {
                    if(isLoading) {
                        repeat = 0;
                        Thread.sleep(75);
                    } else {
                        repeat++;
                        Thread.sleep(25);
                    }
                }
            } catch (InterruptedException e) { return null; }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!isLoading) {
                Log.d(TAG, "Finished");
                loadingLayout.setVisibility(View.INVISIBLE);
                loadingCurrentFile.setText("");
            } else {
                Log.d(TAG, "Throttle");
            }

            synchronized (MangaViewActivity.this) {
                throttleTask = null;
            }
        }
    }
}
