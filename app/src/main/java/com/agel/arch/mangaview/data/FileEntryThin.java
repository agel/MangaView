package com.agel.arch.mangaview.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by agel on 29/03/2015.
 */
public class FileEntryThin implements Comparable<FileEntryThin> {

    public String Path;
    public Boolean IsDirectory;
    public String Name;
    public FileEntryThin Parent;
    public ArrayList<FileEntryThin> Children = new ArrayList<>();
    public Lock ChildrenLock = new ReentrantLock();

    public FileEntryThin() {
        Name = "Root";
        IsDirectory = true;
    }

    public FileEntryThin(File file, FileEntryThin parent) {
        this.Path = file.getAbsolutePath();
        this.Name = file.getName();
        this.IsDirectory = file.isDirectory();
        this.Parent = parent;
        parent.addAsChild(this);
    }

    private void addAsChild(FileEntryThin child) {
        ChildrenLock.lock();
        try {
            Children.add(child);
        } finally {
            ChildrenLock.unlock();
        }
    }

    @Override
    public String toString() {
        return Name;
    }

    public int compareTo(FileEntryThin another) {
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
}