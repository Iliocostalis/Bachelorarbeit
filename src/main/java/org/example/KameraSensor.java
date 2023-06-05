package org.example;

import org.example.assets.JsonObjektInstance;
import org.example.assets.JsonSensor;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL40.*;

public class KameraSensor extends Sensor{

    int width;
    int height;

    int PBO;
    byte[] data;
    private final int sensorInfoSize = 13;
    private int imageSize;






    private RenderTarget renderTarget;
    private Kamera kamera;



    public KameraSensor(JsonSensor jsonSensor, JsonObjektInstance jsonObjektInstance)
    {
        super(jsonObjektInstance);

        width = jsonSensor.resolution_width;
        height = jsonSensor.resolution_height;
        renderTarget = new RenderTarget(width, height, getColorFormat(jsonSensor.color_format));
        kamera = new Kamera(renderTarget);
        createPBO();

        data = new byte[imageSize + sensorInfoSize];
        ConstValues.intToByteArray(width*height*renderTarget.byteProPixel()+sensorInfoSize, 0, data);
        data[4] = 10;
        if(renderTarget.farben == RENDER_TARGET_COLOR_FORMAT.DEPTH8)
            data[4] += 1;

        ConstValues.intToByteArray(width, 5, data);
        ConstValues.intToByteArray(height, 9, data);
    }

    private RENDER_TARGET_COLOR_FORMAT getColorFormat(String colorFormat)
    {
        //if(true)
        //return RENDER_TARGET_COLOR_FORMAT.RGB;
        return switch (colorFormat)
        {
            case "BW" -> RENDER_TARGET_COLOR_FORMAT.SCHWARZ_WEISS;
            case "RGB" -> RENDER_TARGET_COLOR_FORMAT.RGB;
            case "BGR" -> RENDER_TARGET_COLOR_FORMAT.BGR;
            case "DEPTH8" -> RENDER_TARGET_COLOR_FORMAT.DEPTH8;
            case "DEPTH16" -> RENDER_TARGET_COLOR_FORMAT.DEPTH16;
            default -> RENDER_TARGET_COLOR_FORMAT.RGB;
        };
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
        //glFlush();

        glBindFramebuffer(GL_READ_FRAMEBUFFER, renderTarget.framebuffer);
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glReadPixels(0,0, width, height, GL_RED, GL_UNSIGNED_BYTE, 0);

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