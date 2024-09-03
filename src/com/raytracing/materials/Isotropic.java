package com.raytracing.materials;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.interfaces.Texture;
import com.raytracing.scene.Ray;
import com.raytracing.textures.SolidColor;

public record Isotropic(Texture texture) implements Material {

    /**
     * Constructs an isotropic material with constant albedo
     */
    public Isotropic(PixelColor albedo) {
        this(new SolidColor(albedo));
    }

    /**
     * Randomly scatter a ray attenuated
     */
    public ScatterRecord scatter(Hittable.HitRecord hitRecord) {
        var scattered = new Ray(hitRecord.point(), Vector3d.randomUnit(), hitRecord.ray().time());
        var attenuation = texture.value(hitRecord.u(), hitRecord.v(), hitRecord.point());
        return new ScatterRecord(attenuation, scattered);
    }
}
