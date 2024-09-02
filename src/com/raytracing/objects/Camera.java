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
    private final double lensRadius;

    /**
     * Constructs a camera with default depth of field
     */
    public Camera(Vector3d lookFrom, Vector3d lookAt, Vector3d viewUp, double verticalFOV, double aspectRatio) {
        this(lookFrom, lookAt, viewUp, verticalFOV, aspectRatio, 0.0, 10.0);
    }
    /**
     * Initialize a camera with the default specifications.
     */
    public Camera(Vector3d lookFrom, Vector3d lookAt, Vector3d viewUp, double verticalFOV, double aspectRatio,
                  double aperture, double focusDist) {
        lensRadius = aperture / 2;

        double theta = Math.toRadians(verticalFOV);
        double viewportHeight = 2.0 * (Math.tan(theta / 2) * focusDist);
        double viewportWidth = aspectRatio * viewportHeight;

        Vector3d focus = lookFrom.subtract(lookAt).normalized().scale(focusDist);
        Vector3d u = viewUp.cross(focus).normalized();
        Vector3d v = focus.cross(u).normalized();

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
        Vector3d offset = horizontal.normalized().scale(2.0 * Math.random() - 1.0)
                .add(vertical.normalized().scale(2.0 * Math.random() - 1.0));
        while (offset.length() > 1.0) {
            offset = horizontal.normalized().scale(2.0 * Math.random() - 1.0)
                    .add(vertical.normalized().scale(2.0 * Math.random() - 1.0));
        }
        offset = offset.scale(lensRadius);
        Vector3d point = bottomLeft.add(horizontal.scale(u)).add(vertical.scale(v));
        Vector3d rayOrigin = origin.add(offset);
        Vector3d rayDirection = point.subtract(rayOrigin);
        double rayTime = Math.random();

        return new Ray(rayOrigin, rayDirection, rayTime);
    }
}
