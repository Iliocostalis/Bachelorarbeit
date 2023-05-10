package org.example;

import org.example.assets.JsonObjektInstance;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Auto extends Objekt{
    ArrayList<Sensor> sensoren;
    Vector3f back = new Vector3f(0,0.1f,0);
    Vector3f front = new Vector3f(1,0.1f,0);

    Vector3f direction = new Vector3f(1,0,0);
    Vector3f directionFront;
    Vector3f up = new Vector3f(0,1,0);

    Vector3f vector3fTmp = new Vector3f();
    Quaternionf quaternionTmp = new Quaternionf();

    float rotation = (float)Math.toRadians(20);
    Vector3f rotationFrontRelativ;

    static float rotationS = 0;
    float length = 0.5f;

    public Auto(JsonObjektInstance jsonObjektInstance, int meshHash)
    {
        super(jsonObjektInstance, meshHash);
    }
    public void move()
    {
        direction.rotateY((float)Math.toRadians(rotationS));

        front.add(direction.mul(0.002f));

        front.sub(back, direction);
        direction.normalize();
        direction.mul(-1f);

        front.add(direction.mul(length), back);

        front.add(back, vector3fTmp);
        vector3fTmp.mul(0.5f);
        transformation.setPosition(vector3fTmp);



        front.sub(back, direction);
        direction.normalize();
        quaternionTmp.identity();
        quaternionTmp.lookAlong(direction, up);
        quaternionTmp.invert();
        transformation.setQuaternion(quaternionTmp);
    }
}
