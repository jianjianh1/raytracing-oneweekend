package com.raytracing.pdf;

import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.PDF;

import java.util.Random;

public record MixturePdf(PDF p0, PDF p1) implements PDF {

    private static final Random rng = new Random(42);

    /**
     * @return mix the two pdf values by the same direction
     */
    @Override
    public double value(Vector3d direction) {
        return 0.5 * p0.value(direction) + 0.5 * p1.value(direction);
    }

    /**
     * @return a random direction weighted by the internal com.raytracing.pdf distribution
     */
    @Override
    public Vector3d generate() {
        if (rng.nextDouble() < 0.5) {
            return p0.generate();
        } else {
            return p1.generate();
        }
    }
}
