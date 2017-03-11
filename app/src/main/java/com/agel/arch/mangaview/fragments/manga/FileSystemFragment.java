package com.agel.arch.mangaview.fragments.manga;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.activities.MainActivity;
import com.agel.arch.mangaview.activities.MangaViewActivity;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;
import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.models.FsModelFragment;

import java.util.Date;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment implements FsModelFragment.OnScanProgressObserver {
    private FileSystemAdapter listAdapter;
    private MainActivity mainActivity;
    private FsModelFragment fsModelFragment;

    @Override
    public void onDetach() {
        try {
            fsModelFragment.removeChangeListener(this);
        } catch (Exception e) {
            Log.d("Manga", "Detach " + e.getMessage());
        }
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) this.getActivity();
        mainActivity.onSectionAttached(MainActivity.FilesystemSection);

        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);

        fsModelFragment = mainActivity.getFsModelFragment();
        fsModelFragment.addChangeListener(this);
        fsModelFragment.scan(fsModelFragment.getCurrentFsEntry());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileEntry item = listAdapter.getItem(position);
        if(item.IsDirectory) {
            fsModelFragment.setCurrentFsEntry(item);
            fsModelFragment.scan(item);
        } else {
            Intent intent = new Intent(mainActivity, MangaViewActivity.class);
            intent.putExtra(MangaViewActivity.ImagePath, item.Path);
            startActivity(intent);
        }
    }

    public boolean onBackPressed() {
        if(mainActivity.getFsModelFragment().getCurrentFsEntry() == mainActivity.getFsModelFragment().getRootEntry()) {
            return true;
        }
        FileEntry parent = fsModelFragment.getCurrentFsEntry().Parent;
        fsModelFragment.setCurrentFsEntry(parent);
        fsModelFragment.scan(parent);
        return false;
    }

    @Override
    public void onScanProgressChanged(final FileEntry entry) {
        listAdapter.clear();
        listAdapter.addAll(entry.Children);
    }
}
