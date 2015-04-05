package com.agel.arch.mangaview.fragments;

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
import com.agel.arch.mangaview.data.FileScanner;

import java.util.Date;

;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment implements FileScanner.OnScanProgressListener {

    private FileSystemAdapter listAdapter;
    private MainActivity mainActivity;
    private long lastUIUpdate = 0;
    private FileScanner.IRemoveCallback removeCallback;

    @Override
    public void onDetach() {
        removeCallback.remove();
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
        mainActivity.onSectionAttached(MainActivity.FilesystemSection);

        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);

        removeCallback = FileScanner.getInstance().addOnScanProgressListener(this);

        synchronized (mainActivity.CurrentFsEntry.Children) {
            listAdapter.addAll(mainActivity.CurrentFsEntry.Children);
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
        if(mainActivity.CurrentFsEntry == mainActivity.RootEntry) {
            return true;
        }

        setCurrentEntry(mainActivity.CurrentFsEntry.Parent);

        return false;
    }

    private void setCurrentEntry(FileEntry entry) {
        mainActivity.CurrentFsEntry = entry;
        listAdapter.clear();
        synchronized (mainActivity.CurrentFsEntry.Children) {
            listAdapter.addAll(mainActivity.CurrentFsEntry.Children);
        }
    }

    @Override
    public void onScanProgress(boolean finished, FileEntry entry) {
        long time = new Date().getTime();
        if(mainActivity.CurrentFsEntry == entry &&  time - lastUIUpdate > 10000) {
            lastUIUpdate = time;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.clear();
                    synchronized (mainActivity.CurrentFsEntry.Children) {
                        listAdapter.addAll(mainActivity.CurrentFsEntry.Children);
                    }
                }
            });
        }
    }
}
