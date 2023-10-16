package org.example.sensors;

import org.example.*;
import org.example.jsonClasses.JsonSensorNew;
import org.example.virtualEnvironment.Transformation;
import org.example.virtualEnvironment.Umgebung;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL40.*;

public class KameraSensor extends Sensor {

    private final int maxQueueCount = 4;
    private int width;
    private int height;

    private int PBO;
    private int imageSize;

    private DataPackage dataPackage;


    private ByteBuffer byteBuffer;

    private RenderTarget renderTarget;
    private Kamera kamera;


    public KameraSensor(JsonSensorNew jsonSensor, byte type)
    {
        super(jsonSensor, type);

        width = jsonSensor.resolution_width;
        height = jsonSensor.resolution_height;
        renderTarget = new RenderTarget(width, height, getColorFormat(jsonSensor.color_format));
        kamera = new Kamera(renderTarget);

        imageSize = width*height*renderTarget.byteProPixel();

        createPBO();

        dataPackage = new DataPackage(imageSize + 4 + 4 + 1);
        dataPackage.setHeader(type, address);

        ConstValues.intToByteArray(width, 0, dataPackage.customData);
        ConstValues.intToByteArray(height, 4, dataPackage.customData);
        dataPackage.customData[8] = 0;
    }

    private RENDER_TARGET_COLOR_FORMAT getColorFormat(String colorFormat)
    {
        return switch (colorFormat)
        {
            case "BW" -> RENDER_TARGET_COLOR_FORMAT.BLACK_WHITE;
            case "RGB" -> RENDER_TARGET_COLOR_FORMAT.RGB;
            case "BGR" -> RENDER_TARGET_COLOR_FORMAT.BGR;
            case "DEPTH8" -> RENDER_TARGET_COLOR_FORMAT.DEPTH8;
            case "DEPTH16" -> RENDER_TARGET_COLOR_FORMAT.DEPTH16;
            default -> RENDER_TARGET_COLOR_FORMAT.RGB;
        };
    }

    private void createPBO()
    {
        PBO = glGenBuffers();
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glBufferData(GL_PIXEL_PACK_BUFFER, imageSize, GL_STREAM_READ);
    }

    private void sendImage()
    {
        Schnittstelle schnittstelle = Schnittstelle.getInstance();
        schnittstelle.senden(dataPackage, maxQueueCount);
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

        glBindFramebuffer(GL_READ_FRAMEBUFFER, renderTarget.getFramebuffer());
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glReadPixels(0,0, width, height, renderTarget.getOpenGlFormat(), GL_UNSIGNED_BYTE, 0);

        byteBuffer = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY, imageSize, byteBuffer);
        if(byteBuffer != null)
            byteBuffer.get(dataPackage.customData, 9, imageSize);
        glUnmapBuffer(GL_PIXEL_PACK_BUFFER);

        sendImage();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void destroy() {
        renderTarget.destroy();
        byteBuffer.clear();
    }
}