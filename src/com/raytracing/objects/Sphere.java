package com.raytracing.objects;

import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;

/**
 * Represents a sphere that can be hit by a ray
 *
 * @param center the center of the sphere
 * @param radius the radius of the sphere
 */
public record Sphere(Vector3d center, double radius, Material material) implements Hittable {
    /**
     * Returns the surface normal vector from a given point
     *
     * @param point a point that should be on the surface of the sphere
     * @return the surface normal vector
     */
    public Vector3d normalAt(Vector3d point) {
        return point.subtract(center).scale(1 / radius);
    }

    /**
     * Returns a {@code HitRecord} if the ray hit the sphere
     *
     * @param ray  the ray
     * @param tMin the minimum scale of direction
     * @param tMax the maximum scale of direction
     * @return the {@code HitRecord} if hit else null
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        // require us to solve (origin + t * direction - center) * (origin + t * direction - center) = r * r
        // we can write it as at^2 + bt + c = 0
        Vector3d originToCenter = ray.origin().subtract(center);
        double a = ray.direction().lengthSquared();
        double halfB = ray.direction().dot(originToCenter); // negative
        double c = originToCenter.lengthSquared() - radius * radius;
        double quarterDiscriminant = halfB * halfB - a * c;

        double t;
        if (quarterDiscriminant < 0) {
            return null;
        } else {
            t = (-halfB - Math.sqrt(quarterDiscriminant)) / a; // first intersection
        }
        if (tMin <= t && t <= tMax) {
            return new HitRecord(ray, t, normalAt(ray.at(t)), material);
        } else {
            t = (-halfB + Math.sqrt(quarterDiscriminant)) / a; // second intersection
        }
        if (tMin <= t && t <= tMax) {
            return new HitRecord(ray, t, normalAt(ray.at(t)), material);
        } else {
            return null;
        }
    }
}
