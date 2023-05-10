package org.example;

import org.example.assets.JsonObjektInstance;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Transformation {
    private Vector3f position;
    private Quaternionf quaternion;
    private Matrix4f matrix;

    private FloatBuffer matrixBuffer;

    private boolean gotModified;

    public Transformation()
    {
        position = new Vector3f();
        quaternion = new Quaternionf();
        matrix = new Matrix4f();
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        gotModified = true;
    }

    public Transformation(JsonObjektInstance jsonObjektInstance)
    {
        position = new Vector3f(jsonObjektInstance.position[0], jsonObjektInstance.position[1], jsonObjektInstance.position[2]);
        quaternion = new Quaternionf();
        quaternion.rotationXYZ(jsonObjektInstance.rotation[0] * ConstValues.DEGREES_TO_RADIANS, jsonObjektInstance.rotation[1] * ConstValues.DEGREES_TO_RADIANS, jsonObjektInstance.rotation[2] * ConstValues.DEGREES_TO_RADIANS);
        matrix = new Matrix4f();
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        gotModified = true;
    }

    public void calculateMatrix()
    {
        matrix.identity();
        matrix.translate(position);
        matrix.rotate(quaternion);
        matrix.get(matrixBuffer);
        gotModified = false;
    }

    public void getPosition(Vector3f out)
    {
        out.set(position);
    }

    public void setPosition(Vector3f position)
    {
        gotModified = true;
        this.position.set(position);
    }

    public void getQuaternion(Quaternionf out)
    {
        out.set(quaternion);
    }

    public void setQuaternion(Quaternionf quaternion)
    {
        gotModified = true;
        this.quaternion.set(quaternion);
    }

    public FloatBuffer getMatrix()
    {
        if(gotModified)
            calculateMatrix();
        return matrixBuffer;
    }
}
