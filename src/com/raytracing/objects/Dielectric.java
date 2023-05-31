package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;

/**
 * Represents a dielectric material
 *
 * @param indexOfRefraction the index of refraction
 */
public record Dielectric(double indexOfRefraction) implements Material {
    /**
     * Returns a record of scattering of a hit.
     *
     * @param hitRecord the hit record
     * @return a scatter record
     */
    @Override
    public ScatterRecord scatter(Hittable.HitRecord hitRecord) {
        PixelColor attenuation = PixelColor.WHITE;
        double refractionRatio = hitRecord.frontFace() ? (1 / indexOfRefraction) : indexOfRefraction;

        Vector3d in = hitRecord.ray().unitDirection();
        Vector3d normal = hitRecord.normal();
        double cosTheta = -in.dot(normal);
        double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
        if (refractionRatio * sinTheta > 1 || reflectance(cosTheta, refractionRatio) > Math.random()) {
            // reflect
            Vector3d reflectedDirection = in.reflectOn(normal);
            Ray reflectedRay = new Ray(hitRecord.point(), reflectedDirection);

            return new ScatterRecord(attenuation, reflectedRay);
        } else {
            // refract
            Vector3d refractedDirection = in.refractOn(normal, refractionRatio);
            Ray refractedRay = new Ray(hitRecord.point(), refractedDirection);

            return new ScatterRecord(attenuation, refractedRay);
        }
    }

    /**
     * Computes reflectance using Schlick's approximation
     * @param cosine the cosine value of the shooting-in angle
     * @param refractionRation the refraction ratio
     * @return the reflectance
     */
    private double reflectance(double cosine, double refractionRation) {
        double r0 = (1 - refractionRation) / (1 + refractionRation);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow(1 - cosine, 5);
    }
}
