package org.example.sensors;

import org.example.*;
import org.example.jsonClasses.JsonSensor;
import org.example.virtualEnvironment.Environment;
import org.joml.Vector3f;

public class DistanceSensor extends Sensor {

    float maxDistance;
    float minDistance;
    int samplingRate;
    float fov;
    int sampleCountFov;
    private byte address;
    private long sensorExecuteTime;
    private long totalTime;
    private long delayInNanos;

    public DistanceSensor(JsonSensor jsonSensor, byte type) {
        super(jsonSensor, type);

        maxDistance = jsonSensor.max_distance * 100f;
        minDistance = jsonSensor.min_distance * 100f;
        samplingRate = jsonSensor.sampling_rate;
        fov = jsonSensor.fov_distance_sensor * ConstValues.DEGREES_TO_RADIANS;
        sampleCountFov = jsonSensor.sample_count_fov;
        address = jsonSensor.address;

        dataPackage = new DataPackage(4);
        dataPackage.setHeader(type, address);

        delayInNanos = 1000*1000*1000 / samplingRate;
    }

    float calculateDistance() {
        Vector3f pos = VectorMatrixPool.getVector3f();
        Vector3f direction = VectorMatrixPool.getVector3f();

        float distance = maxDistance;
        float rotations = 7.0f * 360.0f * ConstValues.DEGREES_TO_RADIANS;
        for(int i = 0; i < sampleCountFov; i++){
            float degree = rotations * ((float)i / sampleCountFov);
            float fovDegree = fov * ((float)i / sampleCountFov);

            //Default direction -> X+
            direction.set(1,0,0);
            direction.rotateY(fovDegree);
            direction.rotateX(degree);
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

        totalTime += nanos;

        while(sensorExecuteTime <= totalTime){
            sensorExecuteTime += delayInNanos;

            float distance = calculateDistance();
            //if(address == 5)
            //    System.out.println("Sensor distance: {}" + distance);

            ConstValues.floatToByteArray(distance / 100f, 0, dataPackage.customData);
            int maxQueueCount = 1 + samplingRate / 4;
            Schnittstelle.getInstance().senden(dataPackage, maxQueueCount);
        }
    }

    @Override
    public void destroy() {

    }
}
