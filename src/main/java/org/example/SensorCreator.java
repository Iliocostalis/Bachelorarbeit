package org.example;

import org.example.assets.JsonObjektInstance;
import org.example.assets.JsonSensor;

public class SensorCreator {
    public static Sensor create(JsonSensor sensor, JsonObjektInstance sensorInstance)
    {
        switch(sensor.type)
        {
            case "2DCAM":
                return new KameraSensor(sensor, sensorInstance);

        }
        return null;
    }
}
