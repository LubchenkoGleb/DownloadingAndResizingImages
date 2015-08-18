package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClass {

    /*I am using deque because at the beginning of program this queue is empty, that's why I started to push kill task in the begging only when
    * I know that download threads have done their work and dead*/
    public static LinkedBlockingDeque<Task> resizeThreadQueue = new LinkedBlockingDeque<>();
    public static LinkedBlockingQueue<Task>tasksQueue = new LinkedBlockingQueue<>();
    static String URLPack, finalFolder;

    public static void main(String[] args) {

        if(args.length != 0) {
            URLPack = args[0];
            finalFolder = args[1];
        }
        else {
            URLPack = "URLPack.txt";
            finalFolder = "";
        }
        Task.TaskListInitialize(tasksQueue);

        int downloadThreadsAmount = (tasksQueue.size() < 16) ? tasksQueue.size() : 16;
        int resizeThreadsAmount = (tasksQueue.size() < 4) ? tasksQueue.size() : 4;
        long start_time = System.currentTimeMillis();

        for (int i = 0; i < downloadThreadsAmount; i++) {
            tasksQueue.add(new Task(true));                     //pushing kill tasks in the task Queue
            Thread thread = new Thread(new DownloadThread());
            thread.start();
        }
        for (int i = 0; i < resizeThreadsAmount; i++) {
            Thread thread = new Thread(new ResizeThread());
            thread.start();
        }
        while (Thread.activeCount() != 2) {                     //waiting all threads ending
            try {
                Thread.currentThread().sleep(300);
            } catch (InterruptedException e) {}
        }

        System.out.println(resizeThreadQueue.size() - 12 + " images was successfully downloaded and resized ");
        long end_time = System.currentTimeMillis();
        System.out.println("used time = " + (end_time - start_time)/1000.0);
    }
}