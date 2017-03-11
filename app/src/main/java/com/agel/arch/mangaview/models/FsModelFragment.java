package com.agel.arch.mangaview.models;

import android.app.Fragment;
import android.database.Observable;
import android.os.Bundle;
import android.util.Log;

import com.agel.arch.mangaview.data.FileEntry;
import com.agel.arch.mangaview.data.MangaFileFilter;

import java.io.File;
import java.io.FileFilter;

public class FsModelFragment extends Fragment {
    public static final String TAG = "FsModelFragment";

    //Event Observer
    public interface OnScanProgressObserver {
        void onScanProgressChanged(FileEntry lastProcessed);
    }
    //Observable manager
    private class ScanProgressObservable extends Observable<OnScanProgressObserver> {
        void notifyProgress(FileEntry lastProcessed) {
            for (final OnScanProgressObserver observer : mObservers) {
                observer.onScanProgressChanged(lastProcessed);
            }
        }
    }

    //Members
    private ScanProgressObservable eventListeners = new ScanProgressObservable();
    private FileEntry rootEntry;
    private FileEntry currentFsEntry;

    public FsModelFragment() {
        final String external_storage = System.getenv("EXTERNAL_STORAGE");
        rootEntry = new FileEntry();
        currentFsEntry = rootEntry;

        if(external_storage != null) {
            FileEntry ext = new FileEntry(new File(external_storage), rootEntry);
            rootEntry.Children.add(ext);
            ext.IsDirectory = true;
        }

        final String secondary_storage = System.getenv("SECONDARY_STORAGE");
        if(secondary_storage != null) {
            FileEntry sec = new FileEntry(new File(secondary_storage), rootEntry);
            rootEntry.Children.add(sec);
            sec.IsDirectory = true;
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public FileEntry getRootEntry() {
        return rootEntry;
    }

    public FileEntry getCurrentFsEntry() {
        return currentFsEntry;
    }

    public void setCurrentFsEntry(FileEntry currentFsEntry) {
        this.currentFsEntry = currentFsEntry;
    }

    public void scan(FileEntry entry) {
        scanDir(entry);
    }

    private void scanDir(FileEntry entry) {
        //Scan directory
        if (entry.Path != null) {
            entry.clear();

            File[] contents = new File(entry.Path).listFiles((FileFilter)new MangaFileFilter());
            if(contents != null) {
                for (File file : contents) {
                    FileEntry child = new FileEntry(file, entry);
                    entry.Children.add(child);
                }
                entry.sort();
            } else {
                //TODO alert
                Log.w("Manga", "Access denied to: " + entry.Path);
            }
        }
        eventListeners.notifyProgress(entry);
    }

    public void addChangeListener(final OnScanProgressObserver observer) {
        eventListeners.registerObserver(observer);
    }

    public void removeChangeListener(final OnScanProgressObserver observer) {
        eventListeners.unregisterObserver(observer);
    }
}
