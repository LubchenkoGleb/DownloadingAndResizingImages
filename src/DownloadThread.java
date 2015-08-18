package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by ???? on 10.08.2015.
 */

public class DownloadThread implements Runnable{

    static int pictureCount = 1;

    public void run() {

        while(true) {

            Task currentTask = MainClass.tasksQueue.poll();

            if(currentTask.killTask) {
                MainClass.resizeThreadQueue.addFirst(currentTask);
                break;
            }
            else {
                try {
                    BufferedImage img = ImageIO.read(currentTask.url);

                    if (img != null) {
                        currentTask.name = "picture_" + pictureCount++ + ".png";
                        currentTask.file = new File(MainClass.finalFolder + currentTask.name);
                        ImageIO.write(img, "jpg", currentTask.file);
                        MainClass.resizeThreadQueue.add(currentTask);
                    }
                } catch (Exception e) {
                    System.out.println(currentTask.url);
                    System.err.println("Image wasn't download");
                }
            }
        }
    }
}
