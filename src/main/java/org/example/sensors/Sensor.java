package org.example.sensors;

import org.example.ConstValues;
import org.example.virtualEnvironment.Transformation;
import org.example.jsonClasses.JsonSensorNew;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Sensor{

    public Vector3f offsetPosition;
    public Quaternionf offsetRotation;
    public Vector3f position;
    public Quaternionf rotation;

    protected byte address;
    protected byte type;
    protected DataPackage dataPackage;

    public Sensor(){
        offsetPosition = new Vector3f();
        offsetRotation = new Quaternionf();
        position = new Vector3f();
        rotation = new Quaternionf();
    }

    public Sensor(JsonSensorNew jsonSensor, byte type){
        this.address = jsonSensor.address;
        this.type = type;

        offsetPosition = new Vector3f();
        offsetRotation = new Quaternionf();
        position = new Vector3f();
        rotation = new Quaternionf();
        offsetPosition.x = jsonSensor.position[0];
        offsetPosition.y = jsonSensor.position[1];
        offsetPosition.z = jsonSensor.position[2];
        offsetRotation.rotationXYZ(jsonSensor.rotation[0] * ConstValues.DEGREES_TO_RADIANS, jsonSensor.rotation[1] * ConstValues.DEGREES_TO_RADIANS, jsonSensor.rotation[2] * ConstValues.DEGREES_TO_RADIANS);
    }

    public void updatePosition(Transformation transformationCar)
    {
        position.set(offsetPosition);
        position.rotate(transformationCar.getQuaternion());
        position.add(transformationCar.getPosition());

        rotation.set(offsetRotation);
        transformationCar.getQuaternion().mul(rotation, rotation);
        //rotation.mul(transformationCar.getQuaternion());
    }

    public abstract void ausfuehren(long nanos);

    public abstract void destroy();
}
