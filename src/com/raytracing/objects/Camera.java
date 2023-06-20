package com.raytracing.objects;

import com.raytracing.base.Vector3d;

/**
 * Represents a camera in our raytracer
 */
public class Camera {
    private static final double DEFAULT_FOCAL_LENGTH = 1.0;

    private final Vector3d origin;
    private final Vector3d bottomLeft;
    private final Vector3d horizontal;
    private final Vector3d vertical;

    /**
     * Initialize a camera with the default specifications.
     */
    public Camera(double verticalFOV, double aspectRatio) {
        double theta = Math.toRadians(verticalFOV);
        double h = Math.tan(theta / 2);
        double viewportHeight = 2.0 * h;
        double viewportWidth = aspectRatio * viewportHeight;
        origin = new Vector3d(0, 0, 0);
        horizontal = new Vector3d(viewportWidth, 0, 0);
        vertical = new Vector3d(0, viewportHeight, 0);
        bottomLeft = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5))
                .subtract(new Vector3d(0, 0, DEFAULT_FOCAL_LENGTH));
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
