package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClass {

    public static void main(String[] args) {

        LinkedBlockingQueue<Task> resizeQueue = new LinkedBlockingQueue<>();
        Queue<Task> resaultQueue = new LinkedList<>(), tasksQueue = new LinkedBlockingQueue<>();
        String URLPack, finalFolder;
        int amountOfDownloadingThreads = 0, amountOfResizeThreads = 0;

        if(args.length != 0) {
            URLPack = args[0];
            finalFolder = args[1];
        }
        else {
            URLPack = "URLPack.txt";
            finalFolder = "";
        }
        ResizeThread.finalFolder = finalFolder;
        DownloadThread.finalFolder = finalFolder;

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(URLPack));

            amountOfDownloadingThreads = Math.toIntExact((Long)jsonObject.get("amountOfDownloadingThreads"));
            amountOfResizeThreads = Math.toIntExact((Long)jsonObject.get("amountOfResizingThreads"));
            ResizeThread.IMG_HEIGHT = Math.toIntExact((Long) jsonObject.get("imageHeight"));
            ResizeThread.IMG_WIDTH = Math.toIntExact((Long) jsonObject.get("imageWidth"));
            Task.InitializeTaskList(amountOfDownloadingThreads, amountOfResizeThreads, tasksQueue, jsonObject);
        }catch (Exception e) {}

        long start_time = System.currentTimeMillis();

        for (int i = 0; i < amountOfDownloadingThreads; i++) {
            Thread thread = new Thread(new DownloadThread(tasksQueue, resizeQueue));
            thread.start();
        }

        Queue<Thread> activeThreads = new LinkedList<>();
        for (int i = 0; i < amountOfResizeThreads; i++) {
            Thread thread = new Thread(new ResizeThread(resizeQueue, resaultQueue));
            activeThreads.add(thread);
            thread.start();
        }

        while (!activeThreads.isEmpty()) {
            try {
                activeThreads.peek().join();
            }catch (Exception e) {}
            activeThreads.poll();
        }

        System.out.println(resaultQueue.size() + " images was successfully downloaded and resized ");
        long end_time = System.currentTimeMillis();
        System.out.println("used time = " + (end_time - start_time) / 1000.0);
    }
}