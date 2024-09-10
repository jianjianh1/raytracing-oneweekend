package com.raytracing.interfaces;

import com.raytracing.base.Vector3d;

/**
 * A com.raytracing.pdf is responsible for
 * 1. returning a random direction weighted by the internal com.raytracing.pdf distribution
 * 2. returning the corresponding com.raytracing.pdf distribution value in that direction
 */
public interface PDF {
    /**
     * @return the corresponding com.raytracing.pdf value in the direction
     */
    double value(Vector3d direction);

    /**
     * @return a random direction weighted by the internal com.raytracing.pdf distribution
     */
    Vector3d generate();
}
