package com.raytracing.oneweekend;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RayTracer {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    public static void main(String[] args) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                float r = (float) x / (WIDTH - 1);
                float g = (float) y / (WIDTH - 1);
                float b = 0.25F;

                graphics.setColor(new Color(r, g, b));
                graphics.fillRect(x, y, 1, 1);
            }
        }

        ImageIO.write(image, "PNG", new File("sample.png"));
    }
}
