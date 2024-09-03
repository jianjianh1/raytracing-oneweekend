package com.raytracing.transform;

import com.raytracing.base.AABB;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.scene.Ray;

public record RotateY(Hittable object, double sinTheta, double cosTheta, AABB boundingBox) implements Hittable {

    /**
     * Constructs a rotated instance of a given object that's rotated about y-axis with a given degree
     */
    public RotateY(Hittable object, double angleInDegree) {
        this(
                object,
                Math.sin(Math.toRadians(angleInDegree)),
                Math.cos(Math.toRadians(angleInDegree)),
                object.boundingBox().rotateY(angleInDegree)
        );
    }

    /**
     * Move the ray to object space, get the hit record then transform the position and normal
     * to world space
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        var origin = new Vector3d(
                cosTheta * ray.origin().x() - sinTheta * ray.origin().z(),
                ray.origin().y(),
                sinTheta * ray.origin().x() + cosTheta * ray.origin().z()
        );
        var direction = new Vector3d(
                cosTheta * ray.direction().x() - sinTheta * ray.direction().z(),
                ray.direction().y(),
                sinTheta * ray.direction().x() + cosTheta * ray.direction().z()
        );

        var rotatedRay = new Ray(origin, direction, ray.time());
        var hit = object.hit(rotatedRay, tMin, tMax);
        if (hit != null) {
            var normal = new Vector3d(
                    cosTheta * hit.normal().x() + sinTheta * hit.normal().z(),
                    hit.normal().y(),
                    -sinTheta * hit.normal().x() + cosTheta * hit.normal().z()
            );
            hit = new HitRecord(ray, hit.t(), normal, hit.frontFace(), hit.material(), hit.u(), hit.v());
        }

        return hit;
    }
}
