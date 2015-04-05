package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.activities.MainActivity;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;
import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.data.FileScanner;

import java.util.Date;

;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment implements FileScanner.OnScanProgressListener {

    private FileSystemAdapter listAdapter;
    private FileEntry rootEntry = FileScanner.getInstance().getRoot();
    private FileEntry currentEntry = rootEntry;
    private MainActivity mainActivity;
    private long lastUIUpdate = 0;
    private FileScanner.IRemoveCallback removeCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);

        synchronized (currentEntry.Children) {
            listAdapter.addAll(currentEntry.Children);
        }

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
        FileEntry item = listAdapter.getItem(position);
        if(item.IsDirectory) {
            setCurrentEntry(item);
        } else {
            //TODO open view activity
        }
    }

    public boolean onBackPressed() {
        if(currentEntry == rootEntry) {
            return true;
        }

        setCurrentEntry(currentEntry.Parent);

        return false;
    }

    private void setCurrentEntry(FileEntry entry) {
        currentEntry = entry;
        listAdapter.clear();
        synchronized (currentEntry.Children) {
            listAdapter.addAll(currentEntry.Children);
        }
    }

    @Override
    public void onScanProgress(boolean finished, FileEntry entry) {
        long time = new Date().getTime();
        if(currentEntry == entry &&  time - lastUIUpdate > 10000) {
            lastUIUpdate = time;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.clear();
                    synchronized (currentEntry.Children) {
                        listAdapter.addAll(currentEntry.Children);
                    }
                }
            });
        }
    }
}
