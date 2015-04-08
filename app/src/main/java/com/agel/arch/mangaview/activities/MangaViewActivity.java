package com.agel.arch.mangaview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.data.Settings;

public class MangaViewActivity extends Activity {
    private static final String TAG = "MangaViewActivity";

    public static final String ImagePath = "Path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_manga_view);

        int fullScreenFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        fullScreenFlags ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        fullScreenFlags ^= View.SYSTEM_UI_FLAG_LOW_PROFILE;

        if(android.os.Build.VERSION.SDK_INT >= 18) {
            fullScreenFlags ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        final View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(fullScreenFlags);
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //TODO set low profile on tablets
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
