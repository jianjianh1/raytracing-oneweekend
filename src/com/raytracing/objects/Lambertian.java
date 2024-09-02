package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.interfaces.Texture;

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
        var scatterDirection = hitRecord.normal().add(Vector3d.randomUnit());
        var scatteredRay = new Ray(hitRecord.point(), scatterDirection, hitRecord.ray().time());
        var attenuation = texture.value(hitRecord.u(), hitRecord.v(), hitRecord.point());
        return new ScatterRecord(attenuation, scatteredRay);
    }
}
