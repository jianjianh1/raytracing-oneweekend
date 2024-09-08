package com.raytracing.textures;

import com.raytracing.base.Vector3d;

import java.util.Random;

public class Perlin {
    private static final int pointCount = 256;
    private static final Random rng = new Random(42);
    private final Vector3d[] randVec = new Vector3d[pointCount];
    private final int[] permX = new int[pointCount];
    private final int[] permY = new int[pointCount];
    private final int[] permZ = new int[pointCount];

    /**
     * Constructs necessary arrays for generating perlin noise
     */
    public Perlin() {
        for (int i = 0; i < randVec.length; i++) randVec[i] = Vector3d.randomUnitUniform();

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

        Vector3d[][][] c = new Vector3d[2][2][2];
        for (int di = 0; di < 2; di++) {
            for (int dj = 0; dj < 2; dj++) {
                for (int dk = 0; dk < 2; dk++) {
                    c[di][dj][dk] = randVec[permX[(i + di) & 255] ^ permY[(j + dj) & 255] ^ permZ[(k + dk) & 255]];
                }
            }
        }

        return perlinInterp(c, u, v, w);
    }

    /**
     * Turbulence, a sum of repeated calls to noise
     */
    public double turbulence(Vector3d p, int depth) {
        double result = 0.0;
        double weight = 1.0;
        for (int i = 0; i < depth; i++) {
            result += weight * noise(p);
            weight *= 0.5;
            p = p.scale(2);
        }
        return Math.abs(result);
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

    /**
     * Interpolates on the dot product of the difference vector and vector on lattice point
     */
    private static double perlinInterp(Vector3d[][][] c, double u, double v, double w) {
        double uu = u * u * (3.0 - 2.0 * u);
        double vv = v * v * (3.0 - 2.0 * v);
        double ww = w * w * (3.0 - 2.0 * w);

        double result = 0.0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    Vector3d diff = new Vector3d(u - i, v - j, w - k);
                    result += (i * uu + (1 - i) * (1 - uu))
                            * (j * vv + (1 - j) * (1 - vv))
                            * (k * ww + (1 - k) * (1 - ww))
                            * c[i][j][k].dot(diff);
                }
            }
        }
        return result;
    }
}
