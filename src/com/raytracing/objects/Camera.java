package com.raytracing.objects;

import com.raytracing.base.Vector3d;

/**
 * Represents a camera in our raytracer
 */
public class Camera {
    private final Vector3d origin;
    private final Vector3d bottomLeft;
    private final Vector3d horizontal;
    private final Vector3d vertical;

    /**
     * Initialize a camera with the default specifications.
     */
    public Camera(Vector3d lookFrom, Vector3d lookAt, Vector3d viewUp, double verticalFOV, double aspectRatio) {
        double theta = Math.toRadians(verticalFOV);
        double h = Math.tan(theta / 2);
        double viewportHeight = 2.0 * h;
        double viewportWidth = aspectRatio * viewportHeight;

        Vector3d focus = lookFrom.subtract(lookAt).normalize();
        Vector3d u = viewUp.cross(focus).normalize();
        Vector3d v = focus.cross(u).normalize();

        origin = lookFrom;
        horizontal = u.scale(viewportWidth);
        vertical = v.scale(viewportHeight);
        bottomLeft = origin.subtract(horizontal.scale(0.5)).subtract(vertical.scale(0.5)).subtract(focus);
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
