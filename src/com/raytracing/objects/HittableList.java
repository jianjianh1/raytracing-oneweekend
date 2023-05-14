package com.raytracing.objects;

import com.raytracing.interfaces.Hittable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a list of {@code Hittable} that itself can be hit as any of {@code Hittable} in the list.
 */
public class HittableList implements Hittable, Iterable<Hittable> {
    private final List<Hittable> list;

    /**
     * Constructs an empty list of {@code Hittable}.
     */
    public HittableList() {
        list = new ArrayList<>();
    }

    /**
     * Adds a {@code Hittable} to the list
     *
     * @param hittable a {@code Hittable}
     */
    public void add(Hittable hittable) {
        list.add(hittable);
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
     * Returns an iterator over elements of type {@code Hittable}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Hittable> iterator() {
        return list.iterator();
    }
}
