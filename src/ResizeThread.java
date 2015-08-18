package com.shpp.lubchenko.learning.downloadAndresize_3_0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ResizeThread implements Runnable {

    private static final int IMG_WIDTH = 1000;
    private static final int IMG_HEIGHT = 1000;

    public void run() {

        while (true) {
            try {
                Task currentTask = MainClass.resizeThreadQueue.take();

                if (currentTask.killTask)
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

                    ImageIO.write(resizedImage, "jpg", new File(MainClass.finalFolder + currentTask.name));
                    MainClass.resizeThreadQueue.add(currentTask);
                }
            } catch (Exception e) {}
        }
    }
}

