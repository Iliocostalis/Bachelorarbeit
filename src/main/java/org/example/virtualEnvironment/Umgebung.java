package org.example.virtualEnvironment;

import org.example.Renderer;
import org.example.VectorMatrixPool;
import org.example.sensors.*;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Umgebung {
    public static Umgebung umgebung;

    public String sceneName;

    public Auto auto;
    public ArrayList<Objekt> objekte = new ArrayList<>();
    public ArrayList<Objekt> debugObjekte = new ArrayList<>();
    public Kamera kamera;
    private RenderTarget renderTarget;

    Umgebung(String sceneName)
    {
        this.sceneName = sceneName;
        renderTarget = new RenderTarget(720, 480, 0, RENDER_TARGET_COLOR_FORMAT.RGB);
        kamera = new Kamera(renderTarget);
    }

    public void aktualisieren(long nanoseconds)
    {
        VectorMatrixPool.returnAll();
        if(auto != null)
            auto.update(nanoseconds);
    }

    Vector3f tmp = new Vector3f();

    public void draw()
    {
        Renderer renderer = Renderer.getInstance();

        for (Objekt o : objekte) {
            renderer.draw(o);
        }

        if(auto != null)
            renderer.draw(auto);
    }

    public void visualisieren()
    {
        Renderer renderer = Renderer.getInstance();

        tmp.x = 200;
        tmp.y = 300;
        tmp.z = -250;

        kamera.position.set(tmp);
        tmp.x = 0;
        tmp.y = 0;
        tmp.z = 0;
        kamera.lookAt.set(tmp);
        kamera.updateMatrix();

        renderer.setKamera(kamera);

        //System.out.println(auto.transformation.getPosition().distance(kamera.position));
        draw();

        for (Objekt o : debugObjekte) {
            renderer.draw(o);
        }
        debugObjekte.clear();
    }

    public void getRayIntersection(Vector3f rayOrigin, Vector3f rayDirection, float maxDistance, Vector3f outPosition)
    {
        Vector3f rayDirectionNormalized = VectorMatrixPool.getVector3f();
        Vector3f intersection = VectorMatrixPool.getVector3f();
        Vector3f intersectionOffset = VectorMatrixPool.getVector3f();

        rayDirection.normalize(rayDirectionNormalized);

        rayDirectionNormalized.mulAdd(maxDistance, rayOrigin, outPosition);

        float distanceSquared = maxDistance * maxDistance;

        for (Objekt o : objekte) {
            boolean intersecting = o.isRayIntersecting(rayOrigin, rayDirection);
            if(intersecting)
            {
                intersection.set(o.getRayIntersectionPosition());
                intersection.sub(rayOrigin, intersectionOffset);

                float distance = intersectionOffset.lengthSquared();
                if(distance < distanceSquared)
                {
                    distanceSquared = distance;
                    outPosition.set(intersection);
                }
            }
        }
    }

    public void destroy()
    {
        if(auto != null)
            auto.destroy();

        renderTarget.destroy();
        objekte.clear();
    }
}
