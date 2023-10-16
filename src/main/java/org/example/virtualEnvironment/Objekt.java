package org.example.virtualEnvironment;

import org.example.jsonClasses.JsonObjectNew;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Objekt {
    public Transformation transformation;

    public Mesh mesh;

    private Collider collider;

    public Objekt(Mesh mesh)
    {
        this.mesh = mesh;
        transformation = new Transformation();
        collider = new Collider(this);
    }

    public Objekt(JsonObjectNew jsonObjekt)
    {
        mesh = new Mesh(jsonObjekt);
        transformation = new Transformation(jsonObjekt);
        collider = new Collider(this);
    }

    public boolean isRayIntersecting(Vector3f rayOrigin, Vector3f rayDirection)
    {
        return collider.isRayIntersecting(rayOrigin, rayDirection);
    }

    public Vector3fc getRayIntersectionPosition()
    {
        return collider.getIntersectionPosition();
    }
}
