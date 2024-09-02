package com.raytracing.objects;

import com.raytracing.base.Perlin;
import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Texture;

public record NoiseTexture(Perlin noise) implements Texture {

    /**
     * Constructs a noise texture with new perlin noise
     */
    public NoiseTexture() {
        this(new Perlin());
    }

    /**
     * Get noise color with a given 3D point
     */
    @Override
    public PixelColor value(double u, double v, Vector3d p) {
        double color = noise.noise(p);
        return new PixelColor(color, color, color);
    }
}
