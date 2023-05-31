package org.example;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Kamera {

    private Matrix4f projection = new Matrix4f();
    private Matrix4f view = new Matrix4f();
    private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public Vector3f position = new Vector3f();
    public Vector3f lookAt = new Vector3f();
    private Vector3f up = new Vector3f(0,1,0);

    private RenderTarget renderTarget;

    Kamera(RenderTarget renderTarget)
    {
        this.renderTarget = renderTarget;
        projection.perspective((float)Math.toRadians(60.f) , 16.0f / 9.0f, 0.1f, 1000.f, true);
        view.lookAt(position, lookAt, up);
    }

    public void updateMatrix(Vector3f position, Quaternionf quaternionf)
    {
        this.position.set(position);
        quaternionf.transform(1,0,0,lookAt);
        lookAt.add(position);

        updateMatrix();
    }

    public void updateMatrix()
    {
        view.identity();
        view.lookAt(position, lookAt, up);
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
