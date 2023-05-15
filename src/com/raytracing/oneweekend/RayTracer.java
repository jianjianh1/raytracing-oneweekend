package com.raytracing.oneweekend;

import com.raytracing.basis.PixelColor;
import com.raytracing.basis.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.objects.Camera;
import com.raytracing.objects.HittableList;
import com.raytracing.objects.Ray;
import com.raytracing.objects.Sphere;
import com.raytracing.utils.ProgressBar;
import com.raytracing.utils.Canvas;

import java.io.IOException;

public class RayTracer {
    private static final int IMAGE_WIDTH = 1920;
    private static final int IMAGE_HEIGHT = 1080;

    private static final double INFINITY = Double.MAX_VALUE;
    private static final double HIT_THRESHOLD = 1E-3;
    private static final int SAMPLES_PER_PIXEL = 100;
    private static final int MAX_DEPTH = 50;

    private static final Camera camera = new Camera();
    private static final HittableList world = new HittableList();

    public static void main(String[] args) throws IOException {
        initializeWorld();

        ProgressBar progressBar = new ProgressBar(IMAGE_WIDTH * IMAGE_HEIGHT);

        try (Canvas canvas = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT)) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                for (int y = 0; y < IMAGE_HEIGHT; y++) {
                    PixelColor pixel = new PixelColor();
                    for (int i = 0; i < SAMPLES_PER_PIXEL; i++) {
                        double u = (x + Math.random()) / (IMAGE_WIDTH - 1);
                        double v = (y + Math.random()) / (IMAGE_HEIGHT - 1);
                        // ray start from origin and hit at u portion of width and v portion of height
                        Ray ray = camera.getRay(u, v);
                        pixel.addSample(rayColor(ray, MAX_DEPTH));
                    }
                    canvas.fillPixel(x, y, pixel.color());

                    progressBar.step();
                    progressBar.show();
                }
            }
            canvas.save("reflection.png");
        }
    }

    private static PixelColor rayColor(Ray ray, int depth) {
        if (depth <= 0) {
            return PixelColor.BLACK;
        }

        Hittable.HitRecord record = world.hit(ray, HIT_THRESHOLD, INFINITY);
        if (record != null) {
            Vector3d surfaceNormal = record.normal();
            Vector3d reflection = surfaceNormal.add(Vector3d.randomUnit());
            // brightness is halved by each reflection
            return rayColor(new Ray(record.point(), reflection), depth - 1).scale(0.5);
        }

        // no hit
        Vector3d unitDirection = ray.unitDirection();
        float s = (float) (0.5 * (unitDirection.y() + 1.0)); // [-1.0, 1.0]

        // blend white (1.0F, 1.0F, 1.0F) and sky blue (0.5F, 0.7F, 1.0F)
        return PixelColor.WHITE.scale(1 - s).add(PixelColor.SKY_BLUE.scale(s));
    }

    private static void initializeWorld() {
        world.add(new Sphere(new Vector3d(0, 0, -1), 0.5));
        world.add(new Sphere(new Vector3d(0, -100.5, -1), 100));
    }
}
