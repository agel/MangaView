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
        void onScanProgress(boolean finished, FileEntryThin lastProcessed);
    }
    public interface IRemoveCallback {
        void remove();
    }

    //Members
    private ArrayList<OnScanProgressListener> eventListeners = new ArrayList<>();
    private boolean scanInProgress = false;
    private FileEntryThin rootEntry;

    //Getters/Setters
    public boolean isScanning() {
        return scanInProgress;
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
    public FileEntryThin getRoot() {
        return rootEntry;
    }

    //Methods
    private void init() {
        final String external_storage = System.getenv("EXTERNAL_STORAGE");
        rootEntry = new FileEntryThin();

        if(external_storage != null) {
            new FileEntryThin(new File(external_storage), rootEntry);
        }

        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            new FileEntryThin(new File(secondary_storage), rootEntry);
        }

        ScanStack stack = new ScanStack();

        scanInProgress = true;
        queueScan(rootEntry, stack);
    }

    private void queueScan(final FileEntryThin entry, final ScanStack stack) {
        BackgroundWorker.getInstance().put(new Runnable() {
            @Override
            public void run() {
                scanDir(entry, stack);
            }
        });
    }

    private void scanDir(FileEntryThin entry, ScanStack stack) {
        //Scan directories
        if(entry.Path == null)  {
            for (FileEntryThin fileEntry : entry.Children) {
                queueScan(fileEntry, stack.push(fileEntry.Path));
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
                    FileEntryThin fileEntry = new FileEntryThin(file, entry);
                    if (file.isDirectory()) {
                        queueScan(fileEntry, stack);
                    }
                }
            } else {
                Log.w("Manga", "WTF? - " + entry.Path);
            }
        }

        scanInProgress = stack.pop(entry.Path);
        for(OnScanProgressListener listener : eventListeners) {
            listener.onScanProgress(scanInProgress, entry);
        }
    }
}
