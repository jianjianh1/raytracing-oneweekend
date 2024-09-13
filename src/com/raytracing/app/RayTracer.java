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
import com.raytracing.pdf.HittablePdf;
import com.raytracing.pdf.MixturePdf;
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
    private static final HittableList lights = new HittableList();

    public static void main(String[] args) throws IOException {
        switch (7) {
            case 1 -> boundingSpheres();
            case 2 -> checkeredSpheres();
            case 3 -> earth();
            case 4 -> perlinSpheres();
            case 5 -> quads();
            case 6 -> sampleLight();
            case 7 -> cornellBox();
            case 8 -> cornellSmoke();
            case 9 -> finalScene(800, 5_000, 20);
            default -> finalScene(400, 250, 4);
        }

        // square root of samples per pixel
        int sqrtSpp = (int) (Math.sqrt(samplesPerPixel));
        double sqrtSppReciprocal = 1.0 / sqrtSpp;

        int imageHeight = (int) (imageWidth / aspectRatio);
        ProgressBar progressBar = new ProgressBar(imageWidth * imageHeight);

        try (Canvas canvas = new Canvas(imageWidth, imageHeight)) {
            for (int x = 0; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    PixelColor pixel = new PixelColor();
                    for (int si = 0; si < sqrtSpp; si++) {
                        for (int sj = 0; sj < sqrtSpp; sj++) {
                            double px = (si + rng.nextDouble()) * sqrtSppReciprocal - 0.5;
                            double py = (sj + rng.nextDouble()) * sqrtSppReciprocal - 0.5;
                            double u = (x + px) / (imageWidth - 1);
                            double v = (y + py) / (imageHeight - 1);
                            // ray start from Q and hit at u portion of width and v portion of height
                            Ray ray = camera.getRay(u, v);
                            pixel.addSample(rayColor(ray, maxDepth));
                        }
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

        PixelColor colorFromEmission = hit.material().emitted(hit, hit.u(), hit.v(), hit.point());

        Material.ScatterRecord scatter = hit.material().scatter(hit);
        if (scatter == null) {
            return colorFromEmission;
        }

        PixelColor colorFromScatter;
        if (scatter.pdf() == null) {
            return rayColor(scatter.scatteredRay(), depth - 1).dot(scatter.attenuation());
        } else {
            var lightPdf = new HittablePdf(lights, hit.point());
            var mixedPdf = new MixturePdf(scatter.pdf(), lightPdf);

            var scatteredRay = new Ray(hit.point(), mixedPdf.generate(), ray.time());
            var pdfValue = mixedPdf.value(scatteredRay.direction());
            // for debugging
            if (pdfValue == 0.0) {
                System.out.println(scatteredRay.direction());
            }

            double scatteringPdf = hit.material().scatteringPdf(hit, scatteredRay);

            colorFromScatter = rayColor(scatteredRay, depth - 1).dot(scatter.attenuation()).scale(scatteringPdf / pdfValue);
        }

        return colorFromEmission.add(colorFromScatter);
    }

    private static void finalScene(int width, int samples, int depth) throws IOException {
        // ground green boxes
        HittableList boxes1 = new HittableList();
        var ground = new Lambertian(new PixelColor(0.48, 0.83, 0.53));
        int boxesPerSide = 20;
        for (int i = 0; i < boxesPerSide; i++) {
            for (int j = 0; j < boxesPerSide; j++) {
                var w = 100.0;
                var x0 = -1000.0 + i * w;
                var z0 = -1000.0 + j * w;
                var y0 = 0.0;
                var x1 = x0 + w;
                var y1 = rng.nextDouble(1, 101);
                var z1 = z0 + w;

                boxes1.add(new Box(new Vector3d(x0, y0, z0), new Vector3d(x1, y1, z1), ground));
            }
        }
        world.add(new BVHNode(boxes1));

        // light
        var light = new DiffuseLight(new PixelColor(7, 7, 7));
        world.add(new Quad(new Vector3d(123, 554, 147), new Vector3d(300, 0, 0), new Vector3d(0, 0, 265), light));

        // brown moving sphere
        var center1 = new Vector3d(400, 400, 200);
        var center2 = center1.add(new Vector3d(30, 0, 0));
        world.add(new Sphere(center1, center2, 50, new Lambertian(new PixelColor(0.7, 0.3, 0.1))));

        // glass ball
        world.add(new Sphere(new Vector3d(260, 150, 45), 50, new Dielectric(1.5)));

        // diffuse metal ball
        world.add(new Sphere(new Vector3d(0, 150, 145), 50, new Metal(new PixelColor(0.8, 0.8, 0.9), 1.0)));

        // glass ball with blue volume content
        var boundary = new Sphere(new Vector3d(360, 150, 145), 70, new Dielectric(1.5));
        world.add(boundary);
        world.add(new ConstantMedium(boundary, 0.2, new PixelColor(0.2, 0.4, 0.9)));

        // the whole scene is contained in fog
        boundary = new Sphere(new Vector3d(), 5000, new Dielectric(1.5));
        world.add(new ConstantMedium(boundary, 1e-4, PixelColor.WHITE));

        // earth
        String texturePath = Paths.get(System.getProperty("user.dir"), "assets", "earthmap.jpg").toString();
        ImageTexture earthTexture = new ImageTexture(texturePath);
        world.add(new Sphere(new Vector3d(400, 200, 400), 100, new Lambertian(earthTexture)));

        // perlin ball
        var perlinTexture = new NoiseTexture(0.2); // low frequency
        world.add(new Sphere(new Vector3d(220, 280, 300), 80, new Lambertian(perlinTexture)));

        // cluster of white balls
        HittableList boxes2 = new HittableList();
        var white = new Lambertian(new PixelColor(0.73, 0.73, 0.73));
        int ns = 1000; // number of balls
        for (int j = 0; j < ns; j++) {
            boxes2.add(
                    new Sphere(
                            new Vector3d(rng.nextDouble(165), rng.nextDouble(165), rng.nextDouble(165)),
                            10,
                            white
                    )
            );
        }
        world.add(new Translate(
                new RotateY(new BVHNode(boxes2), 15),
                new Vector3d(-100, 270, 395)
        ));

        aspectRatio = 1.0;
        imageWidth = width;
        samplesPerPixel = samples;
        maxDepth = depth;
        background = PixelColor.BLACK;

        double vFov = 40;
        var lookFrom = new Vector3d(478, 278, -600);
        var lookAt = new Vector3d(278, 278, 0);
        var viewUp = new Vector3d(0, 1, 0);

        camera = new Camera(lookFrom, lookAt, viewUp, vFov, aspectRatio);
    }

    private static void cornellSmoke() {
        var red = new Lambertian(new PixelColor(0.65, 0.05, 0.05));
        var white = new Lambertian(new PixelColor(0.73, 0.73, 0.73));
        var green = new Lambertian(new PixelColor(0.12, 0.45, 0.15));
        var light = new DiffuseLight(new PixelColor(7, 7, 7));

        world.add(new Quad(new Vector3d(555, 0, 0), new Vector3d(0, 555, 0), new Vector3d(0, 0, 555), green));
        world.add(new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 555, 0), new Vector3d(0, 0, 555), red));
        world.add(new Quad(new Vector3d(113, 554, 127), new Vector3d(330, 0, 0), new Vector3d(0, 0, 305), light));
        world.add(new Quad(new Vector3d(0, 0, 0), new Vector3d(555, 0, 0), new Vector3d(0, 0, 555), white));
        world.add(new Quad(new Vector3d(555, 555, 555), new Vector3d(-555, 0, 0), new Vector3d(0, 0, -555), white));
        world.add(new Quad(new Vector3d(0,0, 555), new Vector3d(555, 0, 0), new Vector3d(0, 555, 0), white));

        Hittable box1 = new Box(new Vector3d(), new Vector3d(165, 330, 165), white);
        box1 = new RotateY(box1, 15);
        box1 = new Translate(box1, new Vector3d(265, 0, 295));
        world.add(new ConstantMedium(box1, 0.01, PixelColor.BLACK));

        Hittable box2 = new Box(new Vector3d(), new Vector3d(165, 165, 165), white);
        box2 = new RotateY(box2, -18);
        box2 = new Translate(box2, new Vector3d(130, 0, 65));
        world.add(new ConstantMedium(box2, 0.01, PixelColor.WHITE));

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

        var glass = new Dielectric(1.5);
        world.add(new Sphere(new Vector3d(190, 90, 190), 90, glass));

        lights.add(new Quad(new Vector3d(343, 554, 332), new Vector3d(-130, 0, 0), new Vector3d(0, 0, -105), null));
        lights.add(new Sphere(new Vector3d(190, 90, 190), 90, null));

        aspectRatio = 1.0;
        imageWidth = 600;
        samplesPerPixel = 1000;
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
