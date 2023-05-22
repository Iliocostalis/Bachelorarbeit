package org.example;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Stack;

public class VectorMatrixPool {

    private static Stack<Vector3f> vector3fsPool = new Stack<>();
    private static Stack<Vector3f> vector3fsLent = new Stack<>();
    private static Stack<Matrix4f> matrix4fsPool = new Stack<>();
    private static Stack<Matrix4f> matrix4fsLent = new Stack<>();
    private static Stack<Quaternionf> quaternionfsPool = new Stack<>();
    private static Stack<Quaternionf> quaternionfsLent = new Stack<>();

    public static Vector3f getVector3f()
    {
        if(vector3fsPool.isEmpty())
        {
            vector3fsPool.push(new Vector3f());
        }

        vector3fsLent.push(vector3fsPool.peek());
        return vector3fsPool.pop();
    }

    public static Matrix4f getMatrix4f()
    {
        if(matrix4fsPool.isEmpty())
        {
            matrix4fsPool.push(new Matrix4f());
        }

        matrix4fsLent.push(matrix4fsPool.peek());
        return matrix4fsPool.pop();
    }

    public static Quaternionf getQuaternionf()
    {
        if(quaternionfsPool.isEmpty())
        {
            quaternionfsPool.push(new Quaternionf());
        }

        quaternionfsLent.push(quaternionfsPool.peek());
        return quaternionfsPool.pop();
    }

    public static void returnAll()
    {
        while (!vector3fsLent.isEmpty())
        {
            vector3fsPool.push(vector3fsLent.pop());
        }
        while (!matrix4fsLent.isEmpty())
        {
            matrix4fsPool.push(matrix4fsLent.pop());
        }
        while (!quaternionfsLent.isEmpty())
        {
            quaternionfsPool.push(quaternionfsLent.pop());
        }
    }
}
