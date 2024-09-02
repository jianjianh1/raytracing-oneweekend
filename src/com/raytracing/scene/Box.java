package com.raytracing.scene;

import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Material;

public class Box extends HittableList {

    /**
     * Constructs a box with two corners and material
     */
    public Box(Vector3d corner1, Vector3d corner2, Material material) {
        super();

        // construct the two opposite vertices
        var min = new Vector3d(
                Math.min(corner1.x(), corner2.x()),
                Math.min(corner1.y(), corner2.y()),
                Math.min(corner1.z(), corner2.z())
        );
        var max = new Vector3d(
                Math.max(corner1.x(), corner2.x()),
                Math.max(corner1.y(), corner2.y()),
                Math.max(corner1.z(), corner2.z())
        );

        var dx = new Vector3d(max.x() - min.x(), 0, 0);
        var dy = new Vector3d(0, max.y() - min.y(), 0);
        var dz = new Vector3d(0, 0, max.z() - min.z());

        add(new Quad(new Vector3d(min.x(), min.y(), max.z()), dx, dy, material)); // front
        add(new Quad(new Vector3d(max.x(), min.y(), max.z()), dz.opposite(), dy, material)); // right
        add(new Quad(new Vector3d(max.x(), min.y(), min.z()), dx.opposite(), dy, material)); // back
        add(new Quad(new Vector3d(min.x(), min.y(), min.z()), dz, dy, material)); // left
        add(new Quad(new Vector3d(min.x(), max.y(), max.z()), dx, dz.opposite(), material)); // top
        add(new Quad(new Vector3d(min.x(), min.y(), min.z()), dx, dz, material)); // bottom
    }
}
