package org.example;

import org.example.assets.JsonObjektInstance;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Sensor{
    public Vector3f offsetPosition;
    public Quaternionf offsetRotation;
    public Vector3f position;
    public Quaternionf rotation;

    Sensor(){
        offsetPosition = new Vector3f();
        offsetRotation = new Quaternionf();
        position = new Vector3f();
        rotation = new Quaternionf();
    }

    Sensor(JsonObjektInstance jsonObjektInstance){
        offsetPosition = new Vector3f();
        offsetRotation = new Quaternionf();
        position = new Vector3f();
        rotation = new Quaternionf();
        offsetPosition.x = jsonObjektInstance.position[0];
        offsetPosition.y = jsonObjektInstance.position[1];
        offsetPosition.z = jsonObjektInstance.position[2];
        offsetRotation.rotationXYZ(jsonObjektInstance.rotation[0] * ConstValues.DEGREES_TO_RADIANS, jsonObjektInstance.rotation[1] * ConstValues.DEGREES_TO_RADIANS, jsonObjektInstance.rotation[2] * ConstValues.DEGREES_TO_RADIANS);
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

    public abstract void ausfuehren(float vergangeneZeit);

    public abstract void destroy();
}
