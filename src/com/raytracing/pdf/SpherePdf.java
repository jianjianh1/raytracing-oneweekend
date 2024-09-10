package com.raytracing.pdf;

import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.PDF;

/**
 * Uniform density over the unit sphere
 */
public class SpherePdf implements PDF {
    /**
     * @return the corresponding com.raytracing.pdf value in the direction
     */
    @Override
    public double value(Vector3d direction) {
        return 1.0 / (4.0 * Math.PI);
    }

    /**
     * @return a random direction weighted by the internal com.raytracing.pdf distribution
     */
    @Override
    public Vector3d generate() {
        return Vector3d.randomUnitUniform();
    }
}
