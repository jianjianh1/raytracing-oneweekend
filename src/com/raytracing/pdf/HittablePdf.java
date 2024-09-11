package com.raytracing.pdf;

import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.PDF;

public record HittablePdf(Hittable objects, Vector3d origin) implements PDF {
    /**
     * @return the corresponding pdf value if the light shoots from the direction
     */
    @Override
    public double value(Vector3d direction) {
        return objects.pdfValue(origin, direction);
    }

    /**
     * @return a random direction shoot from the origin to this hittable
     */
    @Override
    public Vector3d generate() {
        return objects.random(origin);
    }
}
