package com.raytracing.pdf;

import com.raytracing.base.ONB;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.PDF;

/**
 * Density proportional to the cosine of angle from the normal direction
 */
public class CosinePdf implements PDF {
    private final ONB uvw;

    /**
     * Construct the cosine pdf by the normal direction
     */
    public CosinePdf(Vector3d normal) {
        uvw = new ONB(normal);
    }

    /**
     * @return the corresponding com.raytracing.pdf value in the direction
     */
    @Override
    public double value(Vector3d direction) {
        double cosTheta = direction.normalized().dot(uvw.w());
        return Math.max(0, cosTheta / Math.PI);
    }

    /**
     * @return a random direction weighted by the internal com.raytracing.pdf distribution
     */
    @Override
    public Vector3d generate() {
        return uvw.transform(Vector3d.randomUnitCosine());
    }
}
