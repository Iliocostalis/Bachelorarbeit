package org.example.virtualEnvironment;

import org.example.ConstValues;
import org.example.Listener;
import org.example.jsonClasses.JsonObjectNew;
import org.joml.*;

import java.util.ArrayList;

public class Transformation {
    private Vector3f position;
    private Quaternionf quaternion;

    private float scale;
    private Matrix4f matrix;

    private boolean gotModified;

    private ArrayList<Listener> listeners;

    public Transformation()
    {
        listeners = new ArrayList<>();
        position = new Vector3f();
        quaternion = new Quaternionf();
        scale = 1f;
        matrix = new Matrix4f();
        gotModified = true;
    }

    public Transformation(JsonObjectNew jsonObjekt)
    {
        listeners = new ArrayList<>();
        position = new Vector3f(jsonObjekt.position[0], jsonObjekt.position[1], jsonObjekt.position[2]);
        quaternion = new Quaternionf();
        quaternion.rotationXYZ(jsonObjekt.rotation[0] * ConstValues.DEGREES_TO_RADIANS, jsonObjekt.rotation[1] * ConstValues.DEGREES_TO_RADIANS, jsonObjekt.rotation[2] * ConstValues.DEGREES_TO_RADIANS);
        scale = 1f;
        matrix = new Matrix4f();
        gotModified = true;
    }

    public void calculateMatrix()
    {
        matrix.identity();
        matrix.translate(position);
        matrix.rotate(quaternion);
        matrix.scale(scale);
        gotModified = false;
    }

    public Vector3fc getPosition()
    {
        return position;
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

    public Quaternionfc getQuaternion()
    {
        return quaternion;
    }

    public void setQuaternion(Quaternionf quaternion)
    {
        gotModified = true;
        this.quaternion.set(quaternion);
        notifyListeners();
    }

    public Matrix4fc getMatrix()
    {
        if(gotModified)
            calculateMatrix();

        return matrix;
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
