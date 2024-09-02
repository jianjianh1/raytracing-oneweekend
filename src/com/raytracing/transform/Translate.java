package com.raytracing.transform;

import com.raytracing.base.AABB;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.scene.Ray;

public record Translate(Hittable object, Vector3d offset, AABB boundingBox) implements Hittable {

    /**
     * Constructs a translated instance of an object with a given offset
     */
    public Translate(Hittable object, Vector3d offset) {
        this(
                object,
                offset,
                object.boundingBox().translate(offset)
        );
    }

    /**
     * Move the ray backwards to test hit, then translate the hit point back
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        Ray offsetRay = new Ray(
                ray.origin().subtract(offset),
                ray.direction(),
                ray.time()
        );

        HitRecord hit = object.hit(offsetRay, tMin, tMax);
        if (hit != null) {
            // use the original ray with other record unchanged
            hit = new HitRecord(ray, hit.t(), hit.normal(), hit.frontFace(), hit.material(), hit.u(), hit.v());
        }

        return hit;
    }
}
