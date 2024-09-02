package com.raytracing.scene;

import com.raytracing.base.Interval;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.base.AABB;

/**
 * Parallelogram primitive
 */
public record Quad(Vector3d origin, Vector3d u, Vector3d v, Vector3d w, Material material, AABB boundingBox, Vector3d normal, double D) implements Hittable {

    /**
     * Constructs a parallelogram with an origin, two sides vector, and material
     */
    public Quad(Vector3d origin, Vector3d u, Vector3d v, Material material) {
        this(
                origin,
                u,
                v,
                u.cross(v).scale(1.0 / u.cross(v).lengthSquared()),
                material,
                new AABB(
                        new AABB(origin, origin.add(u).add(v)),
                        new AABB(origin.add(u), origin.add(v))
                ),
                u.cross(v).normalized(),
                u.cross(v).normalized().dot(origin)
            );
    }

    /**
     * Returns a record that the ray hit this parallelogram at the given range.
     *
     * @param ray  the ray
     * @param tMin the minimum scale of direction
     * @param tMax the maximum scale of direction
     * @return a {@code HitRecord}
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        Interval tRange = new Interval(tMin, tMax);
        double denominator = normal.dot(ray.direction());
        if (Math.abs(denominator) < 1e-6) return null;

        double t = (D - normal.dot(ray.origin())) / denominator;
        if (!tRange.contains(t)) return null;

        Vector3d intersection = ray.at(t); // intersection on the plane
        Vector3d planeRelative = intersection.subtract(origin);
        double alpha = w.dot(planeRelative.cross(v));
        double beta = w.dot(u.cross(planeRelative));
        if (!Interval.UNIT.contains(alpha) || !Interval.UNIT.contains(beta)) return null;

        return new HitRecord(ray, t, normal, material, alpha, beta);
    }
}
