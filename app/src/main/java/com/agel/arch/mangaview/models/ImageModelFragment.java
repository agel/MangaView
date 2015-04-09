package com.agel.arch.mangaview.models;

import android.app.Fragment;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.agel.arch.mangaview.data.MangaFileFilter;
import com.agel.arch.mangaview.data.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageModelFragment extends Fragment {
    public static final String TAG = "ImageModelFragment";

    //Observer
    public interface ImageLoadObserver {
        void onImageReady(Bitmap bitmap);
        void onRectangleReady(Rect rectangle, Bitmap bitmap);
        void onLoadingChanged(boolean loading, String currentPath);
    }

    //Observable manager
    private class ImageLoadObservable extends Observable<ImageLoadObserver> {
        public void notifyImageReady(Bitmap bitmap) {
            for (final ImageLoadObserver observer : mObservers) {
                observer.onImageReady(bitmap);
            }
        }
        public void notifyRectangleChanged(Rect rectangle, Bitmap bitmap) {
            for (final ImageLoadObserver observer : mObservers) {
                observer.onRectangleReady(rectangle, bitmap);
            }
        }
        public void notifyLoadingChanged(boolean loading, String currentPath) {
            for (final ImageLoadObserver observer : mObservers) {
                observer.onLoadingChanged(loading, currentPath);
            }
        }
    }
    //Thread pool
    public static class ImagePoolExecutor extends ThreadPoolExecutor {

        //Members
        public ImagePoolExecutor() {
            super(0, 1, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    //Members
    private final ImagePoolExecutor executor = new ImagePoolExecutor();
    private final Handler uiHandler;
    private final ImageLoadObservable observers = new ImageLoadObservable();
    private int currentIndex;
    private File[] directoryFiles;

    public ImageModelFragment() {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public boolean init(String imagePath) {
        File currFile = new File(imagePath);
        File currDir = currFile.getParentFile();

        if(currDir.exists())
        {
            //Construct array of all images in same folder
            directoryFiles = currDir.listFiles(new MangaFileFilter(false));
            Arrays.sort(directoryFiles);
            currentIndex = Arrays.binarySearch(directoryFiles, currFile);
        }

        return currentIndex >= 0 && directoryFiles.length > 0;

    }

    public void loadCurrent() {
        loadImage(currentIndex);
    }

    public boolean loadNext() {
        if(currentIndex + 1 < directoryFiles.length)
        {
            currentIndex++;
            loadCurrent();
            return false;
        }
        else
            return true;
    }

    public boolean loadPrevious() {
        if(currentIndex - 1 >= 0)
        {
            currentIndex--;
            loadCurrent();
            return false;
        }
        else
            return true;
    }

    public void loadRectangle() {

    }

    private void loadImage(final int idx) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                final Bitmap bitmap;

                //reduce image on load
                try {

                    //Decode image size
                    BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                    decodeOptions.inJustDecodeBounds = true;

                    BitmapFactory.decodeStream(new FileInputStream(directoryFiles[idx]), null, decodeOptions);

                    //Free memory for new image
                    System.gc();

                    //Decode with inSampleSize
                    if(decodeOptions.outHeight * decodeOptions.outWidth * 4 > Settings.IMAGE_MAX_SIZE) {
                        decodeOptions.inSampleSize = (int) Math.round((Math.sqrt(decodeOptions.outHeight * decodeOptions.outWidth * 4) / Math.sqrt(Settings.IMAGE_MAX_SIZE)) + 0.5);
                    }
                    decodeOptions.inJustDecodeBounds = false;

                    bitmap = BitmapFactory.decodeStream(new FileInputStream(directoryFiles[idx]), null , decodeOptions);

                    uiHandler.post(new Runnable() {
                        public void run() {
                            observers.notifyImageReady(bitmap);
                        }
                    });

                }
                catch (OutOfMemoryError | FileNotFoundException e) {
                   //TODO handle out of memory
                }
            }
        });
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void addChangeListener(final ImageLoadObserver observer) {
        observers.registerObserver(observer);
    }

    public void removeChangeListener(final ImageLoadObserver observer) {
        observers.unregisterObserver(observer);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
