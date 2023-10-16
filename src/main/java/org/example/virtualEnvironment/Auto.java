package org.example.virtualEnvironment;

import org.example.Listener;
import org.example.jsonClasses.JsonCarNew;
import org.example.sensors.Sensor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Auto extends Objekt implements Listener {

    //Default direction -> X+

    public ArrayList<Sensor> sensoren;
    private Vector3f back = new Vector3f(0,0.1f,0);
    private Vector3f front = new Vector3f(1,0.1f,0);

    private Vector3f currentDirection = new Vector3f(1,0,0);

    private Vector3f direction = new Vector3f(1,0,0);
    private Vector3f directionFront = new Vector3f(1,0,0);

    private Vector3f vector3fTmp = new Vector3f();
    private Quaternionf quaternionTmp = new Quaternionf();

    private float steeringAngle = 0;
    private float speed = 0;

    private final float maxSteeringAngle;
    private final float maxSpeed;

    private final float length;

    private boolean skipTransformationUpdate;

    public Auto(JsonCarNew jsonCar, ArrayList<Sensor> sensoren)
    {
        super(jsonCar.object);
        this.sensoren = sensoren;
        this.maxSpeed = jsonCar.max_speed;
        this.maxSteeringAngle = jsonCar.max_steering_angle;

        transformation.addModifiedListener(this);

        skipTransformationUpdate = false;

        mesh.getSize(vector3fTmp);
        length = vector3fTmp.x;
    }

    public void update(long nanoseconds)
    {
        move(((float)nanoseconds) / (1000*1000*1000));

        for(Sensor sensor : sensoren)
        {
            sensor.updatePosition(transformation);
            sensor.ausfuehren(0);
        }
    }

    private void move(float deltaTime)
    {
        skipTransformationUpdate = true;

        //calculate front /back from direction and position
        currentDirection.mul(length*0.5f, vector3fTmp);

        transformation.getPosition().add(vector3fTmp, front);
        transformation.getPosition().sub(vector3fTmp, back);

        //transformation.getPosition(front);
        //front.add(vector3fTmp);
        //transformation.getPosition(back);
        //back.sub(vector3fTmp);

        // rotate direction
        currentDirection.rotateY((float)Math.toRadians(maxSteeringAngle * steeringAngle), direction);

        // move front (convert m/s to cm/s)
        front.add(direction.mul(speed * maxSpeed * 100.0f * deltaTime));

        // get new direction
        front.sub(back, direction);
        direction.normalize();
        currentDirection.set(direction);

        // calculate new back position
        direction.mul(length, vector3fTmp);
        front.sub(vector3fTmp, back);

        // calculate new position (center of front and back)
        front.add(back, vector3fTmp);
        vector3fTmp.mul(0.5f);
        transformation.setPosition(vector3fTmp);

        // calculate new rotation
        quaternionTmp.identity();
        quaternionTmp.rotateTo(directionFront, direction);
        transformation.setQuaternion(quaternionTmp);

        skipTransformationUpdate = false;
    }

    public void destroy()
    {
        for(Sensor sensor : sensoren)
            sensor.destroy();
    }

    public void setSpeed(float value)
    {
        speed = Math.max(-1.0f, Math.min(1.0f, value));
    }

    public void setSteeringAngle(float value)
    {
        steeringAngle = Math.max(-1.0f, Math.min(1.0f, value));
    }

    @Override
    public void notifyListener() {
        if(skipTransformationUpdate)
            return;

        directionFront.rotate(transformation.getQuaternion(), vector3fTmp);
        currentDirection.set(vector3fTmp);
    }
}