package com.agel.arch.mangaview.models;

import android.app.Fragment;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.agel.arch.mangaview.data.MangaFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageModelFragment extends Fragment {
    public static final String TAG = "ImageModelFragment";

    //Observer
    public interface ImageLoadObserver {
        void onImageReady(Bitmap bitmap);
        void onZoomedImageReady(Rect rectangle, Bitmap bitmap);
        void onLoadingChanged(boolean loading, String currentPath);
    }

    //Observable manager
    private class ImageLoadObservable extends Observable<ImageLoadObserver> {
        public void notifyImageReady(final Bitmap bitmap) {
            for (final ImageLoadObserver observer : mObservers) {
                uiHandler.post(new Runnable() {
                    public void run() {
                        observer.onImageReady(bitmap);
                    }
                });
            }
        }
        public void notifyZoomedImageReady(final Rect rectangle, final Bitmap bitmap) {
            for (final ImageLoadObserver observer : mObservers) {
                uiHandler.post(new Runnable() {
                    public void run() {
                        observer.onZoomedImageReady(rectangle, bitmap);
                    }
                });

            }
        }
        public void notifyLoadingChanged(final boolean loading, final String currentPath) {
            for (final ImageLoadObserver observer : mObservers) {
                //Theese update view will handle itself
                observer.onLoadingChanged(loading, currentPath);
            }
        }
    }
    //Thread pool
    public static class ImagePoolExecutor extends ThreadPoolExecutor {
        //Members
        public ImagePoolExecutor() {
            super(0, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    //Members
    private final ImagePoolExecutor executor = new ImagePoolExecutor();
    private final Handler uiHandler;
    private final ImageLoadObservable observers = new ImageLoadObservable();
    private int currentIndex;
    private BitmapRegionDecoder currentZoomDecoder;
    private File[] directoryFiles;
    private Rect zoomImgRect = new Rect();
    private Matrix zoomMatrix = new Matrix();
    private float mtxValues[] = new float[9];

    public ImageModelFragment() {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public boolean init(String imagePath) {
        File currFile = new File(imagePath);
        File currDir = currFile.getParentFile();

        if(currDir.exists())
        {
            //Construct array of all images in same folder
            directoryFiles = currDir.listFiles((FilenameFilter) new MangaFileFilter());
            Arrays.sort(directoryFiles);
            currentIndex = Arrays.binarySearch(directoryFiles, currFile);
        }

        return currentIndex >= 0 && directoryFiles.length > 0;
    }

    public void loadCurrent(RectF viewDimensions) {
        loadImage(viewDimensions, currentIndex);
    }

    public boolean loadNext(RectF viewDimensions) {
        if(currentIndex + 1 < directoryFiles.length)
        {
            currentIndex++;
            loadCurrent(viewDimensions);
            return false;
        }
        else
            return true;
    }

    public boolean loadPrevious(RectF viewDimensions) {
        if(currentIndex - 1 >= 0)
        {
            currentIndex--;
            loadCurrent(viewDimensions);
            return false;
        }
        else
            return true;
    }

    public Rect calcZoomRectangles(Rect displayZoom, Point displayPan, RectF screenDimensions, int imgWidth, int imgHeight) {
        //Enforcing Zoom limits
        if(displayZoom.left < 0)
            displayZoom.left = 0;

        if(displayZoom.top < 0)
            displayZoom.top = 0;

        if(displayZoom.right > screenDimensions.right)
            displayZoom.right = (int) screenDimensions.right;

        if(displayZoom.bottom > screenDimensions.bottom)
            displayZoom.bottom = (int) screenDimensions.bottom;

        zoomMatrix.setRectToRect(new RectF(0,0,imgWidth, imgHeight), screenDimensions, Matrix.ScaleToFit.CENTER);
        zoomMatrix.getValues(mtxValues);

        PointF zoomCenter = new PointF((displayZoom.left + ((displayZoom.right - displayZoom.left) / 2f)), (displayZoom.top + ((displayZoom.bottom - displayZoom.top) / 2f)));

        float centX = ((zoomCenter.x - mtxValues[Matrix.MTRANS_X]) / mtxValues[Matrix.MSCALE_X]);
        float centY = ((zoomCenter.y - mtxValues[Matrix.MTRANS_Y]) / mtxValues[Matrix.MSCALE_X]);
        float sizeX = ((displayZoom.right - displayZoom.left) / 2f);
        float sizeY = ((displayZoom.bottom - displayZoom.top) / 2f);

        centX += displayPan.x * 0.5f / mtxValues[Matrix.MSCALE_X];
        centY += displayPan.y * 0.5f / mtxValues[Matrix.MSCALE_X];

        //TODO ZoomFactor adjusted by user
        sizeX = sizeX  * 0.5f / (mtxValues[Matrix.MSCALE_X] * (100f/100f));
        sizeY = sizeY  * 0.5f / (mtxValues[Matrix.MSCALE_X] * (100f/100f));

        zoomImgRect.left = (int) (centX - sizeX);
        zoomImgRect.top = (int) (centY - sizeY);
        zoomImgRect.right = (int) (centX + sizeX);
        zoomImgRect.bottom = (int) (centY + sizeY);

        //Enforcing pan limits
        if(zoomImgRect.left < 0)
        {
            zoomImgRect.left = 0;
            zoomImgRect.right = (int) (sizeX * 2);
        }
        if(zoomImgRect.top < 0)
        {
            zoomImgRect.top = 0;
            zoomImgRect.bottom = (int) (sizeY * 2);
        }

        if(zoomImgRect.right > imgWidth)
        {
            zoomImgRect.right = imgWidth;
            zoomImgRect.left = (int) (imgWidth - 2 * sizeX);
        }

        if(zoomImgRect.bottom > imgHeight)
        {
            zoomImgRect.bottom = imgHeight;
            zoomImgRect.top = (int) (imgHeight - 2 * sizeY);
        }
        Log.d(TAG, String.format("Display: w%s h%s, Image: w%s h%s", displayZoom.right - displayZoom.left, displayZoom.bottom - displayZoom.top, zoomImgRect.right - zoomImgRect.left, zoomImgRect.bottom - zoomImgRect.top));
        return zoomImgRect;
    }

    public void loadZoomed(final RectF viewDimensions, final Rect screenPosition, final Point screenPan) {
        if(currentZoomDecoder == null) {
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                Rect imagePosition = calcZoomRectangles(screenPosition, screenPan, viewDimensions, currentZoomDecoder.getWidth(), currentZoomDecoder.getHeight());

                //Load part of the image
                try {
                    bitmap = currentZoomDecoder.decodeRegion(imagePosition, null);
                } catch (OutOfMemoryError e) {
                    Log.e(TAG, "Out of memory while zooming");
                } catch (Exception ex) {
                    Log.e(TAG, String.format("Wrong zoom dimensions: %s", imagePosition));
                }

                if (bitmap != null) {
                    observers.notifyZoomedImageReady(screenPosition, bitmap);
                }
            }
        });
    }

    private void loadImage(final RectF viewDimensions, final int idx) {
        observers.notifyLoadingChanged(true, directoryFiles[idx].getAbsolutePath());
        synchronized (this) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final File file = directoryFiles[idx];
                    Bitmap bitmap = null;
                    //reduce image on load
                    try {
                        //Decode image size
                        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                        decodeOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(new FileInputStream(file), null, decodeOptions);
                        //Calculate downsampling
                        decodeOptions.inSampleSize = calculateInSampleSize(decodeOptions, (int)viewDimensions.right, (int)viewDimensions.bottom);
                        decodeOptions.inJustDecodeBounds = false;
                        //Decode actual bitmap
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, decodeOptions);
                        //Prepare zoom decoder
                        if(currentZoomDecoder != null) {
                            currentZoomDecoder.recycle();
                        }
                        currentZoomDecoder = BitmapRegionDecoder.newInstance(new FileInputStream(file), false);

                    } catch (OutOfMemoryError | IOException e) {
                        Log.e(TAG, "Out of memory while loading");
                    }

                    observers.notifyLoadingChanged(false, null);

                    if(bitmap != null) {
                        observers.notifyImageReady(bitmap);
                    }
                }
            });
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
        if(currentZoomDecoder != null) {
            currentZoomDecoder.recycle();
        }
        executor.shutdownNow();
    }
}
