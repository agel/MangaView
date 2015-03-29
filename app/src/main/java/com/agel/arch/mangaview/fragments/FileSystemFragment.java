package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.agel.arch.mangaview.BackgroundWorker;
import com.agel.arch.mangaview.MainActivity;
import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;

;import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment {

    private FileSystemAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new FileSystemAdapter(getActivity(), R.layout.fs_item);
        setListAdapter(listAdapter);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.filesystem_layout, container, false);
//
//
//        return rootView;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(MainActivity.FilesystemSection);
        final String external_storage = System.getenv("EXTERNAL_STORAGE");
        if(external_storage != null) {
            BackgroundWorker.getInstance().put(new Runnable() {
                @Override
                public void run() {
                    scanDir(external_storage);
                }
            });
        }
        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            BackgroundWorker.getInstance().put(new Runnable() {
                @Override
                public void run() {
                    scanDir(secondary_storage);
                }
            });
        }
    }

    private void scanDir(String path) {
        File dir = new File(path);
        if(dir.isDirectory()) {

        } else {

        }
    }
}
