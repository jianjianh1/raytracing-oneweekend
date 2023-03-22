package com.raytracing.objects;

import com.raytracing.basis.Vector3d;

/**
 * Represents rays in 3D scenes
 *
 * @param origin    the origin
 * @param direction the direction
 */
public record Ray(Vector3d origin, Vector3d direction) {
    /**
     * Returns the position of the ray's end point at t
     *
     * @param t a scale or time
     * @return the position of the ray's end point
     */
    public Vector3d at(double t) {
        return origin.add(direction.scale(t));
    }
}
