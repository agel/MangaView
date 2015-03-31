package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.BackgroundWorker;
import com.agel.arch.mangaview.activities.MainActivity;
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
        }

        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            final FileEntry secStorage = new FileEntry(new File(secondary_storage), rootEntry);
            rootEntry.Children.add(secStorage);
        }
        else
        {
            currentEntry = rootEntry.Children.get(0);
        }

        BackgroundWorker.getInstance().put(new Runnable() {
            @Override
            public void run() {
                scanDir(rootEntry);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        setCurrentEntry(listAdapter.getItem(position));
    }

    private void scanDir(FileEntry file) {
        //TODO manually simulate call stack to correctly display loading indicator
        //TODO update UI only every few seconds.
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
        listAdapter.addAll(currentEntry.Children);
    }
}
