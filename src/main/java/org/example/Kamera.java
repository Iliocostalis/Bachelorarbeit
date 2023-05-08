package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Kamera {
    int width;
    int height;

    int FBO;
    int colorTexture;
    int depthBuffer;
    int uniformID;

    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f();
    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    Transformation transformation = new Transformation();

    RenderTarget renderTarget;

    Kamera(RenderTarget renderTarget)
    {
        this.renderTarget = renderTarget;
        projection.perspective((float)Math.toRadians(60.f) , 16.0f / 9.0f, 0.1f, 1000.f, true);

        transformation.position.z = -2f;
        transformation.calculateMatrix();
        transformation.matrix.get(view);
    }

    public void bindRenderTarget()
    {
        renderTarget.bind();
    }

    public void bindMatrixToShader(int programmId)
    {
        glUseProgram(programmId);
        projection.get(matrixBuffer);
        glUniformMatrix4fv(0, false, matrixBuffer);

        view.get(matrixBuffer);
        glUniformMatrix4fv(1, false, matrixBuffer);
    }
}
