package org.example;

import org.example.assets.JsonObjektInstance;

public class Objekt {
    public Transformation transformation;

    public int meshHash;

    public Objekt(JsonObjektInstance jsonObjektInstance, int meshHash)
    {
        transformation = new Transformation(jsonObjektInstance);
        this.meshHash = meshHash;
    }
}
