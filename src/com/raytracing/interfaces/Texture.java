package com.raytracing.interfaces;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;

public interface Texture {
    /**
     * Get the texture color with a given (u, v) coordinate and a point
     */
    PixelColor value(double u, double v, Vector3d p);
}
