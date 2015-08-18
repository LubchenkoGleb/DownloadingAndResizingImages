package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;

/**
 * Created by глеб on 17.08.2015.
 */
public class Task {

    String name;
    URL url;
    File file;
    boolean killTask;

    public Task(boolean check) {
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

    public static void TaskListInitialize(Queue taskQueue) {

        try {

            FileReader reader = new FileReader(MainClass.URLPack);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(reader);

            JSONArray jsonArray = (JSONArray)jsonObject.get("URLlist");
            System.out.println(jsonArray.size() + " links was found");

            for(int i=0; i<jsonArray.size(); i++) {
                Task currentTask = new Task(jsonArray.get(i).toString());
                if(currentTask.url != null)
                    taskQueue.add(currentTask);
            }
            System.out.println(taskQueue.size() + " links was checked and added");

        }catch (Exception ex) {}
    }
}
