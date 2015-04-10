package com.agel.arch.mangaview.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.data.Gestures;
import com.agel.arch.mangaview.data.Settings;
import com.agel.arch.mangaview.models.FsModelFragment;
import com.agel.arch.mangaview.models.ImageModelFragment;
import com.agel.arch.mangaview.views.ViewManga;

public class MangaViewActivity extends Activity implements ImageModelFragment.ImageLoadObserver {
    private static final String TAG = "MangaViewActivity";
    public static final String ImagePath = "Path";

    //Members
    private final RotateAnimation animationForward;
    private final RotateAnimation animationBackward;

    private ImageModelFragment modelFragment;
    private RelativeLayout loadingLayout;
    private ViewManga mangaView;

    private boolean exitPlanned;



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

        mangaView = (ViewManga)findViewById(R.id.view_manga);

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            modelFragment.removeChangeListener(this);
            if(isFinishing()) {
                modelFragment.shutdown();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mangaView.initViewDimensions();

        if(!mangaView.hasImage()) {
            modelFragment.loadCurrent();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Tell mangaView to redraw itself using new dimensions
        mangaView.onConfigurationChanged();
    }

    private boolean processAction(int action) {
        int zoomF;
        Settings settings = Settings.getInstance();
        switch(action)
        {
            case Gestures.ACTION_NEXT:
                return modelFragment.loadNext();
            case Gestures.ACTION_BACK:
                return modelFragment.loadPrevious();
            case Gestures.ACTION_ZOOM_IN:
                zoomF = mangaView.getZoomState().getZoomFactor();
                if(zoomF <= 140)
                    zoomF += 10;
                else
                    zoomF = 150;
                mangaView.getZoomState().setZoomFactor(zoomF);
                settings.ZoomFactor = zoomF;
                settings.saveSettings(PreferenceManager.getDefaultSharedPreferences(this), getResources());
                mangaView.redraw();
                break;
            case Gestures.ACTION_ZOOM_OUT:
                zoomF = mangaView.getZoomState().getZoomFactor();
                if(zoomF >= 60)
                    zoomF -= 10;
                else
                    zoomF = 50;
                mangaView.getZoomState().setZoomFactor(zoomF);
                settings.ZoomFactor = zoomF;
                settings.saveSettings(PreferenceManager.getDefaultSharedPreferences(this), getResources());
                mangaView.redraw();
                break;
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
    public void onRectangleReady(Rect rectangle, Bitmap bitmap) {

    }

    @Override
    public void onLoadingChanged(boolean loading, String currentPath) {

    }
}
