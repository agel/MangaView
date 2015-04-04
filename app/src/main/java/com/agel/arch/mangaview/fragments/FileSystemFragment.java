package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.activities.MainActivity;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;
import com.agel.arch.mangaview.data.FileEntryThin;
import com.agel.arch.mangaview.data.FileScanner;

import java.util.Date;

;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment implements FileScanner.OnScanProgressListener {

    private FileSystemAdapter listAdapter;
    private FileEntryThin rootEntry = FileScanner.getInstance().getRoot();
    private FileEntryThin currentEntry = rootEntry;
    private MainActivity mainActivity;
    private long lastUIUpdate = 0;
    private FileScanner.IRemoveCallback removeCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);

        listAdapter.addAll(currentEntry.Children);

        removeCallback = FileScanner.getInstance().addOnScanProgressListener(this);
    }

    @Override
    public void onDestroy() {
        removeCallback.remove();
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
        mainActivity.onSectionAttached(MainActivity.FilesystemSection);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        setCurrentEntry(listAdapter.getItem(position));
    }

    public boolean onBackPressed() {
        if(currentEntry == rootEntry) {
            return true;
        }

        setCurrentEntry(currentEntry.Parent);

        return false;
    }

    private void setCurrentEntry(FileEntryThin entry) {
        currentEntry = entry;
        listAdapter.clear();
        listAdapter.addAll(currentEntry.Children);
    }

    @Override
    public void onScanProgress(boolean finished, FileEntryThin entry) {
        long time = new Date().getTime();
        if(currentEntry == entry &&  time - lastUIUpdate > 10000) {
            lastUIUpdate = time;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.clear();
                    listAdapter.addAll(currentEntry.Children);
                }
            });
        }
    }
}
