package com.raytracing.oneweekend;

import com.raytracing.basis.Vector3d;
import com.raytracing.objects.Ray;
import com.raytracing.utils.ProgressBar;
import com.raytracing.basis.Canvas;

import java.awt.*;
import java.io.IOException;

public class RayTracer {
    private static final double ASPECT_RATIO = 16.0 / 9.0;
    // Image
    private static final int IMAGE_HEIGHT = 1080;
    private static final int IMAGE_WIDTH = (int) (IMAGE_HEIGHT * ASPECT_RATIO);

    // Camera
    private static final double VIEWPORT_HEIGHT = 2.0;
    private static final double VIEWPORT_WIDTH = VIEWPORT_HEIGHT * ASPECT_RATIO;
    private static final double FOCAL_LENGTH = 1.0;
    private static final Vector3d ORIGIN = new Vector3d();
    private static final Vector3d HORIZONTAL = new Vector3d(VIEWPORT_WIDTH, 0, 0);
    private static final Vector3d VERTICAL = new Vector3d(0, VIEWPORT_HEIGHT, 0);
    private static final Vector3d LOWER_LEFT_CORNER =
            ORIGIN.subtract(HORIZONTAL.scale(0.5)).subtract(VERTICAL.scale(0.5)).subtract(new Vector3d(0, 0, FOCAL_LENGTH));

    public static void main(String[] args) throws IOException {
        ProgressBar progressBar = new ProgressBar(IMAGE_WIDTH * IMAGE_HEIGHT);

        try (Canvas canvas = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT)) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                for (int y = 0; y < IMAGE_HEIGHT; y++) {
                    double u = (double) x / (IMAGE_WIDTH - 1);
                    double v = (double) y / (IMAGE_HEIGHT - 1);

                    // ray start from origin and hit at u portion of width and v portion of height
                    Ray ray = new Ray(ORIGIN,
                            LOWER_LEFT_CORNER.add(HORIZONTAL.scale(u)).add(VERTICAL.scale(v)).subtract(ORIGIN));

                    canvas.fillPixel(x, y, rayColor(ray));

                    progressBar.step();
                    progressBar.show();
                }
            }
            canvas.save("gradient.png");
        }
    }

    private static Color rayColor(Ray ray) {
        Vector3d unitDirection = ray.direction().normalize();
        float t = (float) (0.5 * (unitDirection.y() + 1.0)); // [-1.0, 1.0]

        // blend white (1.0F, 1.0F, 1.0F) and blue (0.5F, 0.7F, 1.0F)
        return new Color(
                1F - t + 0.5F * t,
                1F - t + 0.7F * t,
                1F - t + t
        );
    }
}
