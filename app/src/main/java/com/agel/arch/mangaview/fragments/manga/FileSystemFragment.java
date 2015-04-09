package com.agel.arch.mangaview.fragments.manga;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.activities.MainActivity;
import com.agel.arch.mangaview.activities.MangaViewActivity;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;
import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.fragments.FsModelFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment implements FsModelFragment.OnScanProgressObserver {

    public static final int UI_UPDATE_INTERVAL = 10000;

    private FileSystemAdapter listAdapter;
    private MainActivity mainActivity;
    private long lastUIUpdate = 0;

    @Override
    public void onDetach() {
        mainActivity.getModelFragment().removeChangeListener(this);
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
        mainActivity.onSectionAttached(MainActivity.FilesystemSection);

        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);

        mainActivity.getModelFragment().addChangeListener(this);

        final List<FileEntry> children = mainActivity.getCurrentFsEntry().Children;
        synchronized (children) {
            listAdapter.addAll(children);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileEntry item = listAdapter.getItem(position);
        if(item.IsDirectory) {
            setCurrentEntry(item);
        } else {
            Intent intent = new Intent(mainActivity, MangaViewActivity.class);
            intent.putExtra(MangaViewActivity.ImagePath, item.Path);
            startActivity(intent);
        }
    }

    public boolean onBackPressed() {
        if(mainActivity.getCurrentFsEntry() == mainActivity.getRootEntry()) {
            return true;
        }

        setCurrentEntry(mainActivity.getCurrentFsEntry().Parent);

        return false;
    }

    private void setCurrentEntry(FileEntry entry) {
        mainActivity.setCurrentFsEntry(entry);
        listAdapter.clear();
        final List<FileEntry> entries = mainActivity.getCurrentFsEntry().Children;
        synchronized (entries) {
            listAdapter.addAll(entries);
        }
    }

    @Override
    public void onScanProgressChanged(boolean finished, FileEntry entry) {
        long time = new Date().getTime();
        if(mainActivity.getCurrentFsEntry() == entry &&  time - lastUIUpdate > UI_UPDATE_INTERVAL) {
            lastUIUpdate = time;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.clear();
                    final List<FileEntry> entries = mainActivity.getCurrentFsEntry().Children;
                    synchronized (entries) {
                        listAdapter.addAll(entries);
                    }
                }
            });
        }
    }
}
