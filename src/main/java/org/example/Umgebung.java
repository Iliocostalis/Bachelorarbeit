package org.example;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;

public class Umgebung {
    public static Umgebung umgebung;

    public Auto auto;
    public ArrayList<Objekt> objekte = new ArrayList<>();
    public Kamera kamera;

    private Zwei_D_Kamera zwei_d_kamera;
    private DistanceSensor distanceSensor;

    Umgebung()
    {
        RenderTarget renderTarget = new RenderTarget(720, 480, 0, RENDER_TARGET_COLOR_FORMAT.RGBA);
        kamera = new Kamera(renderTarget);

        zwei_d_kamera = new Zwei_D_Kamera(this);
        distanceSensor = new DistanceSensor(this);

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
        auto.move();
        zwei_d_kamera.ausfuehren(0f);
        distanceSensor.ausfuehren(0f);
    }

    Vector3f tmp = new Vector3f();

    public void draw()
    {
        Renderer renderer = Renderer.getInstance();

        for (Objekt o : objekte) {
            renderer.draw(o);
        }
        renderer.draw(auto);
    }

    public void visualisieren()
    {
        Renderer renderer = Renderer.getInstance();

        tmp.x = 2;
        tmp.y = 4;
        tmp.z = -3;

        kamera.position.set(tmp);
        tmp.x = 2;
        tmp.y = 0;
        tmp.z = 1;
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
}
