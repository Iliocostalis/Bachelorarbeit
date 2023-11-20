package org.example.virtualEnvironment;

import org.example.Listener;
import org.example.VectorMatrixPool;
import org.example.jsonClasses.JsonCar;
import org.example.sensors.Sensor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Auto extends VirtualObject implements Listener {

    //Default direction -> X+

    public ArrayList<Sensor> sensors;
    private Vector3f currentDirection = new Vector3f(1,0,0);
    private Vector3f direction = new Vector3f(1,0,0);
    private Vector3f directionFront = new Vector3f(1,0,0);

    private float steeringAngle = 0;
    private float speed = 0;

    private final float maxSteeringAngle;
    private final float maxSpeed;
    private final float wheelbase;

    private boolean skipTransformationUpdate;

    public Auto(JsonCar jsonCar, ArrayList<Sensor> sensors) {
        super(jsonCar.object);
        this.sensors = sensors;
        this.maxSpeed = jsonCar.max_speed;
        this.maxSteeringAngle = jsonCar.max_steering_angle;
        this.wheelbase = jsonCar.wheelbase;

        transformation.addModifiedListener(this);
        skipTransformationUpdate = false;
        notifyListener();
    }

    public void update(long nanoseconds) {
        move(((float)nanoseconds) / (1000*1000*1000));

        for(Sensor sensor : sensors) {
            sensor.updatePosition(transformation);
            sensor.execute(nanoseconds);
        }
    }

    private void moveForward(float distance) {
        //calculate front /back from direction and position
        Vector3f halfCarLength = VectorMatrixPool.getVector3f();
        currentDirection.mul(wheelbase*0.5f, halfCarLength);

        Vector3f front = VectorMatrixPool.getVector3f();
        Vector3f back = VectorMatrixPool.getVector3f();
        transformation.getPosition().add(halfCarLength, front);
        transformation.getPosition().sub(halfCarLength, back);

        // rotate direction
        currentDirection.rotateY((float)Math.toRadians(maxSteeringAngle * steeringAngle), direction);

        // move front (convert m/s to cm/s)
        front.add(direction.mul(distance));

        // get new direction
        front.sub(back, direction);
        direction.normalize();
        currentDirection.set(direction);

        // calculate new back position
        Vector3f carLengthDirection = VectorMatrixPool.getVector3f();
        direction.mul(wheelbase, carLengthDirection);
        front.sub(carLengthDirection, back);

        // calculate new position (center of front and back)
        Vector3f newPosition = VectorMatrixPool.getVector3f();
        front.add(back, newPosition);
        newPosition.mul(0.5f);
        transformation.setPosition(newPosition);

        // calculate new rotation
        Quaternionf quaternion = VectorMatrixPool.getQuaternionf();
        quaternion.identity();
        quaternion.rotateTo(directionFront, direction);
        transformation.setQuaternion(quaternion);
    }


    private void move(float deltaTime) {
        skipTransformationUpdate = true;

        // Step 1 // car drives to slow in curves -> more steps approach the target speed better
        Vector3f positionStart = VectorMatrixPool.getVector3f();
        positionStart.set(transformation.getPosition());

        float distance = speed * maxSpeed * 100.0f * deltaTime;
        moveForward(distance);

        Vector3f positionNew = VectorMatrixPool.getVector3f();
        positionNew.set(transformation.getPosition());

        float distanceMoved = positionStart.distance(positionNew);
        float diff = Math.max(distance - distanceMoved, 0.0f);

        // Step 2
        distance = diff;
        positionStart.set(transformation.getPosition());
        moveForward(distance);
        positionNew.set(transformation.getPosition());
        distanceMoved = positionStart.distance(positionNew);
        diff = Math.max(distance - distanceMoved, 0.0f);

        // Step 3
        distance = diff;
        positionStart.set(transformation.getPosition());
        moveForward(distance);
        positionNew.set(transformation.getPosition());
        distanceMoved = positionStart.distance(positionNew);
        diff = Math.max(distance - distanceMoved, 0.0f);

        skipTransformationUpdate = false;
    }

    public void destroy() {
        for(Sensor sensor : sensors)
            sensor.destroy();
    }

    public void setSpeed(float value) {
        speed = Math.max(-1.0f, Math.min(1.0f, value));
    }

    public void setSteeringAngle(float value) {
        steeringAngle = Math.max(-1.0f, Math.min(1.0f, value));
    }

    @Override
    public void notifyListener() {
        if(skipTransformationUpdate)
            return;

        directionFront.rotate(transformation.getQuaternion(), currentDirection);
    }
}