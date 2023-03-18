package com.raytracing.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Represents a image in raytracing programs
 */
public class Image implements Closeable {
    BufferedImage data;
    Graphics2D graphics;

    /**
     * Constructs an RGB image
     *
     * @param width  the width of the image
     * @param height the height of the image
     */
    public Image(int width, int height) {
        data = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = data.createGraphics();
    }

    /**
     * Fill pixel (x, y) with the given color
     *
     * @param x     the x-coordinate of the pixel
     * @param y     the y-coordinate of the pixel
     * @param color a color
     */
    public void fillPixel(int x, int y, Color color) {
        graphics.setColor(color);
        graphics.fillRect(x, y, 1, 1);
    }

    /**
     * Save the image to a PNG file
     * @param fileName name of a PNG file
     * @throws IOException if it cannot write the file
     */
    public void save(String fileName) throws IOException {
        ImageIO.write(data, "PNG", new File(fileName));
    }

    @Override
    public void close() {
        graphics.dispose();
    }
}
