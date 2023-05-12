package org.example;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL40;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL40.*;

public class Zwei_D_Kamera extends Sensor{

    int width;
    int height;

    int PBO;
    byte[] data;
    private final int sensorInfoSize = 9;
    private int imageSize;

    Zwei_D_Kamera_Farben farben;






    private RenderTarget renderTarget;
    private Kamera kamera;

    public Zwei_D_Kamera(Umgebung umgebung)
    {
        this.umgebung = umgebung;
        width = 300;
        height = 300;
        renderTarget = new RenderTarget(width, height, RENDER_TARGET_COLOR_FORMAT.RGB);
        kamera = new Kamera(renderTarget);
        createPBO();

        data = new byte[imageSize + sensorInfoSize];
        data[0] = 10;
        ConstValues.intToByteArray(width, 1, data);
        ConstValues.intToByteArray(height, 5, data);
    }

    void createPBO()
    {
        imageSize = width*height*renderTarget.byteProPixel();

        PBO = glGenBuffers();
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glBufferData(GL_PIXEL_PACK_BUFFER, imageSize, GL_STREAM_READ);
    }

    public void zerstoeren()
    {

    }

    private void sendImage()
    {
        Schnittstelle schnittstelle = Schnittstelle.getInstance();
        schnittstelle.senden(data);
    }

    Vector3f tmp = new Vector3f();
    @Override
    public void ausfuehren(float vergangeneZeit)
    {
        tmp.x = 2;
        tmp.y = 10;
        tmp.z = 0.5f;

        kamera.position.set(tmp);
        tmp.x = 2;
        tmp.y = 0;
        tmp.z = 1;
        kamera.lookAt.set(tmp);
        kamera.updateMatrix();

        kamera.updateMatrix();

        Renderer renderer = Renderer.getInstance();
        renderer.setKamera(kamera);
        umgebung.draw();
        glFlush();

        //read
        glBindFramebuffer(GL_READ_FRAMEBUFFER, renderTarget.framebuffer);
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glReadPixels(0,0, width, height, renderTarget.getOpenGlFormat(), GL_UNSIGNED_BYTE, 0);

        ByteBuffer byteBuffer = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY);
        byteBuffer.get(data, sensorInfoSize, imageSize);
        glUnmapBuffer(GL_PIXEL_PACK_BUFFER);

        sendImage();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
