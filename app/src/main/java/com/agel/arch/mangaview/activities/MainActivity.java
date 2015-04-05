package com.agel.arch.mangaview.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.agel.arch.mangaview.NavigationDrawerFragment;
import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.data.FileScanner;
import com.agel.arch.mangaview.data.Settings;
import com.agel.arch.mangaview.fragments.BookmarksFragment;
import com.agel.arch.mangaview.fragments.FileSystemFragment;
import com.agel.arch.mangaview.fragments.HistoryFragment;
import com.agel.arch.mangaview.fragments.SettingsFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FileScanner.OnScanProgressListener {

    public static final int FilesystemSection = 1;
    public static final int BookmarksSection = 2;
    public static final int HistorySection = 3;
    public static final int SettingsSection = 4;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Fragment currentFragment;
    private ProgressBar progressBar;
    private FileScanner.IRemoveCallback removeCallback;
    //FS state
    public FileEntry RootEntry = FileScanner.getInstance().getRoot();
    public FileEntry CurrentFsEntry = RootEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Progress bar
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);

        setContentView(R.layout.main_layout);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //Load app settings
        Settings.getInstance().loadSettings(PreferenceManager.getDefaultSharedPreferences(this), getResources());

        //Launch scan
        setLoading(true);
        FileScanner scanner = FileScanner.getInstance();
        removeCallback = scanner.addOnScanProgressListener(this);
        scanner.scan();
    }

    @Override
    protected void onDestroy() {
        removeCallback.remove();
        super.onDestroy();
    }

    @Override
    public void onScanProgress(boolean finished, FileEntry lastProcessed) {
        if(finished) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoading(false);   }
            });
        }
    }

    public void setLoading(final boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        currentFragment = null;
        switch (position + 1) {
            case FilesystemSection:
                currentFragment = new FileSystemFragment();
                break;
            case BookmarksSection:
                currentFragment = new BookmarksFragment();
                break;
            case HistorySection:
                currentFragment = new HistoryFragment();
                break;
            case SettingsSection:
                currentFragment = new SettingsFragment();
                break;
            default:
                return;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFragment).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case FilesystemSection:
                mTitle = getString(R.string.title_filesystem);
                break;
            case BookmarksSection:
                mTitle = getString(R.string.title_bookmarks);
                break;
            case HistorySection:
                mTitle = getString(R.string.title_history);
                break;
            case SettingsSection:
                mTitle = getString(R.string.action_settings);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(progressBar);

//        Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) progressBar.getLayoutParams();
//        layoutParams.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == null || !(currentFragment instanceof FileSystemFragment) || ((FileSystemFragment)currentFragment).onBackPressed() )
            super.onBackPressed();
    }

    //    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
