package com.raytracing.materials;

import com.raytracing.base.PixelColor;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.interfaces.Texture;
import com.raytracing.pdf.CosinePdf;
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
        var pdf = new CosinePdf(hitRecord.normal());
        var attenuation = texture.value(hitRecord.u(), hitRecord.v(), hitRecord.point());
        return new ScatterRecord(attenuation, pdf);
    }

    /**
     * The scattering probability density function, proportional to cosine theta
     */
    @Override
    public double scatteringPdf(Hittable.HitRecord hitRecord, Ray rayOut) {
        double cosTheta = hitRecord.normal().dot(rayOut.direction().normalized());
        return Math.max(0, cosTheta / Math.PI);
    }
}
