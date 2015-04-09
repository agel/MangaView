package com.agel.arch.mangaview.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.agel.arch.mangaview.R;
import com.agel.arch.mangaview.data.MangaFileFilter;
import com.agel.arch.mangaview.data.Settings;

import java.io.File;
import java.util.Arrays;

/**
 * Created by agel on 09/04/2015.
 */
public class ImageModelFragment extends Fragment {

    public static final String TAG = "ImageModelFragment";
    private int currentIndex;
    private File[] directoryFiles;

    public void init(String imagePath) {
        File currFile = new File(imagePath);
        File currDir = currFile.getParentFile();

        if(currDir.exists())
        {
            //Construct array of all images in same folder
            directoryFiles = currDir.listFiles(new MangaFileFilter(false));
            Arrays.sort(directoryFiles);
            currentIndex = Arrays.binarySearch(directoryFiles, currFile);
        }

        if(currentIndex >= 0 && directoryFiles.length > 0) //if all went OK - setup image
        {
            //Load image end finish calculations
            loadImage(true);
        }
        else
            Settings.makeToast(getActivity(), getActivity().getString(R.string.msg_file_not_exist));
    }

    private void loadImage(boolean forward) {

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
