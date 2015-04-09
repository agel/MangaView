package com.agel.arch.mangaview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.data.Settings;
import com.agel.arch.mangaview.data.ZoomStateController;
import com.agel.arch.mangaview.fragments.FsModelFragment;
import com.agel.arch.mangaview.fragments.ImageModelFragment;
import com.agel.arch.mangaview.views.TouchInputListener;
import com.agel.arch.mangaview.views.ViewManga;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MangaViewActivity extends Activity implements Observer {
    private static final String TAG = "MangaViewActivity";
    public static final String ImagePath = "Path";

    //Thread pool
    public static class ImagePoolExecutor extends ThreadPoolExecutor {

        //Members
        public ImagePoolExecutor() {
            super(0, 1, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    //Members
    private final ImagePoolExecutor executor;
    private final Handler uiHandler;
    private final RotateAnimation animationForward;
    private final RotateAnimation animationBackward;
    private final ZoomStateController zoomState;
    private final TouchInputListener zoomListener;

    private ImageModelFragment modelFragment;
    private RelativeLayout loadingLayout;
    private ViewManga mangaView;

    public MangaViewActivity() {
        executor = new ImagePoolExecutor();

        animationForward = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationForward.setInterpolator(new LinearInterpolator());
        animationForward.setRepeatCount(Animation.INFINITE);
        animationForward.setDuration(1000);


        animationBackward = new RotateAnimation(350f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationBackward.setInterpolator(new LinearInterpolator());
        animationBackward.setRepeatCount(Animation.INFINITE);
        animationBackward.setDuration(1000);

        Settings settings = Settings.getInstance();
        final int zoomF = settings.ZoomFactor;
        zoomState = new ZoomStateController(zoomF);
        zoomListener = new TouchInputListener();
        zoomListener.setZoomState(zoomState);

        uiHandler = new Handler();


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

        mangaView = (ViewManga)findViewById(R.id.view_manga);
        mangaView.setScaleType(ImageView.ScaleType.CENTER);
        mangaView.setZoomState(zoomState);
        zoomState.addObserver(this);
        mangaView.setOnTouchListener(zoomListener);

        //Get model
        modelFragment = (ImageModelFragment) getFragmentManager().findFragmentByTag(ImageModelFragment.TAG);
        if(modelFragment == null) {
            modelFragment = new ImageModelFragment();
            modelFragment.init(getIntent().getStringExtra(ImagePath));
            getFragmentManager().beginTransaction().add(modelFragment, FsModelFragment.TAG).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    public void update(Observable observable, Object data) {

    }

}
