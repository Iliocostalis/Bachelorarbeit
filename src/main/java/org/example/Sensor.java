package org.example;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Sensor{
    public Vector3f position;
    public Quaternionf rotation;
    protected Umgebung umgebung;

    Sensor(Umgebung umgebung){
        this.umgebung = umgebung;
        position = new Vector3f();
        rotation = new Quaternionf();
    }

    public abstract void ausfuehren(float vergangeneZeit);

    public abstract void destroy();
}
