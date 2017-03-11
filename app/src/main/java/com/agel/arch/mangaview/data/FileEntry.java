package com.agel.arch.mangaview.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by agel on 29/03/2015.
 */
public class FileEntry implements Comparable<FileEntry> {

    public String Path;
    public Boolean IsDirectory;
    public String Name;
    public FileEntry Parent;
    public final List<FileEntry> Children = new ArrayList<>();

    public FileEntry() {
        Name = "Root";
        IsDirectory = true;
    }

    public FileEntry(File file, FileEntry parent) {
        this.Path = file.getAbsolutePath();
        this.Name = file.getName();
        this.IsDirectory = file.isDirectory();
        this.Parent = parent;
    }

    @Override
    public String toString() {
        return Name;
    }

    public int compareTo(FileEntry another) {
        if(this.IsDirectory == another.IsDirectory)
        {
            return this.Path.compareToIgnoreCase(another.Path);
        }
        else
        {
            if(this.IsDirectory)
                return -1;
            else
                return 1;
        }
    }

    public void clear() {
        Children.clear();
    }

    public void sort() {
        Collections.sort(Children);
    }
}