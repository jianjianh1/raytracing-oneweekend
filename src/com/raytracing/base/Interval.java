package com.raytracing.base;

public record Interval(double min, double max) {
    public static final Interval EMPTY = new Interval();
    public static final Interval UNIVERSE = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    /**
     * Constructs an empty interval
     */
    public Interval() {
        this(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    /**
     * The size (length) of interval
     * @return the size
     */
    public double size() {
        return max - min;
    }

    /**
     * @param x a real number
     * @return whether the interval contains a number as a closed interval
     */
    public boolean contains(double x) {
        return min <= x && x <= max;
    }

    /**
     * @param x a real number
     * @return whether the interval contains a number as an open interval
     */
    public boolean surrounds(double x) {
        return min < x && x < max;
    }

    /**
     * Clamps the value x within the interval
     * @param x a real number
     * @return the clamped value
     */
    public double clamp(double x) {
        return Math.clamp(x, min, max);
    }

    /**
     * Expands this interval by an amount of delta
     * @param delta the amount of expansion
     * @return the expanded interval
     */
    public Interval expand(double delta) {
        double padding = delta / 2.0;
        return new Interval(min - padding, max + padding);
    }
}
