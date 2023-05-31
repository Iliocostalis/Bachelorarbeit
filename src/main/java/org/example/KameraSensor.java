package org.example;

import org.example.assets.JsonObjektInstance;
import org.example.assets.JsonSensor;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL30.*;

public class KameraSensor extends Sensor{

    int width;
    int height;

    int PBO;
    byte[] data;
    private final int sensorInfoSize = 9;
    private int imageSize;






    private RenderTarget renderTarget;
    private Kamera kamera;



    public KameraSensor(JsonSensor jsonSensor, JsonObjektInstance jsonObjektInstance)
    {
        super(jsonObjektInstance);

        width = jsonSensor.resolution_width;
        height = jsonSensor.resolution_height;
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

    private void sendImage()
    {
        Schnittstelle schnittstelle = Schnittstelle.getInstance();
        schnittstelle.senden(data);
    }

    @Override
    public void updatePosition(Transformation transformationCar)
    {
        super.updatePosition(transformationCar);
        kamera.updateMatrix(position, rotation);
    }

    @Override
    public void ausfuehren(float vergangeneZeit)
    {
        Renderer renderer = Renderer.getInstance();
        renderer.setKamera(kamera);
        Umgebung.umgebung.draw();
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

    @Override
    public void destroy() {
        renderTarget.destroy();
    }
}