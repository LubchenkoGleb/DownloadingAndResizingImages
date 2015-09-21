package com.shpp.glubchenko.learning.downloadAndResize_4_0;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by glebl on 20.09.2015.
 */

public class DownloadThread implements Runnable{

    public LinkedBlockingQueue<Task> downloadQueue, resizeQueue;

    DownloadThread(LinkedBlockingQueue downloadQueue, LinkedBlockingQueue resizeQueue) {
        this.downloadQueue = downloadQueue;
        this.resizeQueue = resizeQueue;
    }

    public void run() {

        Task currentTask = null;

        while(true) {

            try {
                currentTask = downloadQueue.take();

                if(currentTask.killTask)
                    break;
                else if(currentTask.e == null){

                    BufferedImage img = ImageIO.read(currentTask.url);
                    currentTask.file = new File(Task.finalForlder + currentTask.pictureName);
                    ImageIO.write(img, "jpg", currentTask.file);

                }

            }catch (Exception exception) {

                currentTask.e = exception;
                System.err.println(currentTask.url + " - Image wasn't download");
            }

            resizeQueue.add(currentTask);
        }
    }
}
