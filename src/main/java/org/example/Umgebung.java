package org.example;

import org.joml.Vector3f;
import java.util.ArrayList;

public class Umgebung {
    public static Umgebung umgebung;

    public Auto auto;
    public ArrayList<Objekt> objekte = new ArrayList<>();
    public Kamera kamera;

    Umgebung()
    {
        RenderTarget renderTarget = new RenderTarget(720, 480, 0, RENDER_TARGET_COLOR_FORMAT.RGBA);
        kamera = new Kamera(renderTarget);

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
        auto.move();
    }

    Vector3f tmp = new Vector3f();
    public void visualisieren()
    {
        Renderer renderer = Renderer.getInstance();

        //kamera.transformation.position.x += 0.001f;
        //if(kamera.transformation.position.x > 4)
        //    kamera.transformation.position.x -= 4;
        tmp.x = 2;
        tmp.y = 6;
        tmp.z = -5;

        kamera.position.set(tmp);
        kamera.updateMatrix();

        renderer.setKamera(kamera);

        for (Objekt o : objekte) {
            renderer.draw(o);
        }

        renderer.draw(auto);
    }
}
