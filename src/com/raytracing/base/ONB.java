package com.raytracing.base;

import java.util.Objects;

/**
 * Orthonormal Basis
 */
public final class ONB {
    private final Vector3d u;
    private final Vector3d v;
    private final Vector3d w;

    /**
     * Builds orthonormal basis from a surface normal
     */
    public ONB(Vector3d normal) {
        w = normal.normalized();
        Vector3d a = Math.abs(w.x()) > 0.9 ? new Vector3d(0, 1, 0) : new Vector3d(1, 0, 0);
        v = a.cross(w).normalized();
        u = w.cross(v);
    }

    /**
     * Transforms a vector into coordinate system with this basis
     */
    public Vector3d transform(Vector3d vector) {
        return u.scale(vector.x()).add(v.scale(vector.y())).add(w.scale(vector.z()));
    }

    public Vector3d u() {
        return u;
    }

    public Vector3d v() {
        return v;
    }

    public Vector3d w() {
        return w;
    }
}
