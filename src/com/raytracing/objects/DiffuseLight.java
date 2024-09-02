package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.interfaces.Texture;

public record DiffuseLight(Texture texture) implements Material {

    /**
     * Constructs a diffuse light source emitting single color
     */
    public DiffuseLight(PixelColor color) {
        this(new SolidColor(color));
    }

    /**
     * Emits the texture color at (u, v)
     */
    @Override
    public PixelColor emitted(double u, double v, Vector3d p) {
        return texture.value(u, v, p);
    }
}
