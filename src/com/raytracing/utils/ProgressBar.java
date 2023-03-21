package com.raytracing.utils;

/**
 * Represents a progress bar that can be printed in the console
 */
public class ProgressBar {
    private static final int MAX_DISPLAY_LENGTH = 50;
    private static final char PROGRESS_CHAR = '#';

    private final double maxProgress;
    private double progress;
    private int displayLength;
    private final StringBuilder head;
    private final StringBuilder tail;

    /**
     * Constructs a progress bar with 0 progress
     *
     * @param maxProgress the specified maximum value of progress
     */
    public ProgressBar(double maxProgress) {
        this.maxProgress = maxProgress;
        progress = 0;
        displayLength = 0;

        head = new StringBuilder();

        tail = new StringBuilder();
        tail.append(" ".repeat(MAX_DISPLAY_LENGTH));
    }

    /**
     * Adds progress by the given amount
     *
     * @param increment the amount of progress
     */
    public void step(double increment) {
        progress += increment;
        if (progress > maxProgress) {
            return;
        }
        int displayIncrement = (int) (progress / maxProgress * MAX_DISPLAY_LENGTH) - displayLength;
        for (int i = 0; i < displayIncrement; i++) {
            head.append(PROGRESS_CHAR);
            tail.setLength(tail.length() - 1);
        }
        displayLength += displayIncrement;
    }

    /**
     * Adds progress by one
     */
    public void step() {
        step(1);
    }

    public void show() {
        System.out.print(toString() + '\r');
    }

    /**
     * @return the string representation of the progress bar
     */
    @Override
    public String toString() {
        return "Progress: [" + head.toString() + tail.toString() + ']';
    }
}
