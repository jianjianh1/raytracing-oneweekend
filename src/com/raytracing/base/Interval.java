package com.raytracing.base;

public record Interval(double min, double max) {
    public static final Interval EMPTY = new Interval();
    public static final Interval UNIVERSE = new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Interval UNIT = new Interval(0.0, 1.0);

    /**
     * Constructs an empty interval
     */
    public Interval() {
        this(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    /**
     * Constructs an interval that covers two intervals
     */
    public Interval(Interval a, Interval b) {
        this(
                Math.min(a.min, b.min),
                Math.max(a.max, b.max)
        );
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

    /**
     * Translates the interval by a given amount
     */
    public Interval translate(double displacement) {
        return new Interval(min + displacement, max + displacement);
    }
}
