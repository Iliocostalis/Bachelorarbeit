package org.example.shader;

import org.example.virtualEnvironment.Mesh;
import org.example.virtualEnvironment.Objekt;

public interface Shader {
    void draw(Objekt objekt, Mesh mesh);
    int getProgram();
}
