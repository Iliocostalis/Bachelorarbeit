package org.example;

import org.example.assets.JsonCar;
import org.example.assets.JsonObjektInstance;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Auto extends Objekt{

    //Default direction -> X+

    ArrayList<Sensor> sensoren;
    Vector3f back = new Vector3f(0,0.1f,0);
    Vector3f front = new Vector3f(1,0.1f,0);

    Vector3f direction = new Vector3f(1,0,0);
    Vector3f directionFront = new Vector3f(1,0,0);
    Vector3f up = new Vector3f(0,1,0);

    Vector3f vector3fTmp = new Vector3f();
    Quaternionf quaternionTmp = new Quaternionf();

    float rotation = (float)Math.toRadians(20);
    Vector3f rotationFrontRelativ;

    static float rotationS = 0;
    float length = 0.5f;

    public Auto(JsonCar jsonCar, JsonObjektInstance jsonObjektInstance, int meshHash)
    {
        super(jsonObjektInstance, meshHash);

        length = jsonCar.car_length;
        Mesh mesh = UmgebungsLader.getMesh(meshHash);
        mesh.getSize(vector3fTmp);

        float scale = length / vector3fTmp.x;
        transformation.setScale(scale);
    }
    public void move()
    {
        //calculate front /back from rotation and position
        transformation.getQuaternion(quaternionTmp);
        directionFront.rotate(quaternionTmp, vector3fTmp);
        vector3fTmp.mul(length*0.5f);

        transformation.getPosition(front);
        front.add(vector3fTmp);
        transformation.getPosition(back);
        back.sub(vector3fTmp);

        // get direction
        front.sub(back, direction);
        direction.normalize();
        // rotate direction
        direction.rotateY((float)Math.toRadians(rotationS));

        // move front
        front.add(direction.mul(0.002f));



        // move back in direction of the new front position
        front.sub(back, direction);
        direction.normalize();
        direction.mul(-1f);

        front.add(direction.mul(length), back);

        // calculate new position (center of front and back)
        front.add(back, vector3fTmp);
        vector3fTmp.mul(0.5f);
        transformation.setPosition(vector3fTmp);



        // calculate new rotation
        front.sub(back, direction);
        direction.normalize();
        quaternionTmp.identity();
        //quaternionTmp.lookAlong(direction, up);
        quaternionTmp.rotateTo(directionFront, direction);
        //quaternionTmp.invert();
        transformation.setQuaternion(quaternionTmp);
    }
}
