package org.example;

public abstract class Sensor{
    public Position position;
    public Rotation rotation;
    protected Umgebung umgebung;
    public abstract void ausfuehren(float vergangeneZeit);
}
