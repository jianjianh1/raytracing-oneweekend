package com.raytracing.objects;

import com.raytracing.basis.Vector3d;

/**
 * Represents a sphere that can be hit by a ray
 *
 * @param center the center of the sphere
 * @param radius the radius of the sphere
 */
public record Sphere(Vector3d center, double radius) {
    /**
     * Detects if this sphere is hit by the given ray
     *
     * @param ray a ray
     * @return the parameter representing the position the ray hit the sphere if it hits else -1
     */
    public double hitBy(Ray ray) {
        // require us to solve (origin + t * direction - center) * (origin + t * direction - center) = r * r
        // we can write it as at^2 + bt + c = 0
        Vector3d originToCenter = ray.origin().subtract(center);
        double a = ray.direction().lengthSquared();
        double halfB = ray.direction().dot(originToCenter); // negative
        double c = originToCenter.lengthSquared() - radius * radius;
        double quarterDiscriminant = halfB * halfB - a * c;
        if (quarterDiscriminant < 0) {
            return -1;
        } else {
            return (-halfB - Math.sqrt(quarterDiscriminant)) / a; // first intersection
        }
    }

    /**
     * Returns the surface normal vector from a given point
     *
     * @param point a point that should be on the surface of the sphere
     * @return the surface normal vector
     */
    public Vector3d surfaceNormal(Vector3d point) {
        return point.subtract(center).normalize();
    }
}
