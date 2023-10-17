package org.example.sensors;

import org.example.jsonClasses.JsonSensorNew;

public class SensorCreator {

    public static Sensor create(JsonSensorNew sensor)
    {
        switch(sensor.type)
        {
            case "2D_CAM":
                return new KameraSensor(sensor, DataPackage.TYPE_2D_CAM);
            case "3D_CAM":
                return new KameraSensor(sensor, DataPackage.TYPE_3D_CAM);
            case "DISTANCE_SENSOR":
                return new DistanceSensor(sensor, DataPackage.TYPE_DISTANCE_SENSOR);
            case "LIDAR":
                return new LiDARSensor(sensor, DataPackage.TYPE_LIDAR);

        }
        return null;
    }
}
