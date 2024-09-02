package com.raytracing.objects;

import com.raytracing.base.Perlin;
import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Texture;

public record NoiseTexture(Perlin noise, double scale) implements Texture {

    /**
     * Constructs a noise texture with new perlin noise with scale 1.0
     */
    public NoiseTexture() {
        this(new Perlin(), 1.0);
    }

    /**
     * Constructs a noise texture with given scale
     */
    public  NoiseTexture(double scale) {
        this(new Perlin(), scale);
    }

    /**
     * Get noise color with a given 3D point
     */
    @Override
    public PixelColor value(double u, double v, Vector3d p) {
        double color = 0.5 + 0.5 * noise.noise(p.scale(scale));
        return new PixelColor(color, color, color);
    }
}
