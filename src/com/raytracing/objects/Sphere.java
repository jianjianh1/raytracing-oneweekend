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
     * @return true if the ray hits this sphere
     */
    public boolean hitBy(Ray ray) {
        // require us to solve (origin + t * direction - center) * (origin + t * direction - center) = r * r
        // we can write it as at^2 + bt + c = 0
        double a = ray.direction().lengthSquared();
        double b = 2 * ray.direction().dot(ray.origin().subtract(center));
        double c = ray.origin().subtract(center).lengthSquared() - radius * radius;
        return b * b - 4 * a * c > 0;
    }
}
