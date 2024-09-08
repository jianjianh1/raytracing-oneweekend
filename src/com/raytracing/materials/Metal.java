package com.raytracing.materials;

import com.raytracing.base.PixelColor;
import com.raytracing.base.Vector3d;
import com.raytracing.interfaces.Hittable;
import com.raytracing.interfaces.Material;
import com.raytracing.scene.Ray;

public record Metal(PixelColor albedo, double fuzziness) implements Material {
    /**
     * Constructs a metal surface with fuzziness that's less than 1.
     *
     * @param albedo    the albedo
     * @param fuzziness the fuzziness
     */
    public Metal {
        fuzziness = Math.min(fuzziness, 1);
    }

    /**
     * Returns a record of scattering of a hit.
     *
     * @param hitRecord the hit record
     * @return a scatter record
     */
    @Override
    public ScatterRecord scatter(Hittable.HitRecord hitRecord) {
        var reflectedDirection = hitRecord.ray().unitDirection().reflectOn(hitRecord.normal());
        var scatteredRay = new Ray(hitRecord.point(), reflectedDirection.add(Vector3d.randomUnitUniform().scale(fuzziness)), hitRecord.ray().time());
        return new ScatterRecord(albedo, scatteredRay);
    }
}
