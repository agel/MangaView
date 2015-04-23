package com.agel.arch.mangaview.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class MangaFileFilter implements FileFilter, FilenameFilter {

    @Override
    public boolean accept(File file) {
        return !file.isHidden() && (file.isDirectory() || checkFile(file.getName()));
    }

    @Override
    public boolean accept(File dir, String filename) {
        File file = new File(filename);
        return !file.isHidden() && !file.isDirectory() && checkFile(file.getName());

    }

    private boolean checkFile(String file) {
        String filename = file.toLowerCase();
        return (filename.endsWith(".png")
                || filename.endsWith(".jpg")
                || filename.endsWith(".jpeg"));
    }
}
