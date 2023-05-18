package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;

public record Lambertian(PixelColor albedo) implements Material {
    /**
     * Returns a record of scattering of a hit.
     *
     * @param hitRecord the hit record
     * @return a scatter record
     */
    @Override
    public ScatterRecord scatter(Hittable.HitRecord hitRecord) {
        var scatterDirection = hitRecord.normal().add(Vector3d.randomUnit());
        var scatteredRay = new Ray(hitRecord.point(), scatterDirection);
        return new ScatterRecord(albedo, scatteredRay);
    }
}
