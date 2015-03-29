package com.agel.arch.mangaview.data;

import java.io.File;

/**
 * Created by agel on 29/03/2015.
 */
public class FileEntry implements Comparable<FileEntry> {

    public String Path;
    public Boolean isDirectory;
    public String Name;
    public String Parent;

    public FileEntry() {
        Path = "";
        Name = "";
        isDirectory = false;
    }

    public FileEntry(File file) {
        Path = file.getAbsolutePath();
        Name = file.getName();
        isDirectory = file.isDirectory();
        Parent = file.getParent();
    }

    @Override
    public String toString() {
        return Name;
    }

    public int compareTo(FileEntry another) {
        if(this.isDirectory == another.isDirectory)
        {
            return this.Path.compareToIgnoreCase(another.Path);
        }
        else
        {
            if(this.isDirectory)
                return -1;
            else
                return 1;
        }
    }

    public FileEntry getParentEntry() {
        return new FileEntry(new File(Parent));
    }
}