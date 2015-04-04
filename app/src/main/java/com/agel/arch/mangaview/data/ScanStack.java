package com.agel.arch.mangaview.data;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Agel on 01.04.2015.
 */
public class ScanStack {
    private ScanStack parent;
    private String id;
    private HashMap<String, ScanStack> children = new HashMap<>();
    private Lock lck = new ReentrantLock();

    public ScanStack push(String path) {
        lck.lock();
        try {
            ScanStack stack = new ScanStack();
            stack.id = path;
            children.put(stack.id , stack);
            return stack;
        } finally {
            lck.unlock();
        }
    }

    public boolean pop(String path) {
        return false;
    }
}
