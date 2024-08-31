package com.raytracing.objects;

import com.raytracing.base.Vector3d;

/**
 * Represents rays in 3D scenes
 *
 * @param origin    the origin
 * @param direction the direction
 */
public record Ray(Vector3d origin, Vector3d direction, double time) {
    /**
     * Construct a ray that shoots at time 0.0
     * @param origin the origin of ray
     * @param direction the direction of ray
     */
    public Ray(Vector3d origin, Vector3d direction) {
        this(origin, direction, 0.0);
    }

    /**
     * Returns the position of the ray's end point at t
     *
     * @param t a scale or time
     * @return the position of the ray's end point
     */
    public Vector3d at(double t) {
        return origin.add(direction.scale(t));
    }

    /**
     * Returns the normalized direction
     *
     * @return the normalized direction
     */
    public Vector3d unitDirection() {
        return direction.normalized();
    }

    /**
     * Returns a ray between the starting point and the end point
     *
     * @param start the start point
     * @param end   the end point
     * @return the ray between the start and the end
     */
    public static Ray between(Vector3d start, Vector3d end) {
        return new Ray(start, end.subtract(start));
    }
}
