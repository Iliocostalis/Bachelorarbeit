package org.example.sensors;

import org.example.ConstValues;
import org.example.Schnittstelle;
import org.example.VectorMatrixPool;
import org.example.jsonClasses.JsonSensorNew;
import org.example.virtualEnvironment.EnviromentLoader;
import org.example.virtualEnvironment.Objekt;
import org.example.virtualEnvironment.Umgebung;
import org.joml.Vector3f;

public class LiDARSensor extends Sensor {

    private Objekt debugObject;
    float maxDistance;
    float minDistance;
    int samplingRate;

    float rotationsPerSec;

    long rotationTimeNano;
    long totalSteps;

    public LiDARSensor(JsonSensorNew jsonSensor, byte type) {
        super(jsonSensor, type);

        maxDistance = jsonSensor.max_distance * 100f;
        minDistance = jsonSensor.min_distance * 100f;
        samplingRate = jsonSensor.sampling_rate;
        rotationsPerSec = jsonSensor.rotations_per_second;

        dataPackage = new DataPackage(4*2);
        dataPackage.setHeader(type, address);

        debugObject = EnviromentLoader.getObjekt("default_Icosphere");
    }

    @Override
    public void ausfuehren(long nanos) {
        Vector3f pos = VectorMatrixPool.getVector3f();
        Vector3f direction = VectorMatrixPool.getVector3f();

        rotationTimeNano += nanos;

        long nanosPerStep = (1000*1000*1000 / samplingRate);
        int steps = (int)(rotationTimeNano / nanosPerStep);
        rotationTimeNano -= steps * nanosPerStep;
        float degreesPerStep = (rotationsPerSec / samplingRate) * 360f;

        for(int i = 0; i < steps; i++)
        {
            float angle = (degreesPerStep * totalSteps) % 360;
            totalSteps += 1;

            direction.set(1, 0, 0);
            direction.rotateAxis(angle * ConstValues.DEGREES_TO_RADIANS,0,1,0);
            direction.rotate(rotation);

            Umgebung.umgebung.getRayIntersection(position, direction, maxDistance, pos);
            float distance = Vector3f.distance(position.x, position.y, position.z, pos.x, pos.y, pos.z);
            distance = Math.min(distance, maxDistance);
            distance = Math.max(distance, minDistance);

            debugObject.transformation.setPosition(pos);

            ConstValues.intToByteArray(Float.floatToIntBits(distance / 100f), 0, dataPackage.customData);
            ConstValues.intToByteArray(Float.floatToIntBits(angle), 4, dataPackage.customData);
            int maxQueueCount = 1 + samplingRate / 4;
            Schnittstelle.getInstance().senden(dataPackage, maxQueueCount);
        }
        Umgebung.umgebung.debugObjekte.add(debugObject);
    }

    @Override
    public void destroy() {

    }
}