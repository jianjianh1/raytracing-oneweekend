package com.raytracing.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Represents a canvas in raytracing programs
 */
public class Canvas implements Closeable {
    BufferedImage data;
    Graphics2D graphics;

    /**
     * Constructs an RGB image
     *
     * @param width  the width of the image
     * @param height the height of the image
     */
    public Canvas(int width, int height) {
        data = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = data.createGraphics();
    }

    /**
     * Fill pixel (x, y) with the given color.
     * Positive x direction is from left to right
     * Positive y direction is from bottom to top
     *
     * @param x     the x-coordinate of the pixel
     * @param y     the y-coordinate of the pixel
     * @param color a color
     */
    public void fillPixel(int x, int y, Color color) {
        graphics.setColor(color);
        graphics.fillRect(x, data.getHeight() - 1 - y, 1, 1);
    }

    /**
     * Save the image to an image file
     *
     * @param fileName name of an image file
     * @throws IOException if it cannot write the file
     */
    public void save(String fileName) throws IOException {
        String[] tokens = fileName.split("\\.");
        ImageIO.write(data, tokens[tokens.length - 1], new File(fileName));
    }

    /**
     * Implements Closeable
     */
    @Override
    public void close() {
        graphics.dispose();
    }
}
