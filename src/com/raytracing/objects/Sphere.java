package com.raytracing.objects;

import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;

public record Sphere(Vector3d center, double radius, Material material, boolean isMoving, Vector3d velocity, AABB boundingBox) implements Hittable {
    /**
     * Construct a sphere with the given center, radius, and material that's not moving
     * @param center the center
     * @param radius the radius
     * @param material the material
     */
    public Sphere(Vector3d center, double radius, Material material) {
        this(center, radius, material, false, Vector3d.ZERO, new AABB(center.subtract(Vector3d.ONES.scale(radius)), center.add(Vector3d.ONES.scale(radius))));
    }

    /**
     * Construct a moving sphere
     * @param center1 the starting center position
     * @param center2 the ending center position
     * @param radius the radius
     * @param material the material
     */
    public Sphere(Vector3d center1, Vector3d center2, double radius, Material material) {
        this(center1, radius, material, true, center2.subtract(center1),
                new AABB(
                        new AABB(center1.subtract(Vector3d.ONES.scale(radius)), center1.add(Vector3d.ONES.scale(radius))),
                        new AABB(center2.subtract(Vector3d.ONES.scale(radius)), center2.add(Vector3d.ONES.scale(radius)))
                )
        );
    }

    /**
     * Get the center position at the given time
     * @param time a time
     * @return the center position
     */
    public Vector3d center(double time) {
        return center.add(velocity.scale(time));
    }

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
        Vector3d center = isMoving ? center(ray.time()) : this.center;
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
            var hit =  new HitRecord(ray, t, normalAt(ray.at(t)), material);
            Vector3d uv = getSphereUV(hit.normal());
            hit = new HitRecord(ray, t, normalAt(ray.at(t)), material, uv.x(), uv.y());
            return hit;
        } else {
            t = (-halfB + Math.sqrt(quarterDiscriminant)) / a; // second intersection
        }

        // check second intersection
        if (tMin <= t && t <= tMax) {
            var hit =  new HitRecord(ray, t, normalAt(ray.at(t)), material);
            Vector3d uv = getSphereUV(hit.normal());
            hit = new HitRecord(ray, t, normalAt(ray.at(t)), material, uv.x(), uv.y());
            return hit;
        } else {
            return null;
        }
    }

    /**
     * Get the (u, v) coordinates by a 3D position
     *
     * @return (u, v) coordinates as the first components in a 3D vector
     */
    private static Vector3d getSphereUV(Vector3d p) {
        p = p.normalized();
        double theta = Math.acos(p.y());
        double phi = Math.atan2(p.z(), p.x()) + Math.PI;

        return new Vector3d(phi / (2.0 * Math.PI), theta / Math.PI, 0.0);
    }
}
