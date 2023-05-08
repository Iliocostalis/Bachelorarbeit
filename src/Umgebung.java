import java.util.ArrayList;

public class Umgebung {
    Auto auto;
    ArrayList<Objekt> objekte = new ArrayList<>();
    Kamera kamera;

    Umgebung()
    {
        Objekt objekt = new Objekt();

        float[] positionen = {0f,0f,0f, 1f,0f,0f, 0f,1f,0f};
        objekt.mesh = new Mesh(positionen, null);

        objekte.add(objekt);
        //mesh.

        RenderTarget renderTarget = new RenderTarget(300, 300, 0, RENDER_TARGET_COLOR_FORMAT.RGBA);
        kamera = new Kamera(renderTarget);
    }

    public void aktualisieren()
    {

    }

    public void visualisieren()
    {
        Renderer renderer = Renderer.getInstance();

        renderer.setKamera(kamera);

        for (Objekt o : objekte) {
            renderer.draw(o);
        }
    }
}
