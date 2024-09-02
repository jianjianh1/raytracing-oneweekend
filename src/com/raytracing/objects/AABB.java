package com.raytracing.objects;

import com.raytracing.base.Interval;
import com.raytracing.base.Vector3d;

/**
 * Axis-Aligned Bounding Box
 */
public record AABB(Interval xRange, Interval yRange, Interval zRange) {

    /**
     * Constructs an empty AABB
     */
    public AABB() {
        this(new Interval(), new Interval(), new Interval());
    }

    /**
     * Constructs an AABB using information of two corners
     */
    public AABB(Vector3d corner1, Vector3d corner2) {
        this(
                corner1.x() <= corner2.x() ? new Interval(corner1.x(), corner2.x()) : new Interval(corner2.x(), corner1.x()),
                corner1.y() <= corner2.y() ? new Interval(corner1.y(), corner2.y()) : new Interval(corner2.y(), corner1.y()),
                corner1.z() <= corner2.z() ? new Interval(corner1.z(), corner2.z()) : new Interval(corner2.z(), corner1.z())
        );
    }

    /**
     * Constructs an AABB that contains the two boxes
     */
    public AABB(AABB box1, AABB box2) {
        this(
                new Interval(box1.xRange, box2.xRange),
                new Interval(box1.yRange, box2.yRange),
                new Interval(box1.zRange, box2.zRange)
        );
    }

    /**
     * Get the interval of corresponding axis by index
     * @param index the index
     * @return the interval of corresponding axis
     */
    public Interval axisInterval(int index) {
        return switch (index) {
            case 1 -> yRange;
            case 2 -> zRange;
            default -> xRange;
        };
    }

    /**
     * @return The index of longest axis
     */
    public int longestAxis() {
        double xSize = xRange.size();
        double ySize = yRange.size();
        double zSize = zRange.size();
        if (xSize >= ySize && xSize >= zSize) return 0;
        else if (ySize >= xSize && ySize >= zSize) return 1;
        else return 2;
    }

    /**
     * @return Whether a ray hits this AABB within the given range of t
     */
    boolean hit(Ray ray, double tMin, double tMax) {
        for (int axis = 0; axis < 3; axis++) {
            Interval ax = axisInterval(axis);
            double dInv = 1.0 / ray.direction().component(axis);
            double t0 = (ax.min() - ray.origin().component(axis)) * dInv;
            double t1 = (ax.max() - ray.origin().component(axis)) * dInv;

            if (t0 < t1) {
                if (t0 > tMin) tMin = t0;
                if (t1 < tMax) tMax = t1;
            } else {
                if (t1 > tMin) tMin = t1;
                if (t0 < tMax) tMax = t0;
            }

            if (tMin >= tMax) return false;
        }
        return true;
    }

    /**
     * @return Whether a ray hits this AABB within the given range of t (represented as an interval)
     */
    boolean hit(Ray ray, Interval tRange) {
        return hit(ray, tRange.min(), tRange.max());
    }
}
