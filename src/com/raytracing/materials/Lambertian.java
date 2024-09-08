package com.raytracing.materials;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.interfaces.Texture;
import com.raytracing.scene.Ray;
import com.raytracing.textures.SolidColor;

public record Lambertian(Texture texture) implements Material {
    /**
     * Constructs a lambertian material with a solid color (albedo)
     */
    public Lambertian(PixelColor albedo) {
        this(new SolidColor(albedo));
    }

    /**
     * Returns a record of scattering of a hit.
     *
     * @param hitRecord the hit record
     * @return a scatter record
     */
    @Override
    public ScatterRecord scatter(Hittable.HitRecord hitRecord) {
//        var scatterDirection = hitRecord.normal().add(Vector3d.randomUnit());
        var scatterDirection = Vector3d.randomUnit();
        if (scatterDirection.dot(hitRecord.normal()) < 0.0) {
            scatterDirection = scatterDirection.opposite();
        }
        var scatteredRay = new Ray(hitRecord.point(), scatterDirection, hitRecord.ray().time());
        var attenuation = texture.value(hitRecord.u(), hitRecord.v(), hitRecord.point());
        return new ScatterRecord(attenuation, scatteredRay);
    }

    /**
     * The scattering probability density function, proportional to cosine theta
     */
    @Override
    public double scatteringPdf(Ray rayIn, Hittable.HitRecord hitRecord, Ray rayOut) {
//        double cosTheta = hitRecord.normal().dot(rayOut.direction().normalized());
//        return Math.max(0, cosTheta / Math.PI);
        return 1.0 / (2.0 * Math.PI);
    }
}
