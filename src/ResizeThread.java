package DownloadingAndCropping_2_0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
* Created by глеб on 10.08.2015.
*/

public class ResizeThread implements Runnable{

    BufferedImage image;
    private static final int IMG_WIDTH = 1000;
    private static final int IMG_HEIGHT = 1000;

    ResizeThread(BufferedImage image) {
        this.image = image;
    }

    public void run() {
        int type = image.getType();
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D graphics = resizedImage.createGraphics();

        graphics.setComposite(AlphaComposite.Src);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(image, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);

        graphics.dispose();
        try {
            ImageIO.write(resizedImage, "jpg", new File("D:/images/" + resizedImage.hashCode() + ".png"));
            MainClass.resizeThreadQueue.add(Thread.currentThread());
        }catch (IOException e){}
    }
}

