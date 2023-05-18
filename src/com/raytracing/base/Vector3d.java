package com.raytracing.base;

/**
 * 3-dimensional vector
 *
 * @param x the x-coordinate
 * @param y the y-coordinate
 * @param z the z-coordinate
 */
public record Vector3d(double x, double y, double z) {
    /**
     * Constructs a vector with zeros in all dimensions
     */
    public Vector3d() {
        this(0, 0, 0);
    }

    /**
     * Returns the length of the vector
     *
     * @return the length of the vector
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Returns the squared length of the vector
     *
     * @return the squared length of the vector
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Adds this vector and another vector and returns the result
     *
     * @param other another vector
     * @return the resulting vector of adding
     */
    public Vector3d add(Vector3d other) {
        return new Vector3d(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Subtracts this vector and another vector and returns the result
     *
     * @param other another vector
     * @return the resulting vector of subtracting
     */
    public Vector3d subtract(Vector3d other) {
        return new Vector3d(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Returns a scaled vector by the given factor
     *
     * @param factor the scaling factor
     */
    public Vector3d scale(double factor) {
        return new Vector3d(factor * x, factor * y, factor * z);
    }

    /**
     * Returns the opposite vector of this vector
     *
     * @return the opposite vector
     */
    public Vector3d opposite() {
        return scale(-1);
    }

    /**
     * Returns the dot product of this vector and another vector
     *
     * @param other another vector
     * @return the dot product
     */
    public double dot(Vector3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Returns the cross product between this vector and another vector
     *
     * @param other another vector
     * @return the cross product
     */
    public Vector3d cross(Vector3d other) {
        return new Vector3d(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    /**
     * Returns the unit vector with the same direction
     *
     * @return the unit vector with the same direction
     */
    public Vector3d normalize() {
        return scale(1 / length());
    }

    /**
     * Returns a random unit vector.
     *
     * @return a random unit vector
     */
    public static Vector3d randomUnit() {
        double phi = Math.random() * 2 * Math.PI;
        double theta = Math.random() * Math.PI;
        double x = Math.sin(theta) * Math.cos(phi);
        double y = Math.sin(theta) * Math.sin(phi);
        double z = Math.cos(theta);
        return new Vector3d(x, y, z);
    }

    /**
     * Returns true if this vector is near zero.
     *
     * @return true if this vector is near zero else false
     */
    public boolean nearZero() {
        final double epsilon = 1e-8;
        return x < epsilon && y < epsilon && z < epsilon;
    }

    /**
     * Returns the reflected vector on the surface with the given normal
     * @param surfaceNormal the normal of the surface
     * @return the reflected vector
     */
    public Vector3d reflectOn(Vector3d surfaceNormal) {
        surfaceNormal = surfaceNormal.normalize();
        // v - 2(v dot n) * n
        return this.subtract(surfaceNormal.scale(2 * this.dot(surfaceNormal)));
    }
}
