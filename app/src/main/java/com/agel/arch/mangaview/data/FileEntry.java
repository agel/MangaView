package com.agel.arch.mangaview.data;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by agel on 29/03/2015.
 */
public class FileEntry implements Comparable<FileEntry> {

    public String Path;
    public Boolean isDirectory;
    public String Name;
    public FileEntry Parent;
    public ArrayList<FileEntry> Children = new ArrayList<>();

    public FileEntry() {
        Path = "";
        Name = "";
        isDirectory = true;
    }

    public FileEntry(File file, FileEntry parent) {
        Path = file.getAbsolutePath();
        Name = file.getName();
        isDirectory = file.isDirectory();
        Parent = parent;
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
}