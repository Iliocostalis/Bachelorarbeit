package org.example;

import org.joml.Vector3f;

import java.util.ArrayList;

public class Auto extends Objekt{
    ArrayList<Sensor> sensoren;
    Vector3f back = new Vector3f(0,0.1f,0);
    Vector3f front = new Vector3f(1,0.1f,0);

    Vector3f direction = new Vector3f(1,0,0);
    Vector3f directionFront;
    Vector3f up = new Vector3f(0,1,0);

    float rotation = (float)Math.toRadians(20);
    Vector3f rotationFrontRelativ;

    static float rotationS = 0;
    float length = 0.5f;
    public void move()
    {
        //rotationS = -40;
        direction.rotateY((float)Math.toRadians(rotationS));

        front.add(direction.mul(0.002f));

        front.sub(back, direction);
        direction.normalize();
        direction.mul(-1f);

        front.add(direction.mul(length), back);

        front.add(back, transformation.position);
        transformation.position.mul(0.5f);


        front.sub(back, direction);
        direction.normalize();
        transformation.quaternion.identity();
        transformation.quaternion.lookAlong(direction, up);
        transformation.quaternion.invert();
        transformation.calculateMatrix();
    }
}
