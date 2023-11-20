package org.example.shader;

import org.example.virtualEnvironment.Mesh;
import org.example.virtualEnvironment.VirtualObject;

public interface Shader {
    void draw(VirtualObject virtualObject, Mesh mesh);
    int getProgram();
}
