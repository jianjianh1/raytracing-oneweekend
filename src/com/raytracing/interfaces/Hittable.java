package com.raytracing.interfaces;

import com.raytracing.base.Vector3d;
import com.raytracing.base.Interval;
import com.raytracing.objects.AABB;
import com.raytracing.objects.Ray;

/**
 * Represents objects that could be hit by {@code Ray}.
 */
public interface Hittable {
    /**
     * Records a hit.
     *
     * @param ray       the ray
     * @param t         the scale of direction
     * @param normal    the surface normal at the hit point
     * @param frontFace true if the ray hit at the front face
     * @param material  the material of the surface
     */
    record HitRecord(Ray ray, double t, Vector3d normal, boolean frontFace, Material material, double u, double v) {
        /**
         * Records that the ray hit the surface with the given normal at t.
         *
         * @param ray      the ray
         * @param t        the scale of direction
         * @param normal   the normal of the surface
         * @param material the material of the surface
         */
        public HitRecord(Ray ray, double t, Vector3d normal, Material material) {
            this(ray, t, normal, frontFace(ray, normal), material, 0.0, 0.0);
        }

        /**
         * Returns the surface normal where the ray hit, that is the outward normal
         * if the ray hit outward else inward normal.
         *
         * @return the surface normal where the ray hit
         */
        @Override
        public Vector3d normal() {
            return frontFace ? normal : normal.opposite();
        }

        /**
         * Returns the point where the ray hit the surface.
         *
         * @return the point where the ray hit the surface
         */
        public Vector3d point() {
            return ray.at(t);
        }

        /**
         * Returns true if the ray hits at the front face.
         *
         * @param ray    the ray
         * @param normal the surface normal
         * @return true if the ray hits at the front face else false
         */
        public static boolean frontFace(Ray ray, Vector3d normal) {
            return ray.direction().dot(normal) < 0;
        }
    }

    /**
     * Returns a record that the ray hit this object at the given range.
     *
     * @param ray  the ray
     * @param tMin the minimum scale of direction
     * @param tMax the maximum scale of direction
     * @return a {@code HitRecord}
     */
    HitRecord hit(Ray ray, double tMin, double tMax);

    /**
     * Returns a record if a ray hits an object within the given interval of t
     *
     * @param ray a ray
     * @param tRange an interval of t
     * @return a record if hit else null
     */
    default HitRecord hit(Ray ray, Interval tRange) {
        return hit(ray, tRange.min(), tRange.max());
    }

    /**
     * @return The bounding box of hittable object
     */
    AABB boundingBox();
}
