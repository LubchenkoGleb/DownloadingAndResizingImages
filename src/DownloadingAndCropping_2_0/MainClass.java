package DownloadingAndCropping_2_0;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by ãëåá on 05.08.2015.
 */
/*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!IMPORTANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    first of all set the correct way of JSON file at TaskInitialize class!!!!!!!
    Also set the correct way, where images will be downloading in the ResizeThread class!!!!!!
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */

class Task {

    String name;
    URL url;

    Task(String name) {
        try {
            url = new URL(name);
        }catch (MalformedURLException e) {
            url = null;
            System.err.println("Problem URL");
        }
    }
}
class TaskListInitialize {

    TaskListInitialize(Queue taskQueue) {

        try {
            FileReader reader = new FileReader("D:/URLpack.txt");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(reader);

            JSONArray jsonArray = (JSONArray)jsonObject.get("URLlist");

            for(int i=0; i<jsonArray.size(); i++) {
                Task task = new Task(jsonArray.get(i).toString());
                if (task != null) taskQueue.add(task);
            }

        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }
}

public class MainClass {

    public static LinkedBlockingQueue<Thread>resizeThreadQueue = new LinkedBlockingQueue<>(4);

    public static void main(String[] args) {

        LinkedBlockingQueue<BufferedImage>imgQueue = new LinkedBlockingQueue<>(16);
        Queue<Task> tasksQueue = new LinkedList<>();
        new TaskListInitialize(tasksQueue);

        int activeDownloadThreads = 0, activeResizeThreads = 0;                                             //downloaded and resized pictures adding to the queues only at the end
                                                                                                            //that's why we need counters to control amount of active threads
        long start_time = System.currentTimeMillis();

        while (true) {
            try {

                if ((activeDownloadThreads != 16) && !tasksQueue.isEmpty()) {                               //if we have a free threads for download - fill it
                    Thread downloadThread = new Thread(new DownloadThread(tasksQueue.poll(), imgQueue));
                    downloadThread.start();
                    activeDownloadThreads++;
                }
                else if (activeResizeThreads != 4) {                                                        //if we have a free resize threads - fill it too
                    BufferedImage img = imgQueue.poll(2, TimeUnit.SECONDS);                                 //wait while first image will be download
                    activeDownloadThreads--;
                    if (img != null) {
                        Thread resizeThread = new Thread(new ResizeThread(img));
                        resizeThread.start();
                        ++activeResizeThreads;
                    } else break;                                                                           //if any pictures wasn't added during 2second - close the program
                }
                else {
                    resizeThreadQueue.poll(2,TimeUnit.SECONDS);                                             //if all threads is active, wait while any resize thread becames free
                    activeResizeThreads--;
                }
            }catch (InterruptedException e) {}
        }

        long end_time = System.currentTimeMillis();
        System.out.println((end_time - start_time)/1000.0);
    }
}
