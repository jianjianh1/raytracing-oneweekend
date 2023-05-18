package com.raytracing.interfaces;

import com.raytracing.base.PixelColor;
import com.raytracing.objects.Ray;

/**
 * Represents surface material in the raytracer.
 */
public interface Material {
    /**
     * Records a scattering on the material.
     *
     * @param attenuation  the attenuated color
     * @param scatteredRay the scattered ray
     */
    record ScatterRecord(PixelColor attenuation, Ray scatteredRay) {
    }

    /**
     * Returns a record of scattering of a hit.
     *
     * @param hitRecord the hit record
     * @return a scatter record
     */
    ScatterRecord scatter(Hittable.HitRecord hitRecord);
}
