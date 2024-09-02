package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Texture;

public record SolidColor(PixelColor albedo) implements Texture {

    /**
     * Constructs a solid color texture with the given RGB
     */
    public SolidColor(double red, double green, double blue) {
        this(new PixelColor(red, green, blue));
    }

    /**
     * Get the texture color with a given (u, v) coordinate and a point
     */
    @Override
    public PixelColor value(double u, double v, Vector3d p) {
        return albedo;
    }
}
