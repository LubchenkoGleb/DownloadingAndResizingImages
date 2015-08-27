package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ResizeThread implements Runnable {

    private LinkedBlockingQueue<Task> resizeQueue, resultQueue;
    private ArrayList<Task> taskArray;

    ResizeThread(LinkedBlockingQueue resizeQueue, LinkedBlockingQueue resultQueue, ArrayList taskArray) {
        this.resultQueue = resultQueue;
        this.resizeQueue = resizeQueue;
        this.taskArray = taskArray;
    }

    public void run() {

        while (true) {

            try {
                Task currentTask = resizeQueue.take();

                if (currentTask.killTask)
                    break;
                else if(currentTask.file == null)
                    resultQueue.add(currentTask);
                else {
                    BufferedImage image = ImageIO.read(currentTask.file);
                    int type = image.getType();
                    BufferedImage resizedImage = new BufferedImage(Task.WIDTH, Task.HEIGHT, type);

                    Graphics2D graphics = resizedImage.createGraphics();
                    graphics.setComposite(AlphaComposite.Src);
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.drawImage(image, 0, 0, Task.WIDTH, Task.HEIGHT, null);
                    graphics.dispose();

                    ImageIO.write(resizedImage, "jpg", new File(Task.FINAL_FOLDER + currentTask.pictureName));
                    resultQueue.add(currentTask);
                }
            } catch (Exception e) {}

            synchronized (Task.resizeQueueIsEmpty) {
                if(resultQueue.size() == taskArray.size())
                    Task.resizeQueueIsEmpty.notify();
            }
        }
    }
}

