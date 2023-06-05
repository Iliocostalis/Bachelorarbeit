package org.example;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;

public class Umgebung {
    public static Umgebung umgebung;

    public String sceneName;

    public Auto auto;
    public ArrayList<Objekt> objekte = new ArrayList<>();
    public ArrayList<Objekt> debugObjekte = new ArrayList<>();
    public Kamera kamera;
    private RenderTarget renderTarget;

    private Zwei_D_Kamera zwei_d_kamera;
    private DistanceSensor distanceSensor;

    Umgebung(String sceneName)
    {
        this.sceneName = sceneName;
        renderTarget = new RenderTarget(720, 480, 0, RENDER_TARGET_COLOR_FORMAT.RGBA);
        kamera = new Kamera(renderTarget);

        zwei_d_kamera = new Zwei_D_Kamera();
        distanceSensor = new DistanceSensor();
    }

    public void aktualisieren()
    {
        VectorMatrixPool.returnAll();
        if(auto != null)
            auto.update();

        //zwei_d_kamera.ausfuehren(0f);

        distanceSensor.position.set(auto.transformation.getPosition());
        distanceSensor.position.y += 5;
        distanceSensor.rotation.set(auto.transformation.getQuaternion());
        distanceSensor.ausfuehren(0f);
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

        tmp.x = 100;
        tmp.y = 200;
        tmp.z = -150;

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

        zwei_d_kamera.destroy();
        distanceSensor.destroy();
    }
}
