package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClass {

    public static void main(String[] args) {

        ArrayList<Task> taskArray = new ArrayList<>();
        LinkedBlockingQueue<Task> downloadQueue = new LinkedBlockingQueue<>(), resizeQueue = new LinkedBlockingQueue<>(), resultQueue = new LinkedBlockingQueue<>();
        long start_time = System.currentTimeMillis();

        Task.initializeTaskArrayAndConstants(args, taskArray);

        for (Task currentTask : taskArray)
            downloadQueue.add(currentTask);

        System.out.println("\nprocessing is started, wait a few seconds...\n");

        ArrayList<Thread> activeDownloadThreads = new ArrayList<>(Task.AMOUNT_OF_DOWNLOAD_THREADS);
        for (int i = 0; i < Task.AMOUNT_OF_DOWNLOAD_THREADS; i++) {
            Thread thread = new Thread(new DownloadThread(downloadQueue, resizeQueue));
            activeDownloadThreads.add(thread);
            thread.start();
        }

        ArrayList<Thread> activeResizeThreads = new ArrayList<>(Task.AMOUNT_OF_RESIZE_THREADS);
        for (int i = 0; i < Task.AMOUNT_OF_RESIZE_THREADS; i++) {
            Thread thread = new Thread(new ResizeThread(resizeQueue, resultQueue, taskArray));
            activeResizeThreads.add(thread);
            thread.start();
        }

        synchronized (Task.downloadQueueIsEmpty) { // object will notify if download queue is empty
            try {
                Task.downloadQueueIsEmpty.wait();
            }catch (Exception e) {}
        }

        for (Thread thread : activeDownloadThreads) {
            downloadQueue.add(new Task(true));
        }

        synchronized (Task.resizeQueueIsEmpty) { // object will notify if resultQueue.size == taskArray.size
            try {
                Task.resizeQueueIsEmpty.wait();
            }catch (Exception e) {}
        }

        for (Thread thread : activeResizeThreads) {
            resizeQueue.add(new Task(true));
        }

        System.out.println(resultQueue.size() + " tasks was processed");
        long end_time = System.currentTimeMillis();
        System.out.println("used time = " + (end_time - start_time) / 1000.0);
    }
}