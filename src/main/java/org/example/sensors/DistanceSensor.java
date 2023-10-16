package org.example.sensors;

import org.example.*;
import org.example.jsonClasses.JsonSensorNew;
import org.example.virtualEnvironment.EnviromentLoader;
import org.example.virtualEnvironment.Objekt;
import org.example.virtualEnvironment.Umgebung;
import org.joml.Vector3f;

public class DistanceSensor extends Sensor {

    private Objekt debugObject;
    float maxDistance;
    float minDistance;
    int samplingRate;

    public DistanceSensor(JsonSensorNew jsonSensor, byte type) {
        super(jsonSensor, type);

        maxDistance = jsonSensor.max_distance * 100f;
        minDistance = jsonSensor.min_distance * 100f;
        samplingRate = jsonSensor.sampling_rate;

        dataPackage = new DataPackage(4);
        dataPackage.setHeader(type, address);

        debugObject = EnviromentLoader.getObjekt("default_Icosphere");
    }

    @Override
    public void ausfuehren(float vergangeneZeit)
    {
        Vector3f pos = VectorMatrixPool.getVector3f();

        Vector3f direction = VectorMatrixPool.getVector3f();
        direction.set(1,0,0);
        direction.rotate(rotation);

        Umgebung.umgebung.getRayIntersection(position, direction, maxDistance, pos);
        float distance = Vector3f.distance(position.x, position.y, position.z, pos.x, pos.y, pos.z);
        distance = Math.min(distance, maxDistance);
        distance = Math.max(distance, minDistance);

        debugObject.transformation.setPosition(pos);
        Umgebung.umgebung.debugObjekte.add(debugObject);

        ConstValues.intToByteArray(Float.floatToIntBits(distance / 100f), 0, dataPackage.customData);
        int maxQueueCount = 1 + samplingRate / 4;
        Schnittstelle.getInstance().senden(dataPackage, maxQueueCount);
    }

    @Override
    public void destroy() {

    }
}
