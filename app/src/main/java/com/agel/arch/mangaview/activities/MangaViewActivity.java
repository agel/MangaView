package com.agel.arch.mangaview.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.activities.util.SystemUiHider;
import com.agel.arch.mangaview.data.Settings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MangaViewActivity extends Activity {

    public static final String ImagePath = "Path";
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_manga_view);

        if( android.os.Build.VERSION.SDK_INT >= 14) {
            if (Settings.getInstance().FullscreenViewer) {

                final View decorView = getWindow().getDecorView();

                int newUiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

                if (Build.VERSION.SDK_INT >= 16) {
                    newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                    newUiOptions ^= View.STATUS_BAR_HIDDEN;
                    if(android.os.Build.VERSION.SDK_INT >= 18) {
                        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    }
                } else {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                decorView.setSystemUiVisibility(newUiOptions);
            }
            else
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        final View contentView = findViewById(R.id.view_manga);

        if(Settings.isTablet(this)) {
            // Set up an instance of SystemUiHider to control the system UI for this activity.
            mSystemUiHider = SystemUiHider.getInstance(this, contentView, SystemUiHider.FLAG_HIDE_NAVIGATION);
            mSystemUiHider.setup();
            mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                // Cached values.
                @Override
                @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                public void onVisibilityChange(boolean visible) {
                    if (visible) {
                        // Schedule a hide().
                        delayedHide(500);
                    }
                }
            });
        }
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
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
