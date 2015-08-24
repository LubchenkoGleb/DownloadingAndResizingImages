package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ???? on 10.08.2015.
 */

public class DownloadThread implements Runnable{

    public LinkedBlockingQueue<Task> resizeQueue;
    public Queue<Task> tasksQueue;
    public static String finalFolder;

    DownloadThread(Queue tasksQueue, LinkedBlockingQueue resizeQueue) {
        this.tasksQueue = tasksQueue;
        this.resizeQueue = resizeQueue;
    }

    public void run() {

        while(true) {
            Task currentTask = tasksQueue.poll();

            if(currentTask.killTask == 1)
                break;
            else if(currentTask.killTask == 2) {
                resizeQueue.add(new Task(2));
                break;
            }
            else {
                try {
                    BufferedImage img = ImageIO.read(currentTask.url);

                    if (img != null) {
                        currentTask.name = img.hashCode() + ".png"; //System.currentTimeMillis() % 100000 + ".png";
                        currentTask.file = new File(finalFolder + currentTask.name);
                        ImageIO.write(img, "jpg", currentTask.file);
                        resizeQueue.add(currentTask);
                    }
                } catch (Exception e) {
                    System.out.println(currentTask.url);
                    System.err.println("Image wasn't download");
                }
            }
        }
    }
}
