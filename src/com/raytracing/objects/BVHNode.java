package com.raytracing.objects;

import com.raytracing.interfaces.Hittable;

import java.util.*;

public class BVHNode implements Hittable {
    private final Hittable left;
    private final Hittable right;
    private AABB boundingBox;

    /**
     * Constructs a BVH with a list of hittable
     */
    public BVHNode(HittableList list) {
        this(list.objects(), 0, list.objects().size());
    }

    /**
     * Constructs a BVH using a list of objects within a given range
     */
    private BVHNode(List<Hittable> objects, int start, int end) {
        boundingBox = new AABB();
        for (var object : objects) {
            boundingBox = new AABB(boundingBox, object.boundingBox());
        }
        int axis = boundingBox.longestAxis();

        Comparator<Hittable> comparator = (h1, h2) -> {
            double min1 = h1.boundingBox().axisInterval(axis).min();
            double min2 = h2.boundingBox().axisInterval(axis).min();
            return Double.compare(min1, min2);
        };

        if (end - start == 1) {
            left = objects.get(start);
            right = objects.get(start);
        } else if (end - start == 2) {
            left = objects.get(start);
            right = objects.get(start + 1);
        } else {
            objects.subList(start, end).sort(comparator);
            int mid = (start + end) / 2;
            left = new BVHNode(objects, start, mid);
            right = new BVHNode(objects, mid, end);
        }

        boundingBox = new AABB(left.boundingBox(), right.boundingBox());
    }

    /**
     * Returns a record that the ray hit this object at the given range.
     *
     * @param ray  the ray
     * @param tMin the minimum scale of direction
     * @param tMax the maximum scale of direction
     * @return a {@code HitRecord}
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        if (!boundingBox.hit(ray, tMin, tMax)) return null;

        HitRecord hitLeft = left.hit(ray, tMin, tMax);
        HitRecord hitRight = right.hit(ray, tMin, hitLeft == null ? tMax : hitLeft.t());
        return hitRight == null ? hitLeft : hitRight;
    }

    /**
     * @return The bounding box of hittable object
     */
    @Override
    public AABB boundingBox() {
        return boundingBox;
    }
}
