package org.example;

import org.example.assets.JsonObjektInstance;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Transformation {
    private Vector3f position;
    private Quaternionf quaternion;

    private float scale;
    private Matrix4f matrix;

    private FloatBuffer matrixBuffer;

    private boolean gotModified;

    private ArrayList<Listener> listeners;

    public Transformation()
    {
        listeners = new ArrayList<>();
        position = new Vector3f();
        quaternion = new Quaternionf();
        scale = 1f;
        matrix = new Matrix4f();
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        gotModified = true;
    }

    public Transformation(JsonObjektInstance jsonObjektInstance)
    {
        listeners = new ArrayList<>();
        position = new Vector3f(jsonObjektInstance.position[0], jsonObjektInstance.position[1], jsonObjektInstance.position[2]);
        quaternion = new Quaternionf();
        quaternion.rotationXYZ(jsonObjektInstance.rotation[0] * ConstValues.DEGREES_TO_RADIANS, jsonObjektInstance.rotation[1] * ConstValues.DEGREES_TO_RADIANS, jsonObjektInstance.rotation[2] * ConstValues.DEGREES_TO_RADIANS);
        scale = 1f;
        matrix = new Matrix4f();
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        gotModified = true;
    }

    public void calculateMatrix()
    {
        matrix.identity();
        matrix.translate(position);
        matrix.rotate(quaternion);
        matrix.scale(scale);
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
        notifyListeners();
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        gotModified = true;
        this.scale = scale;
        notifyListeners();
    }

    public void getQuaternion(Quaternionf out)
    {
        out.set(quaternion);
    }

    public void setQuaternion(Quaternionf quaternion)
    {
        gotModified = true;
        this.quaternion.set(quaternion);
        notifyListeners();
    }

    public FloatBuffer getMatrix()
    {
        if(gotModified)
            calculateMatrix();
        return matrixBuffer;
    }

    private void notifyListeners()
    {
        for (Listener listener : listeners) {
            listener.notifyListener();
        }
    }

    public void addModifiedListener(Listener listener)
    {
        listeners.add(listener);
    }

    public void removeModifiedListener(Listener listener)
    {
        listeners.remove(listener);
    }
}
