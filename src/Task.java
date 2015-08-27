package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by глеб on 17.08.2015.
 */
public class Task {

    public String link, pictureName;
    public URL url;
    public File file;
    public boolean killTask = false;
    public static String FINAL_FOLDER, URL_PACK_NAME;
    public static int WIDTH, HEIGHT, AMOUNT_OF_DOWNLOAD_THREADS, AMOUNT_OF_RESIZE_THREADS;
    public static Object downloadQueueIsEmpty = new Object(), resizeQueueIsEmpty = new Object();

    public Task(boolean check) {
        killTask = check;
    }

    public Task(String link) {
        try {
            url = new URL(link);
        }catch (MalformedURLException e) {
            url = null;
            System.err.println(link + " - Damaged URL, task wasn't added ");
        }
    }

    public static void initializeTaskArrayAndConstants(String[] args, ArrayList<Task> taskArray) {

        if(args.length == 1)
            URL_PACK_NAME = args[0];
        else
            URL_PACK_NAME = "URLpack.txt";

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(URL_PACK_NAME));

            AMOUNT_OF_DOWNLOAD_THREADS = Integer.parseInt(jsonObject.get("amountOfDownloadingThreads").toString());
            AMOUNT_OF_RESIZE_THREADS = Integer.parseInt(jsonObject.get("amountOfResizingThreads").toString());
            HEIGHT = Integer.parseInt(jsonObject.get("imageHeight").toString());
            WIDTH = Integer.parseInt(jsonObject.get("imageWidth").toString());
            FINAL_FOLDER = jsonObject.get("finalFolderPath").toString();

            JSONArray jsonArray = (JSONArray) jsonObject.get("URLlist");
            System.out.println(jsonArray.size() + " objects was found");

            for (int i = 0; i < jsonArray.size(); i++) {
                Task task = new Task(jsonArray.get(i).toString());
                if(task.url != null) {
                    task.pictureName = "image_" + (i+1) + ".png";
                    taskArray.add(task);
                }
            }

            System.out.println(taskArray.size() + " tasks was added");

        }catch (Exception e) {}
    }
}
