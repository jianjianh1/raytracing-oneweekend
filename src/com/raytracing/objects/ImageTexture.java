package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public record ImageTexture(BufferedImage image) implements Texture {
    /**
     * Constructs an image texture using the image with the file path
     */
    public ImageTexture(String filepath) throws IOException {
        this(ImageIO.read(new File(filepath)));
    }

    /**
     * Get the corresponding image color with a given (u, v) coordinate
     */
    @Override
    public PixelColor value(double u, double v, Vector3d p) {
        int width = image.getWidth();
        int height = image.getHeight();

        u = Math.clamp(u, 0.0, 1.0);
        v = 1.0 - Math.clamp(v, 0.0, 1.0);

        int i = (int)(u * width);
        int j = (int)(v * height);

        int rgb = image.getRGB(i, j);
        int r = (rgb & 0x00ff0000) >> 16;
        int g = (rgb & 0x0000ff00) >> 8;
        int b = rgb & 0x000000ff;

        // transform to gamma=2.0 space
        double red = r / 255.0;
        red *= red;
        double green = g / 255.0;
        green *= green;
        double blue = b / 255.0;
        blue *= blue;

        return new PixelColor(red, green, blue);
    }
}
