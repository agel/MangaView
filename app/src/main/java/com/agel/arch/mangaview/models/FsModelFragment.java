package com.agel.arch.mangaview.models;

import android.database.Observable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;

import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.data.MangaFileFilter;
import com.agel.arch.mangaview.data.ScanStack;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by agel on 08.04.15.
 */
public class FsModelFragment extends Fragment {
    public static final String TAG = "FsModelFragment";

    //Event Observer
    public interface OnScanProgressObserver {
        void onScanProgressChanged(boolean finished, FileEntry lastProcessed);
    }
    //Observable manager
    private class ScanProgressObservable extends Observable<OnScanProgressObserver> {
        public void notifyProgress(boolean finished, FileEntry lastProcessed) {
            for (final OnScanProgressObserver observer : mObservers) {
                observer.onScanProgressChanged(finished, lastProcessed);
            }
        }
    }
    //Thread pool
    public static class FsThreadPoolExecutor extends ThreadPoolExecutor {

        private static final int ThreadCount = Runtime.getRuntime().availableProcessors();

        //Members
        public FsThreadPoolExecutor() {
            super(0, ThreadCount, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }

    //Members
    private final FsThreadPoolExecutor executor = new FsThreadPoolExecutor();
    private ScanProgressObservable eventListeners = new ScanProgressObservable();
    private final FileEntry rootEntry;
    private boolean scanFinished = false;

    public FsModelFragment() {
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

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void shutdown() {
        executor.shutdownNow();

//        try {
//            executor.awaitTermination(1, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            Log.d(TAG, "Shutdown awaitTermination timeout");
//        }
    }

    public FileEntry getRootEntry() {
        return rootEntry;
    }

    public boolean isScanFinished() {
        return scanFinished;
    }

    public void scan() {
        scanFinished = false;
        synchronized (rootEntry.Children) {
            for (FileEntry entry : rootEntry.Children) {
                entry.clear();
            }
        }
        queueScan(rootEntry, new ScanStack());
    }

    private void queueScan(final FileEntry entry, final ScanStack stack) {
        executor.execute(new Runnable() {
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
            File[] contents = new File(entry.Path).listFiles(new MangaFileFilter(true));

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

        scanFinished = stack.pop(entry.Path);
        eventListeners.notifyProgress(scanFinished, entry);
    }

    public void addChangeListener(final OnScanProgressObserver observer) {
        eventListeners.registerObserver(observer);
    }

    public void removeChangeListener(final OnScanProgressObserver observer) {
        eventListeners.unregisterObserver(observer);
    }
}
