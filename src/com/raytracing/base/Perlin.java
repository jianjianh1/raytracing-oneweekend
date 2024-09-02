package com.raytracing.base;

import com.raytracing.base.Vector3d;

import java.util.Random;

public class Perlin {
    private static final int pointCount = 256;
    private static final Random rng = new Random();
    private final double[] randFloat = new double[pointCount];
    private final int[] permX = new int[pointCount];
    private final int[] permY = new int[pointCount];
    private final int[] permZ = new int[pointCount];

    /**
     * Constructs necessary arrays for generating perlin noise
     */
    public Perlin() {
        for (int i = 0; i < randFloat.length; i++) randFloat[i] = rng.nextDouble();

        perlinGeneratePerm(permX);
        perlinGeneratePerm(permY);
        perlinGeneratePerm(permZ);
    }

    /**
     * Generates perlin noise by a 3D vector as the seed
     */
    public double noise(Vector3d p) {
        double u = p.x() - Math.floor(p.x());
        double v = p.y() - Math.floor(p.y());
        double w = p.z() - Math.floor(p.z());

        int i = (int)Math.floor(p.x());
        int j = (int)Math.floor(p.y());
        int k = (int)Math.floor(p.z());

        double[][][] c = new double[2][2][2];
        for (int di = 0; di < 2; di++) {
            for (int dj = 0; dj < 2; dj++) {
                for (int dk = 0; dk < 2; dk++) {
                    c[di][dj][dk] = randFloat[permX[(i + di) & 255] ^ permY[(j + dj) & 255] ^ permZ[(k + dk) & 255]];
                }
            }
        }

        return trilinearInterp(c, u, v, w);
    }

    /**
     * Permute an array by swapping elements randomly
     */
    private static void permute(int[] p) {
        for (int i = p.length - 1; i > 0; i--) {
            int target = rng.nextInt(i);
            // swap i and target
            int tmp = p[i];
            p[i] = p[target];
            p[target] = tmp;
        }
    }

    /**
     * Generate an array that's randomly permuted
     */
    private static void perlinGeneratePerm(int[] p) {
        for (int i = 0; i < p.length; i++) p[i] = i;

        permute(p);
    }

    /**
     * Trilinear interpolation on a 2x2x2 cube
     */
    private static double trilinearInterp(double[][][] c, double u, double v, double w) {
        double result = 0.0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    result += (i * u + (1 - i) * (1 - u)) * (j * v + (1 - j) * (1 - v)) * (k * w + (1 - k) * (1 - w))
                            * c[i][j][k];
                }
            }
        }
        return result;
    }
}
