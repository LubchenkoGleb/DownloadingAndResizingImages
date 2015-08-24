package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by глеб on 17.08.2015.
 */
public class Task {

    String name;
    URL url;
    File file;
    int killTask; // if killTask == 0 - usual Task, else if killTask == 1 - kill DonloadingThread, else if killTask == 2 - kill Resizing Task

    public Task(int check) {
        killTask = check;
    }

    public Task(String name) {
        try {
            url = new URL(name);
        }catch (MalformedURLException e) {
            url = null;
            System.err.println("Damaged URL");
        }
    }

    public static void InitializeTaskList(int amountOfDownloadingThreads, int amountOfResizingThreads, Queue taskQueue, JSONObject jsonObject) {

        JSONArray jsonArray = (JSONArray)jsonObject.get("URLlist");
        System.out.println(jsonArray.size() + " links was found");

        for(int i=0; i<jsonArray.size(); i++) {
            Task currentTask = new Task(jsonArray.get(i).toString());
            if(currentTask.url != null)
                taskQueue.add(currentTask);
        }
        System.out.println(taskQueue.size() + " links was checked and added");
        for (int i = 0; i < amountOfDownloadingThreads - amountOfResizingThreads; i++) {
            taskQueue.add(new Task(1));
        }
        for (int i = 0; i < amountOfResizingThreads; i++) {
            taskQueue.add(new Task(2));
        }

    }
}
