package org.example;

import org.example.assets.JsonObjektInstance;

public class Objekt {
    public Transformation transformation;
    private Mesh mesh;

    public int meshHash;

    public Objekt(JsonObjektInstance jsonObjektInstance, int meshHash)
    {
        transformation = new Transformation(jsonObjektInstance);
        this.meshHash = meshHash;
    }

    public ShaderTyp getShaderTyp()
    {
        return mesh.shaderTyp;
    }

    public int getMeshHash()
    {
        return meshHash;
    }
}
