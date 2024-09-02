package com.raytracing.scene;

import com.raytracing.interfaces.Hittable;
import com.raytracing.base.AABB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a list of {@code Hittable} that itself can be hit as any of {@code Hittable} in the list.
 */
public class HittableList implements Hittable, Iterable<Hittable> {
    private final List<Hittable> list;
    private AABB boundingBox;

    /**
     * Constructs an empty list of {@code Hittable}.
     */
    public HittableList() {
        list = new ArrayList<>();
        boundingBox = new AABB();
    }

    /**
     * Constructs a hittable list with only one hittable
     */
    public HittableList(Hittable hittable) {
        this();
        add(hittable);
    }

    /**
     * Adds a {@code Hittable} to the list
     *
     * @param hittable a {@code Hittable}
     */
    public void add(Hittable hittable) {
        list.add(hittable);
        boundingBox = new AABB(boundingBox, hittable.boundingBox());
    }

    /**
     * Returns the closest record that the ray hit anything in the list at the given range.
     *
     * @param ray  the ray
     * @param tMin the minimum scale of direction
     * @param tMax the maximum scale of direction
     * @return the first {@code HitRecord} if the ray hit anything in the list
     */
    @Override
    public HitRecord hit(Ray ray, double tMin, double tMax) {
        double tClosest = tMax;
        HitRecord closest = null;
        for (var hittable : list) {
            var record = hittable.hit(ray, tMin, tClosest);
            if (record != null) {
                closest = record;
                tClosest = closest.t();
            }
        }
        return closest;
    }

    /**
     * @return The bounding box of the list of hittable objects
     */
    @Override
    public AABB boundingBox() {
        return boundingBox;
    }

    /**
     * Returns an iterator over elements of type {@code Hittable}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Hittable> iterator() {
        return list.iterator();
    }

    /**
     * @return The list of objects
     */
    public List<Hittable> objects() {
        return list;
    }
}
