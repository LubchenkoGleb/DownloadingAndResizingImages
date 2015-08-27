package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ???? on 10.08.2015.
 */

public class DownloadThread implements Runnable{

    public LinkedBlockingQueue<Task> downloadQueue, resizeQueue;
    public Task currentTask;

    DownloadThread(LinkedBlockingQueue downloadQueue, LinkedBlockingQueue resizeQueue) {
        this.downloadQueue = downloadQueue;
        this.resizeQueue = resizeQueue;
    }

    public void run() {

        while(true) {
            try {
                currentTask = downloadQueue.take();

                if(currentTask.killTask)
                    break;
                else {
                    BufferedImage img = ImageIO.read(currentTask.url);

                    currentTask.file = new File(Task.FINAL_FOLDER + currentTask.pictureName);
                    ImageIO.write(img, "png", currentTask.file);
                    resizeQueue.add(currentTask);
                }
            }catch (Exception e) {
                currentTask.file = null;
                resizeQueue.add(currentTask);
                System.err.println(currentTask.url + " - Image wasn't download");
            }

            synchronized (Task.downloadQueueIsEmpty) {
                if(downloadQueue.isEmpty())
                    Task.downloadQueueIsEmpty.notify();
            }
        }
    }
}
