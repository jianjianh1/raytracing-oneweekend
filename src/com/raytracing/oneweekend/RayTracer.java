package com.raytracing.oneweekend;

import com.raytracing.utils.ProgressBar;
import com.raytracing.utils.Image;

import java.awt.*;
import java.io.IOException;

public class RayTracer {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    public static void main(String[] args) throws IOException {
        ProgressBar progressBar = new ProgressBar(WIDTH * HEIGHT);

        Image image = new Image(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                float r = (float) x / (WIDTH - 1);
                float g = (float) (WIDTH - 1 - y) / (WIDTH - 1);
                float b = 0.25F;

                image.fillPixel(x, y, new Color(r, g, b));

                progressBar.progress();
                System.out.print(progressBar);
            }
        }

        image.save("sample.png");
        image.close();
    }
}
