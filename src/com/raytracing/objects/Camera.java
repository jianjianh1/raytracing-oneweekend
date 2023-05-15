package com.raytracing.objects;

import com.raytracing.basis.Vector3d;

/**
 * Represents a camera in our raytracer
 */
public class Camera {
    private static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;
    private static final double DEFAULT_VIEWPORT_HEIGHT = 2.0;
    private static final double DEFAULT_VIEWPORT_WIDTH = DEFAULT_ASPECT_RATIO * DEFAULT_VIEWPORT_HEIGHT;
    private static final double DEFAULT_FOCAL_LENGTH = 1.0;
    private static final double DEFAULT_ORIGIN_X = 0;
    private static final double DEFAULT_ORIGIN_Y = 0;
    private static final double DEFAULT_ORIGIN_Z = 0;

    private final Vector3d origin;
    private final Vector3d bottomLeft;
    private final Vector3d horizontal;
    private final Vector3d vertical;

    /**
     * Initialize a camera with the default specifications.
     */
    public Camera() {
        origin = new Vector3d(DEFAULT_ORIGIN_X, DEFAULT_ORIGIN_Y, DEFAULT_ORIGIN_Z);
        horizontal = new Vector3d(DEFAULT_VIEWPORT_WIDTH, DEFAULT_ORIGIN_Y, DEFAULT_ORIGIN_Z);
        vertical = new Vector3d(DEFAULT_ORIGIN_X, DEFAULT_VIEWPORT_HEIGHT, DEFAULT_ORIGIN_Z);
        bottomLeft = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5))
                .subtract(new Vector3d(DEFAULT_ORIGIN_X, DEFAULT_ORIGIN_Y, DEFAULT_FOCAL_LENGTH));
    }

    /**
     * Returns the ray that shoots to (u, v).
     *
     * @param u the scale of the horizontal axis
     * @param v the scale of the vertical axis
     * @return the ray that shoots to (u, v)
     */
    public Ray getRay(double u, double v) {
        Vector3d point = bottomLeft.add(horizontal.scale(u)).add(vertical.scale(v));
        return Ray.between(origin, point);
    }
}
