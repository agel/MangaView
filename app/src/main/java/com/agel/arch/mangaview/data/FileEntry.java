package com.agel.arch.mangaview.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by agel on 29/03/2015.
 */
public class FileEntry implements Comparable<FileEntry> {

//    private String path;
//    private Boolean isDirectory;
//    private String Name;
    public File File;
    public FileEntry Parent;
    public ArrayList<FileEntry> Children = new ArrayList<>();
    public Lock ChildrenLock = new ReentrantLock();

    public FileEntry() {
        File = null;
    }

    public FileEntry(File file, FileEntry parent) {
//        path = file.getAbsolutePath();
//        Name = file.getName();
//        isDirectory = file.isDirectory();
        this.File = file;
        this.Parent = parent;
        ChildrenLock.lock();
        try {
            parent.Children.add(this);
            Collections.sort(parent.Children);
        } finally {
            ChildrenLock.unlock();
        }
    }

    @Override
    public String toString() {
        return File != null ? File.getName() : "Root";
    }

    public boolean isDirectory() {
        return File != null ? File.isDirectory() : true;
    }

    public String path() {
        return File != null ? File.getAbsolutePath() : "/";
    }


    public int compareTo(FileEntry another) {
        if(this.isDirectory() == another.isDirectory())
        {
            return this.path().compareToIgnoreCase(another.path());
        }
        else
        {
            if(this.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}