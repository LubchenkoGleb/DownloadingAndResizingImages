package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ResizeThread implements Runnable {

    public static int IMG_WIDTH;
    public static int IMG_HEIGHT;
    public static String finalFolder;
    private LinkedBlockingQueue<Task> resizeQueue;
    private Queue<Task> resaultQueue;

    ResizeThread(LinkedBlockingQueue resizeQueue, Queue resaultQueue) {
        this.resaultQueue = resaultQueue;
        this.resizeQueue = resizeQueue;
    }

    public void run() {

        while (true) {

            try {
                Task currentTask = resizeQueue.take();

                if (currentTask.killTask == 2)
                    break;
                else {
                    BufferedImage image = ImageIO.read(currentTask.file);
                    int type = image.getType();
                    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);

                    Graphics2D graphics = resizedImage.createGraphics();
                    graphics.setComposite(AlphaComposite.Src);
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.drawImage(image, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
                    graphics.dispose();

                    ImageIO.write(resizedImage, "jpg", new File(finalFolder + currentTask.name));
                    resaultQueue.add(currentTask);
                }
            } catch (Exception e) {
                System.err.println("null");
            }
        }
    }
}

