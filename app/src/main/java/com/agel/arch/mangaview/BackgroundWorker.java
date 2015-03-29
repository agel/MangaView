package com.agel.arch.mangaview;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by agel on 29/03/2015.
 */
public class BackgroundWorker {

    //Statics
    private static BackgroundWorker instance;
    public static BackgroundWorker getInstance() {
        if(instance == null)
            instance = new BackgroundWorker();

        return instance;
    }
    //Members
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final ArrayList<Thread> workers = new ArrayList<>();

    private BackgroundWorker() {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        if(cpuCount == 1) cpuCount++;

        for (; cpuCount > 1; cpuCount--)
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            queue.take().run();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            });
            workers.add(thread);
            thread.start();
        }
    }

    public boolean offer(Runnable work) {
        return queue.offer(work);
    }

    public void put(Runnable work) {
        try {
            queue.put(work);
        } catch (InterruptedException e) {
            return;
        }
    }
}
