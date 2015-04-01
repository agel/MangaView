package com.agel.arch.mangaview.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.agel.arch.mangaview.BackgroundWorker;
import com.agel.arch.mangaview.activities.MainActivity;
import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.adapters.FileSystemAdapter;
import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.data.ScanStack;

;import java.io.File;
import java.io.FileFilter;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSystemFragment extends ListFragment {

    private FileSystemAdapter listAdapter;
    private FileEntry rootEntry = new FileEntry();
    private FileEntry currentEntry = rootEntry;
    private MainActivity mainActivity;
    private long lastUIUpdate = 0;

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
        mainActivity = (MainActivity) activity;
        mainActivity.onSectionAttached(MainActivity.FilesystemSection);


        final String external_storage = System.getenv("EXTERNAL_STORAGE");
        if(external_storage != null) {
            new FileEntry(new File(external_storage), rootEntry);
        }

        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            new FileEntry(new File(secondary_storage), rootEntry);
        }
        else
        {
            currentEntry = rootEntry.Children.get(0);
        }

        ScanStack stack = new ScanStack();

        queueScan(rootEntry, stack);
        mainActivity.setLoading(true);
    }

    private void queueScan(final FileEntry entry, final ScanStack stack) {
        BackgroundWorker.getInstance().put(new Runnable() {
            @Override
            public void run() {
                scanDir(entry, stack);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        setCurrentEntry(listAdapter.getItem(position));
    }

    private void scanDir(FileEntry entry, ScanStack stack) {
        //Scan directories
        if(entry.File == null)  {
            for (FileEntry fileEntry : entry.Children) {
                queueScan(fileEntry, stack.push(fileEntry));
            }
        } else {
            File[] contents = entry.File.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String filename = pathname.getName().toLowerCase();

                    return !filename.startsWith(".") && (pathname.isDirectory() ||
                            filename.endsWith(".png") ||
                            filename.endsWith(".jpg") ||
                            filename.endsWith(".jpeg") ||
                            filename.endsWith(".gif"));
                }
            });

            if(contents != null) {

                for (File file : contents) {
                    FileEntry fileEntry = new FileEntry(file, entry);
                    if (file.isDirectory()) {
                        queueScan(fileEntry, stack);
                    }
                }
            } else {
                Log.w("Manga", "WTF? - " + entry.path());
            }
        }
        if(stack.pop(entry)) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setLoading(false);
                }
            });

        } else {
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
