package org.example.sensors;

import org.example.ConstValues;
import org.example.Schnittstelle;
import org.example.VectorMatrixPool;
import org.example.jsonClasses.JsonSensor;
import org.example.virtualEnvironment.EnvironmentLoader;
import org.example.virtualEnvironment.VirtualObject;
import org.example.virtualEnvironment.Environment;
import org.joml.Vector3f;

public class LiDARSensor extends Sensor {

    private VirtualObject debugObject;
    float maxDistance;
    float minDistance;
    int samplingRate;

    float rotationsPerSec;

    long rotationTimeNano;
    long totalSteps;
    float fov;

    int sampleCountFov;

    public LiDARSensor(JsonSensor jsonSensor, byte type) {
        super(jsonSensor, type);

        maxDistance = jsonSensor.max_distance * 100f;
        minDistance = jsonSensor.min_distance * 100f;
        samplingRate = jsonSensor.sampling_rate;
        rotationsPerSec = jsonSensor.rotations_per_second;
        fov = jsonSensor.fov_distance_sensor * ConstValues.DEGREES_TO_RADIANS;
        sampleCountFov = jsonSensor.sample_count_fov;

        dataPackage = new DataPackage(4*2);
        dataPackage.setHeader(type, address);

        debugObject = EnvironmentLoader.getVirtualObject("default_Icosphere");


    }

    float calculateDistance(float rotationY) {
        Vector3f pos = VectorMatrixPool.getVector3f();
        Vector3f direction = VectorMatrixPool.getVector3f();

        float distance = maxDistance;
        float rotations = (float)Math.toRadians(7.0f*360.0f);
        for(int i = 0; i < sampleCountFov; i++){
            float degree = rotations * ((float)i / sampleCountFov);
            float fovDegree = fov * ((float)i / sampleCountFov);

            //Default direction -> X+
            direction.set(1,0,0);
            direction.rotateY(fovDegree);
            direction.rotateX(degree);
            direction.rotateY(rotationY);
            direction.rotate(rotation);

            Environment.environment.getRayIntersection(position, direction, maxDistance, pos);
            float distanceCurrent = Vector3f.distance(position.x, position.y, position.z, pos.x, pos.y, pos.z);
            distanceCurrent = Math.max(distanceCurrent, minDistance);
            distance = Math.min(distanceCurrent, distance);

            //debugObject = EnviromentLoader.getObjekt("default_Icosphere");
            //debugObject.transformation.setPosition(pos);
            //Umgebung.umgebung.debugObjekte.add(debugObject);
        }

        return distance;
    }

    @Override
    public void execute(long nanos) {
        rotationTimeNano += nanos;

        long nanosPerStep = (1000*1000*1000 / samplingRate);
        int steps = (int)(rotationTimeNano / nanosPerStep);
        rotationTimeNano -= steps * nanosPerStep;
        float degreesPerStep = (rotationsPerSec / samplingRate) * 360f;

        for(int i = 0; i < steps; i++)
        {
            float angle = (degreesPerStep * totalSteps) % 360;
            float rotationY = angle * ConstValues.DEGREES_TO_RADIANS;
            totalSteps += 1;

            float distance = calculateDistance(rotationY);

            ConstValues.intToByteArray(Float.floatToIntBits(distance / 100f), 0, dataPackage.customData);
            ConstValues.intToByteArray(Float.floatToIntBits(angle), 4, dataPackage.customData);
            int maxQueueCount = 1 + samplingRate / 4;
            Schnittstelle.getInstance().senden(dataPackage, maxQueueCount);
        }
        Environment.environment.debugObjects.add(debugObject);
    }

    @Override
    public void destroy() {

    }
}