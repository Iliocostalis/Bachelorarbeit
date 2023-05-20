package org.example;

import org.example.assets.JsonObjektInstance;
import org.joml.Vector3f;

public class Objekt {
    public Transformation transformation;

    public int meshHash;

    private Collider collider;

    public Objekt(JsonObjektInstance jsonObjektInstance, int meshHash)
    {
        this.meshHash = meshHash;
        transformation = new Transformation(jsonObjektInstance);
        collider = new Collider(this);
    }

    public boolean isRayIntersecting(Vector3f rayOrigin, Vector3f rayDirection)
    {
        return collider.isRayIntersecting(rayOrigin, rayDirection);
    }
}
