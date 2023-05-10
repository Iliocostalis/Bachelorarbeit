package org.example;

import org.example.assets.JsonObjektInstance;
import org.joml.Vector3f;

public class CollidableObjekt extends Objekt{

    Vector3f boundingSphereCenter;
    float boundingSphereRadius;

    CollidableObjekt(JsonObjektInstance jsonObjektInstance, int meshHash)
    {
        super(jsonObjektInstance, meshHash);

    }

    public boolean getCollisionWithRay(Vector3f ray, Vector3f hitPosition)
    {
        return false;
    }
}
