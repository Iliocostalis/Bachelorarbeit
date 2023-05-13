package org.example;

import org.joml.Vector3f;
import java.util.ArrayList;

public class Umgebung {
    public static Umgebung umgebung;

    public Auto auto;
    public ArrayList<Objekt> objekte = new ArrayList<>();
    public Kamera kamera;

    private Zwei_D_Kamera zwei_d_kamera;

    Umgebung()
    {
        RenderTarget renderTarget = new RenderTarget(720, 480, 0, RENDER_TARGET_COLOR_FORMAT.RGBA);
        kamera = new Kamera(renderTarget);

        zwei_d_kamera = new Zwei_D_Kamera(this);

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
        zwei_d_kamera.ausfuehren(0f);
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
}
