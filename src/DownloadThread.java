package DownloadingAndCropping_2_0;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by глеб on 10.08.2015.
 */

public class DownloadThread implements Runnable{

    LinkedBlockingQueue<BufferedImage> downloadQueue = null;
    Task task;

    DownloadThread(Task task, LinkedBlockingQueue queue) {
        this.task = task;
        downloadQueue = queue;
    }

    public void run() {
        BufferedImage img;
        try{
            img = ImageIO.read(task.url);
            if(img == null) return;

        }catch (IOException e) {
            e.printStackTrace();
            return;
        }
        downloadQueue.add(img);
    }
}
