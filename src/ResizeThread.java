package com.shpp.glubchenko.learning.downloadAndResize_4_0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by glebl on 20.09.2015.
 */
public class ResizeThread implements Runnable {

    private LinkedBlockingQueue<Task> resizeQueue, resultQueue;

    ResizeThread(LinkedBlockingQueue resizeQueue, LinkedBlockingQueue resultQueue) {
        this.resultQueue = resultQueue;
        this.resizeQueue = resizeQueue;
    }

    public void run() {

        Task currentTask = null;

        while (true) {

            try {
                currentTask = resizeQueue.take();

                if (currentTask.killTask)
                    break;
                else if(currentTask.e == null) {

                    BufferedImage image = ImageIO.read(currentTask.file);
                    int type = image.getType();
                    BufferedImage resizedImage = new BufferedImage(currentTask.width, currentTask.height, type);

                    Graphics2D graphics = resizedImage.createGraphics();
                    graphics.setComposite(AlphaComposite.Src);
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.drawImage(image, 0, 0, currentTask.width, currentTask.height, null);
                    graphics.dispose();

                    ImageIO.write(resizedImage, "jpg", new File(Task.finalForlder + currentTask.pictureName));
                }

            } catch (Exception e) {

                currentTask.e = e;
                System.err.println("Image wasn`t resized");

            }

            resultQueue.add(currentTask);
        }
    }
}

