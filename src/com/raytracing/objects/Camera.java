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
     * Initialize a camera with the default specifications.
     */
    public Camera(Vector3d lookFrom, Vector3d lookAt, Vector3d viewUp, double verticalFOV, double aspectRatio,
                  double aperture, double focusDist) {
        lensRadius = aperture / 2;

        double theta = Math.toRadians(verticalFOV);
        double viewportHeight = 2.0 * (Math.tan(theta / 2) * focusDist);
        double viewportWidth = aspectRatio * viewportHeight;

        Vector3d focus = lookFrom.subtract(lookAt).normalize().scale(focusDist);
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
        double randomAngle = 2 * Math.PI * Math.random();
        Vector3d randomDirection = horizontal.normalize().scale(Math.cos(randomAngle))
                .add(vertical.normalize().scale(Math.sin(randomAngle)));
        Vector3d offset = randomDirection.scale(lensRadius);

        Vector3d point = bottomLeft.add(horizontal.scale(u)).add(vertical.scale(v));
        return Ray.between(origin.add(offset), point);
    }
}
