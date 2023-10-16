package org.example.sensors;

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

    private float zNear;
    private float zFar;
    private float aspect;
    private float fov;

    private RenderTarget renderTarget;

    public Kamera(RenderTarget renderTarget)
    {
        this(renderTarget, 60f, 0.1f, 500f);
    }

    public Kamera(RenderTarget renderTarget, float fov, float zNear, float zFar)
    {
        this.renderTarget = renderTarget;
        this.fov = fov;
        aspect = (float)renderTarget.getWidth() / (float)renderTarget.getHeight();
        this.zNear = zNear;
        this.zFar = zFar;
        updatePerspective();
        view.lookAt(position, lookAt, up);
    }

    private void updatePerspective()
    {
        projection.perspective((float)Math.toRadians(fov) , aspect, zNear, zFar, true);
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

    public RENDER_TARGET_COLOR_FORMAT getColorFormat()
    {
        return renderTarget.getColorFormat();
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

    public float getZNear()
    {
        return zNear;
    }

    public float getZFar()
    {
        return zFar;
    }

    public float getAspect()
    {
        return aspect;
    }

    public float getFov()
    {
        return fov;
    }
}