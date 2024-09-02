package com.raytracing.oneweekend;

import com.raytracing.base.Interval;
import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.objects.*;
import com.raytracing.utils.ProgressBar;
import com.raytracing.utils.Canvas;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RayTracer {
    private static final double ASPECT_RATIO = 3.0 / 2.0;
    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = (int) (IMAGE_WIDTH / ASPECT_RATIO);

    private static final double EPSILON = 1E-3;
    private static final int SAMPLES_PER_PIXEL = 100;
    private static final int MAX_DEPTH = 50;

    private static Camera camera;
    private static HittableList world = new HittableList();
    private static final Random rng = new Random(42);

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
            // get timestamp to name the output image
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            canvas.save(Paths.get(System.getProperty("user.dir"), "outputs", now.format(formatter) + ".png").toString());
        }
    }

    private static PixelColor rayColor(Ray ray, int depth) {
        if (depth <= 0) {
            return PixelColor.BLACK;
        }

        Hittable.HitRecord hit = world.hit(ray, new Interval(EPSILON, Double.POSITIVE_INFINITY));
        if (hit != null) {
            Material.ScatterRecord scatter = hit.material().scatter(hit);
            if (scatter == null) {
                return PixelColor.BLACK;
            }
            return rayColor(scatter.scatteredRay(), depth - 1).dot(scatter.attenuation());
        }

        // no hit
        Vector3d unitDirection = ray.unitDirection();
        float s = (float) (0.5 * (unitDirection.y() + 1.0)); // [-1.0, 1.0]

        // blend white (1.0F, 1.0F, 1.0F) and sky blue (0.5F, 0.7F, 1.0F)
        return PixelColor.WHITE.scale(1 - s).add(PixelColor.SKY_BLUE.scale(s));
    }

    private static void initializeWorld() {
        var lookFrom = new Vector3d(13, 2, 3);
        var lookAt = new Vector3d(0, 0, 0);
        var viewUp = new Vector3d(0, 1, 0);
        double distToFocus = 10;
        double aperture = 0.1;
        camera = new Camera(lookFrom, lookAt, viewUp, 20, ASPECT_RATIO, aperture, distToFocus);

        Material groundMaterial = new Lambertian(new PixelColor(0.5, 0.5, 0.5));
        world.add(new Sphere(new Vector3d(0, -1000, 0), 1000, groundMaterial));

        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMaterial = rng.nextDouble();
                var center = new Vector3d(a + rng.nextDouble(0.9), 0.2, b + rng.nextDouble(0.9));

                if (center.subtract(new Vector3d(4, 0.2, 0)).length() > 0.9) {
                    Material sphereMaterial;

                    if (chooseMaterial < 0.8) {
                        // diffuse
                        var albedo = new PixelColor(
                                rng.nextDouble() * rng.nextDouble(),
                                rng.nextDouble() * rng.nextDouble(),
                                rng.nextDouble() * rng.nextDouble()
                        );
                        sphereMaterial = new Lambertian(albedo);
                        var center2 = center.add(new Vector3d(0.0, rng.nextDouble(0.5), 0.0));
                        world.add(new Sphere(center, center2, 0.2, sphereMaterial));
                    } else if (chooseMaterial < 0.95) {
                        // metal
                        var albedo = new PixelColor(
                                rng.nextDouble(0.5, 1),
                                rng.nextDouble(0.5, 1),
                                rng.nextDouble(0.5, 1)
                        );
                        var fuzz = rng.nextDouble(0.5);
                        sphereMaterial = new Metal(albedo, fuzz);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    } else {
                        // glass
                        sphereMaterial = new Dielectric(1.5);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    }
                }
            }
        }

        var material1 = new Dielectric(1.5);
        world.add(new Sphere(new Vector3d(0, 1, 0), 1, material1));

        var material2 = new Lambertian(new PixelColor(0.4, 0.2, 0.1));
        world.add(new Sphere(new Vector3d(-4, 1, 0), 1, material2));

        var material3 = new Metal(new PixelColor(0.7, 0.6, 0.5), 0.0);
        world.add(new Sphere(new Vector3d(4, 1, 0), 1, material3));

        world = new HittableList(new BVHNode(world));
    }
}
