package org.example;

import org.joml.Vector3f;
import java.util.ArrayList;

public class Umgebung {
    Auto auto;
    ArrayList<Objekt> objekte = new ArrayList<>();
    Kamera kamera;

    Umgebung()
    {
        RenderTarget renderTarget = new RenderTarget(300, 300, 0, RENDER_TARGET_COLOR_FORMAT.RGBA);
        kamera = new Kamera(renderTarget);

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
        objekte.add(objekt2);
    }

    public void aktualisieren()
    {

    }

    Vector3f rot = new Vector3f();
    public void visualisieren()
    {
        Renderer renderer = Renderer.getInstance();

        renderer.setKamera(kamera);

        rot.y = 0.001f;

        int i = 0;
        for (Objekt o : objekte) {
            if(i++ == 0)
            {
                o.transformation.quaternion.rotateAxis(0.001f, 0,1,0);
                //o.transformation.quaternion.rotateXYZ(rot.x, rot.y, rot.z);
                o.transformation.position.x += 0.002f;
            }

            if(o.transformation.position.x > 1)
                o.transformation.position.x -= 2;
            o.transformation.calculateMatrix();
            renderer.draw(o);
        }
    }
}
