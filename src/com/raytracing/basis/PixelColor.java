package com.raytracing.basis;

import java.awt.*;

/**
 * Represents color of a pixel that can take many samples and work with arithmetic operations.
 */
public class PixelColor {
    public static final PixelColor WHITE = new PixelColor(1, 1, 1);

    private static final double MIN = 0;
    private static final double MAX = 1;
    private double redSum;
    private double greenSum;
    private double blueSum;
    private int numSamples;

    /**
     * Initialize a pixel without color samples.
     */
    public PixelColor() {
        redSum = 0;
        greenSum = 0;
        blueSum = 0;
        numSamples = 0;
    }

    /**
     * Constructs a pixel with a single sample.
     *
     * @param red   the red value
     * @param green the green value
     * @param blue  the blue value
     */
    public PixelColor(double red, double green, double blue) {
        this();
        addSample(red, green, blue);
    }

    /**
     * Constructs a pixel from a given 3D-vector.
     *
     * @param vector a 3D-vector
     */
    public PixelColor(Vector3d vector) {
        this(vector.x(), vector.y(), vector.z());
    }

    /**
     * Adds a sample to this pixel.
     *
     * @param red   the red value
     * @param green the green value
     * @param blue  the blue value
     */
    public void addSample(double red, double green, double blue) {
        redSum += red;
        greenSum += green;
        blueSum += blue;
        numSamples += 1;
    }

    /**
     * Adds another pixel as a sample
     *
     * @param other another pixel
     */
    public void addSample(PixelColor other) {
        addSample(other.red(), other.green(), other.blue());
    }

    /**
     * Returns the red value.
     *
     * @return the red value
     */
    public double red() {
        return redSum / numSamples;
    }

    /**
     * Returns the green value.
     *
     * @return the green value
     */
    public double green() {
        return greenSum / numSamples;
    }

    /**
     * Returns the blue value.
     *
     * @return the blue value
     */
    public double blue() {
        return blueSum / numSamples;
    }

    /**
     * Returns the color of the pixel.
     *
     * @return the color
     */
    public Color color() {
        return new Color((float) clamp(red()), (float) clamp(green()), (float) clamp(blue()));
    }

    /**
     * Adds this pixel and another pixel.
     *
     * @param other another pixel
     * @return a pixel with color of the sum of two
     */
    public PixelColor add(PixelColor other) {
        return new PixelColor(red() + other.red(), green() + other.green(), blue() + other.blue());
    }

    /**
     * Subtracts another pixel from this pixel.
     *
     * @param other another pixel
     * @return the difference of the two
     */
    public PixelColor subtract(PixelColor other) {
        return new PixelColor(red() - other.red(), green() - other.green(), blue() - other.blue());
    }

    /**
     * Scales the pixel's color.
     *
     * @param scale the scale
     * @return the scaled color of the pixel
     */
    public PixelColor scale(double scale) {
        double red = red() * scale;
        double green = green() * scale;
        double blue = blue() * scale;
        return new PixelColor(red, green, blue);
    }

    /**
     * Clamps the given value between {@code MIN} and {@code MAX}.
     *
     * @param value the value
     * @return the clamped value
     */
    private static double clamp(double value) {
        if (value < PixelColor.MIN) {
            return PixelColor.MIN;
        } else if (value > PixelColor.MAX) {
            return PixelColor.MAX;
        }
        return value;
    }
}
