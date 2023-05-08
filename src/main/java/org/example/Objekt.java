package org.example;

public class Objekt {
    public Transformation transformation = new Transformation();
    public Mesh mesh;

    public ShaderTyp getShaderTyp()
    {
        return mesh.shaderTyp;
    }
}
