package com.shpp.glubchenko.learning.downloadAndResize_4_0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by glebl on 20.09.2015.
 */

public class MainClass {

    static LinkedBlockingQueue<Task> downloadQueue = new LinkedBlockingQueue<>(), resizeQueue = new LinkedBlockingQueue<>(), resultQueue = new LinkedBlockingQueue<>();
    static int amountOfDownloadThreads, amountOfResizeThreads;
    static ArrayList<Task> taskArray;

    public static void main(String[] args) {

        taskArray = Task.initializeTaskArrayAndConstants(args);
        initializeAmountOfThreads();

        for (Task currentTask : taskArray)
            downloadQueue.add(currentTask);

        System.out.println("Download queue size = " + downloadQueue.size());
        long start_time = System.currentTimeMillis();
        System.out.println("processing is started, wait a few seconds...\n");
        int processed = realMain(args);
        long end_time = System.currentTimeMillis();
        System.out.println("used time = " + (end_time - start_time) / 1000.0);
        System.out.println(processed + " tasks was processed");

    }

    public static int realMain(String[] args) {

        Queue<Thread> activeDownloadThreads = startDownloadThreads(downloadQueue, resizeQueue);
        Queue<Thread> activeResizeThreads = startResizeThreads(resizeQueue, resultQueue);

        try {
            for (int i = 0; i < taskArray.size(); i++)
                resultQueue.take();

            for (Thread thread : activeDownloadThreads)
                downloadQueue.add(new KillTask());

            for (Thread thread : activeResizeThreads)
                resizeQueue.add(new KillTask());

            for (Thread thread : activeResizeThreads)
                if (thread.isAlive())
                    thread.join();

        } catch (Exception e) {
            System.err.println("tasks were lost");
        }

        return taskArray.size();
    }

    public static void initializeAmountOfThreads() {

        try {

            JsonElement jsonElement = new JsonParser().parse(new FileReader(Task.URLPackName));
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            amountOfDownloadThreads = jsonObject.get("amountOfDownloadingThreads").getAsInt();
            amountOfResizeThreads = jsonObject.get("amountOfResizingThreads").getAsInt();

        } catch (Exception e) {
            System.err.println("values wasn't initialized");
        }

    }

    public static Queue startDownloadThreads(LinkedBlockingQueue<Task> from, LinkedBlockingQueue<Task> to) {

        Queue<Thread> threadPull = new LinkedList<>();


        for (int i = 0; i < amountOfDownloadThreads; i++) {

            Thread thread = new Thread(new DownloadThread(from, to));
            threadPull.add(thread);
            thread.start();
        }
        return threadPull;
    }

    public static Queue startResizeThreads(LinkedBlockingQueue<Task> from, LinkedBlockingQueue<Task> to) {

        Queue<Thread> threadPull = new LinkedList<>();

        for (int i = 0; i < amountOfResizeThreads; i++) {

            Thread thread = new Thread(new ResizeThread(from, to));
            threadPull.add(thread);
            thread.start();
        }
        return threadPull;
    }
}