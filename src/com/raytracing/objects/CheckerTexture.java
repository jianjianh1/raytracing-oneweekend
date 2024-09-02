package com.raytracing.objects;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Texture;

public record CheckerTexture(double scale, Texture even, Texture odd) implements Texture {
    /**
     * Constructs a checker texture with a given scale and two colors
     */
    public CheckerTexture(double scale, PixelColor color1, PixelColor color2) {
        this(
                scale,
                new SolidColor(color1),
                new SolidColor(color2)
        );
    }

    /**
     * Get the texture color with a given (u, v) coordinate and a point
     */
    @Override
    public PixelColor value(double u, double v, Vector3d p) {
        int x = (int)Math.floor(p.x() / scale);
        int y = (int)Math.floor(p.y() / scale);
        int z = (int)Math.floor(p.z() / scale);

        return (x + y + z) % 2 == 0 ? even.value(u, v, p) : odd.value(u, v, p);
    }
}
