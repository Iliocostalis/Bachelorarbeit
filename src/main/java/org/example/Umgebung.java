package org.example;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;

public class Umgebung {
    public static Umgebung umgebung;

    public String sceneName;

    public Auto auto;
    public ArrayList<Objekt> objekte = new ArrayList<>();
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

        /*
        Objekt objekt = new Objekt();
        float[] positionen = {0f,0f,0f, 1f,0f,0f, 1f,1f,0f};
        objekt.mesh = new Mesh(positionen, null, 0);
        objekte.add(objekt);





        Objekt objektTxt = new Objekt();
        int txId = TextureLoader.getInstance().loadTexture("assets\\images\\test.png");
        float[] positionentxt = {0f,0f,0f, 1f,0f,0f, 1f,1f,0f,    0f,0f,0f, 1f,1f,0f, 0f,1f,0f};
        float[] tx = {0f,0f, 1f,0f, 1f,1f,    0f,0f, 1f,1f, 0f,1f};
        objektTxt.mesh = new Mesh(positionentxt, tx, txId);
        objekte.add(objektTxt);


        Objekt objekt2 = new Objekt();
        float[] positionen2 = {-2f,0f,0f, -1f,0f,0f, -1f,1f,0f};
        objekt2.mesh = new Mesh(positionen2, null, 0);
        objekte.add(objekt2);*/
    }

    public void aktualisieren()
    {
        VectorMatrixPool.returnAll();
        if(auto != null)
            auto.update();

        //zwei_d_kamera.ausfuehren(0f);

        distanceSensor.offsetPosition.set(auto.transformation.getPosition());
        distanceSensor.offsetRotation.y += 10;
        distanceSensor.offsetRotation.set(auto.transformation.getQuaternion());
        //distanceSensor.ausfuehren(0f);
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

        draw();
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

                float distance = intersection.lengthSquared();
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
