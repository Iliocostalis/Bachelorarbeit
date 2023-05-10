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
    public Matrix4f view = new Matrix4f();
    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Transformation transformation = new Transformation();
    Vector3f lookAt = new Vector3f();
    Vector3f up = new Vector3f(0,1,0);

    RenderTarget renderTarget;

    Kamera(RenderTarget renderTarget)
    {
        this.renderTarget = renderTarget;
        projection.perspective((float)Math.toRadians(60.f) , 16.0f / 9.0f, 0.1f, 1000.f, true);

        transformation.position.x = 2f;
        transformation.position.z = -4f;
        transformation.position.y = 2f;
        transformation.quaternion.rotateXYZ((float)Math.toRadians(30.f), 0, 0);
        transformation.calculateMatrix();

        lookAt.x = 2;
        view.set(transformation.getMatrix());
        view.lookAt(transformation.position, lookAt, up);
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
