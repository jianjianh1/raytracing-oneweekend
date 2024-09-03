package com.raytracing.app;

import com.raytracing.base.Interval;
import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.interfaces.Texture;
import com.raytracing.materials.Dielectric;
import com.raytracing.materials.DiffuseLight;
import com.raytracing.materials.Lambertian;
import com.raytracing.materials.Metal;
import com.raytracing.scene.*;
import com.raytracing.structures.BVHNode;
import com.raytracing.textures.CheckerTexture;
import com.raytracing.textures.ImageTexture;
import com.raytracing.textures.NoiseTexture;
import com.raytracing.transform.RotateY;
import com.raytracing.transform.Translate;
import com.raytracing.utils.ProgressBar;
import com.raytracing.utils.Canvas;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RayTracer {
    private static final Random rng = new Random(42);
    private static final double EPSILON = 1E-3;

    private static double aspectRatio = 4.0 / 3.0;
    private static int imageWidth = 400;
    private static int samplesPerPixel = 100;
    private static int maxDepth = 50;

    private static PixelColor background = new PixelColor(0.7, 0.8, 1.0);
    private static Camera camera;
    private static HittableList world = new HittableList();

    public static void main(String[] args) throws IOException {
        switch (7) {
            case 1 -> boundingSpheres();
            case 2 -> checkeredSpheres();
            case 3 -> earth();
            case 4 -> perlinSpheres();
            case 5 -> quads();
            case 6 -> sampleLight();
            case 7 -> cornellBox();
        }

        int imageHeight = (int) (imageWidth / aspectRatio);
        ProgressBar progressBar = new ProgressBar(imageWidth * imageHeight);

        try (Canvas canvas = new Canvas(imageWidth, imageHeight)) {
            for (int x = 0; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    PixelColor pixel = new PixelColor();
                    for (int i = 0; i < samplesPerPixel; i++) {
                        double u = (x + Math.random()) / (imageWidth - 1);
                        double v = (y + Math.random()) / (imageHeight - 1);
                        // ray start from origin and hit at u portion of width and v portion of height
                        Ray ray = camera.getRay(u, v);
                        pixel.addSample(rayColor(ray, maxDepth));
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
        if (hit == null) return background;

        PixelColor colorFromEmission = hit.material().emitted(hit.u(), hit.v(), hit.point());

        Material.ScatterRecord scatter = hit.material().scatter(hit);
        if (scatter == null) {
            return colorFromEmission;
        }
        PixelColor colorFromScatter = rayColor(scatter.scatteredRay(), depth - 1).dot(scatter.attenuation());

        return colorFromEmission.add(colorFromScatter);
    }

    private static void cornellBox() {
        var red = new Lambertian(new PixelColor(0.65, 0.05, 0.05));
        var white = new Lambertian(new PixelColor(0.73, 0.73, 0.73));
        var green = new Lambertian(new PixelColor(0.12, 0.45, 0.15));
        var light = new DiffuseLight(new PixelColor(15, 15, 15));

        world.add(new Quad(new Vector3d(555, 0, 0), new Vector3d(0, 555, 0), new Vector3d(0, 0, 555), green));
        world.add(new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 555, 0), new Vector3d(0, 0, 555), red));
        world.add(new Quad(new Vector3d(343, 554, 332), new Vector3d(-130, 0, 0), new Vector3d(0, 0, -105), light));
        world.add(new Quad(new Vector3d(0, 0, 0), new Vector3d(555, 0, 0), new Vector3d(0, 0, 555), white));
        world.add(new Quad(new Vector3d(555, 555, 555), new Vector3d(-555, 0, 0), new Vector3d(0, 0, -555), white));
        world.add(new Quad(new Vector3d(0,0, 555), new Vector3d(555, 0, 0), new Vector3d(0, 555, 0), white));

        Hittable box1 = new Box(new Vector3d(), new Vector3d(165, 330, 165), white);
        box1 = new RotateY(box1, 15);
        box1 = new Translate(box1, new Vector3d(265, 0, 295));
        world.add(box1);

        Hittable box2 = new Box(new Vector3d(), new Vector3d(165, 165, 165), white);
        box2 = new RotateY(box2, -18);
        box2 = new Translate(box2, new Vector3d(130, 0, 65));
        world.add(box2);

        aspectRatio = 1.0;
        imageWidth = 600;
        samplesPerPixel = 200;
        maxDepth = 50;
        background = PixelColor.BLACK;

        double vFov = 40;
        var lookFrom = new Vector3d(278, 278, -800);
        var lookAt = new Vector3d(278, 278, 0);
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void sampleLight() {
        NoiseTexture perlin = new NoiseTexture(4.0);
        world.add(new Sphere(new Vector3d(0, -1000, 0), 1000, new Lambertian(perlin)));
        world.add(new Sphere(new Vector3d(0, 2, 0), 2, new Lambertian(perlin)));

        DiffuseLight diffuseLight = new DiffuseLight(new PixelColor(4, 4, 4));
        world.add(new Sphere(new Vector3d(0, 7, 0), 2, diffuseLight));
        world.add(new Quad(new Vector3d(3, 1, -2), new Vector3d(2, 0, 0), new Vector3d(0, 2, 0), diffuseLight));

        aspectRatio = 16.0 / 9.0;
        imageWidth = 400;
        samplesPerPixel = 100;
        maxDepth = 50;
        background = PixelColor.BLACK;

        double vFov = 20;
        var lookFrom = new Vector3d(26, 3, 6);
        var lookAt = new Vector3d(0, 2, 0);
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void quads() {
        var leftRed = new Lambertian(new PixelColor(1.0, 0.2, 0.2));
        var backGreen = new Lambertian(new PixelColor(0.2, 1.0, 0.2));
        var rightBlue = new Lambertian(new PixelColor(0.2, 0.2, 1.0));
        var upperOrange = new Lambertian(new PixelColor(1.0, 0.5, 0.0));
        var lowerTeal = new Lambertian(new PixelColor(0.2, 0.8, 0.8));

        world.add(new Quad(new Vector3d(-3, -2, 5), new Vector3d(0, 0, -4), new Vector3d(0, 4, 0), leftRed));
        world.add(new Quad(new Vector3d(-2, -2, 0), new Vector3d(4, 0, 0), new Vector3d(0, 4, 0), backGreen));
        world.add(new Quad(new Vector3d(3, -2, 1), new Vector3d(0, 0, 4), new Vector3d(0, 4, 0), rightBlue));
        world.add(new Quad(new Vector3d(-2, 3, 1), new Vector3d(4, 0, 0), new Vector3d(0, 0, 4), upperOrange));
        world.add(new Quad(new Vector3d(-2, -3, 5), new Vector3d(4, 0, 0), new Vector3d(0, 0, -4), lowerTeal));

        aspectRatio = 1.0;
        imageWidth = 400;
        samplesPerPixel = 100;
        maxDepth = 50;

        double vFov = 80;
        var lookFrom = new Vector3d(0, 0, 9);
        var lookAt = new Vector3d();
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void perlinSpheres() {
        NoiseTexture perlin = new NoiseTexture(4.0);
        world.add(new Sphere(new Vector3d(0, -1000, 0), 1000, new Lambertian(perlin)));
        world.add(new Sphere(new Vector3d(0, 2, 0), 2, new Lambertian(perlin)));

        aspectRatio = 16.0 / 9.0;
        imageWidth = 400;
        samplesPerPixel = 100;
        maxDepth = 50;

        double vFov = 20;
        var lookFrom = new Vector3d(13, 2, 3);
        var lookAt = new Vector3d();
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void earth() throws IOException {
        String texturePath = Paths.get(System.getProperty("user.dir"), "assets", "earthmap.jpg").toString();
        ImageTexture earthTexture = new ImageTexture(texturePath);
        Sphere globe = new Sphere(new Vector3d(), 2, new Lambertian(earthTexture));
        world.add(globe);

        aspectRatio = 16.0 / 9.0;
        imageWidth = 400;
        samplesPerPixel = 100;
        maxDepth = 50;

        double vFov = 20;
        var lookFrom = new Vector3d(0, 0, 12);
        var lookAt = new Vector3d();
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void checkeredSpheres() {
        Texture checker = new CheckerTexture(0.32, new PixelColor(0.2, 0.3, 0.1), new PixelColor(0.9, 0.9, 0.9));

        world.add(new Sphere(new Vector3d(0, -10, 0), 10, new Lambertian(checker)));
        world.add(new Sphere(new Vector3d(0, 10, 0), 10, new Lambertian(checker)));

        aspectRatio = 16.0 / 9.0;
        imageWidth = 400;
        samplesPerPixel = 100;
        maxDepth = 50;

        double vFov = 20;
        var lookFrom = new Vector3d(13, 2, 3);
        var lookAt = new Vector3d();
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void boundingSpheres() {
        aspectRatio = 3.0 / 2.0;
        imageWidth = 400;
        samplesPerPixel = 100;

        var lookFrom = new Vector3d(13, 2, 3);
        var lookAt = new Vector3d(0, 0, 0);
        var viewUp = new Vector3d(0, 1, 0);
        double distToFocus = 10;
        double aperture = 0.1;
        camera = new Camera(lookFrom, lookAt, viewUp, 20, aspectRatio, aperture, distToFocus);

        Texture checker = new CheckerTexture(0.32, new PixelColor(0.2, 0.3, 0.1), new PixelColor(0.9, 0.9, 0.9));
        Material groundMaterial = new Lambertian(checker);
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
