package com.agel.arch.mangaview.data;

import android.util.Log;

import com.agel.arch.mangaview.BackgroundWorker;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by Agel on 04.04.2015.
 */
public class FileScanner {
    //Statics
    private static FileScanner instance;
    public static FileScanner getInstance() {
        if(instance == null) {
            instance = new FileScanner();
            instance.init();
        }
        return instance;
    }

    //Event
    public interface OnScanProgressListener {
        void onScanProgress(boolean finished, FileEntry lastProcessed);
    }
    public interface IRemoveCallback {
        void remove();
    }

    //Members
    private ArrayList<OnScanProgressListener> eventListeners = new ArrayList<>();
    private boolean finished = false;
    private FileEntry rootEntry;

    //Getters/Setters
    public boolean isScanning() {
        return finished;
    }
    public IRemoveCallback addOnScanProgressListener(final OnScanProgressListener listener) {
        eventListeners.add(listener);
        return new IRemoveCallback() {
            @Override
            public void remove() {
                eventListeners.remove(listener);
            }
        };
    }
    public FileEntry getRoot() {
        return rootEntry;
    }

    //Methods
    private void init() {
        final String external_storage = System.getenv("EXTERNAL_STORAGE");
        rootEntry = new FileEntry();

        if(external_storage != null) {
            new FileEntry(new File(external_storage), rootEntry);
        }

        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            new FileEntry(new File(secondary_storage), rootEntry);
        }
    }

    public void scan() {
        finished = false;
        synchronized (rootEntry.Children) {
            for (FileEntry entry : rootEntry.Children) {
                entry.clear();
            }
        }
        queueScan(rootEntry, new ScanStack());
    }

    private void queueScan(final FileEntry entry, final ScanStack stack) {
        BackgroundWorker.getInstance().put(new Runnable() {
            @Override
            public void run() {
                scanDir(entry, stack);
            }
        });
    }

    private void scanDir(FileEntry entry, ScanStack stack) {
        //Scan directories
        if(entry.Path == null)  {
            synchronized (entry.Children) {
                for (FileEntry fileEntry : entry.Children) {
                    queueScan(fileEntry, stack.push(fileEntry.Path));
                }
            }
        } else {
            File[] contents = new File(entry.Path).listFiles(new FileFilter() {
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
                        queueScan(fileEntry, stack.push(fileEntry.Path));
                    }
                }
                entry.sort();
            } else {
                Log.w("Manga", "WTF? - " + entry.Path);
            }
        }

        finished = stack.pop(entry.Path);
        for(OnScanProgressListener listener : eventListeners) {
            listener.onScanProgress(finished, entry);
        }
    }
}
