package com.raytracing.scene;

import com.raytracing.base.AABB;
import com.raytracing.base.Interval;
import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Texture;
import com.raytracing.materials.Isotropic;

public record ConstantMedium(Hittable boundary, double density, Isotropic phaseFunc) implements Hittable {

    /**
     * Constructs a constant medium with given texture as isotropic material
     */
    public ConstantMedium(Hittable boundary, double density, Texture texture) {
        this(boundary, density, new Isotropic(texture));
    }

    /**
     * Construct a constant medium with constant albedo
     */
    public ConstantMedium(Hittable boundary, double density, PixelColor albedo) {
        this(boundary, density, new Isotropic(albedo));
    }

    /**
     * @return a hit record if the ray enter the boundary and hit before leaving the boundary
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        HitRecord enter = boundary.hit(ray, Interval.UNIVERSE);
        if (enter == null) return null;

        HitRecord leave = boundary.hit(ray, new Interval(enter.t() + 1e-3, Double.POSITIVE_INFINITY));
        if (leave == null) return null;

        double tEnter = Math.max(enter.t(), tMin);
        double tLeave = Math.min(leave.t(), tMax);
        if (tEnter >= tLeave) return null;

        double rayLength = ray.direction().length();
        double distanceInside = (tLeave - tEnter) * rayLength;
        double hitDistance = -Math.log(Math.random()) / density;
        if (hitDistance > distanceInside) return null;

        double t = tEnter + hitDistance / rayLength;
        Vector3d normal = new Vector3d(1, 0, 0); // arbitrary
        return new HitRecord(ray, t, normal, true, phaseFunc, 0, 0);
    }

    /**
     * @return The bounding box of boundary
     */
    @Override
    public AABB boundingBox() {
        return boundary.boundingBox();
    }
}
