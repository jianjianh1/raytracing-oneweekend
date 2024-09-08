package com.raytracing.interfaces;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.scene.Ray;

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
    record ScatterRecord(PixelColor attenuation, Ray scatteredRay, double pdf) {
        public ScatterRecord(PixelColor attenuation, Ray scatteredRay) {
            this(attenuation, scatteredRay, 0.0);
        }
    }

    /**
     * Returns a record of scattering of a hit. Default returns null
     *
     * @param hitRecord the hit record
     * @return a scatter record
     */
    default ScatterRecord scatter(Hittable.HitRecord hitRecord) {
        return null;
    }

    /**
     * Default emit no light
     */
    default PixelColor emitted(Hittable.HitRecord hitRecord, double u, double v, Vector3d p) {
        return PixelColor.BLACK;
    }

    /**
     * The scattering probability density function
     */
    default double scatteringPdf(Hittable.HitRecord hitRecord, Ray rayOut) {
        return 0.0;
    };
}
