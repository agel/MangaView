package com.agel.arch.mangaview.data;

import java.io.File;
import java.io.FileFilter;

public class MangaFileFilter implements FileFilter {
    private boolean includeDirectories;

    public MangaFileFilter(boolean includeDirectories) {

        this.includeDirectories = includeDirectories;
    }

    @Override
    public boolean accept(File file) {
        String filename = file.getName().toLowerCase();

        if(file.isHidden())
            return false;

        if(file.isDirectory())
            return includeDirectories;

        return (filename.endsWith(".png") ||
                filename.endsWith(".jpg") ||
                filename.endsWith(".jpeg") ||
                filename.endsWith(".gif"));
    }
}
