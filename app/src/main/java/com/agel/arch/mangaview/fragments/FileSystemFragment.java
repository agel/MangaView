package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.BackgroundWorker;
import com.agel.arch.mangaview.MainActivity;
import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;
import com.agel.arch.mangaview.data.FileEntry;

;import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment {

    private FileSystemAdapter listAdapter;
    private FileEntry rootEntry = new FileEntry();
    private FileEntry currentEntry = rootEntry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);

        listAdapter.addAll(currentEntry.Children);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(MainActivity.FilesystemSection);

        final String external_storage = System.getenv("EXTERNAL_STORAGE");
        if(external_storage != null) {
            final FileEntry extStorage = new FileEntry(new File(external_storage), rootEntry);
            rootEntry.Children.add(extStorage);
            BackgroundWorker.getInstance().put(new Runnable() {
                @Override
                public void run() {
                    scanDir(extStorage);
                }
            });
        }
        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            final FileEntry secStorage = new FileEntry(new File(secondary_storage), rootEntry);
            rootEntry.Children.add(secStorage);
            BackgroundWorker.getInstance().put(new Runnable() {
                @Override
                public void run() {
                    scanDir(secStorage);
                }
            });
        }
        else
        {
            currentEntry = rootEntry.Children.get(0);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    private void scanDir(FileEntry file) {

    }
}
