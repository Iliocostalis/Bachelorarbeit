package org.example.sensors;

import org.example.*;
import org.example.jsonClasses.JsonSensor;
import org.example.virtualEnvironment.Transformation;
import org.example.virtualEnvironment.Environment;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL40.*;

public class KameraSensor extends Sensor {

    private final int maxQueueCount = 4;
    private int width;
    private int height;
    private int framerate;
    private boolean flipImageY;

    private int PBO;
    private int imageSize;

    private DataPackage dataPackage;


    private ByteBuffer byteBuffer;

    private RenderTarget renderTarget;
    private Kamera kamera;
    private long sensorExecuteTime;
    private long totalTime;
    private long delayInNanos;


    public KameraSensor(JsonSensor jsonSensor, byte type) {
        super(jsonSensor, type);

        width = jsonSensor.resolution_width;
        height = jsonSensor.resolution_height;
        renderTarget = new RenderTarget(width, height, getColorFormat(jsonSensor.color_format));
        framerate = jsonSensor.framerate;
        delayInNanos = 1000*1000*1000 / framerate;
        flipImageY = jsonSensor.flip_image_y;

        if(jsonSensor.min_distance == 0)
            jsonSensor.min_distance = 0.1f;

        if(type == DataPackage.TYPE_3D_CAM)
            kamera = new Kamera(renderTarget, jsonSensor.fov, jsonSensor.min_distance*100, jsonSensor.max_distance*100, flipImageY);
        else
            kamera = new Kamera(renderTarget, jsonSensor.fov, 0.1f, 20000.f, flipImageY);

        imageSize = width*height*renderTarget.byteProPixel();

        createPBO();

        dataPackage = new DataPackage(imageSize + 4 + 4 + 1);
        dataPackage.setHeader(type, address);

        ConstValues.intToByteArray(width, 0, dataPackage.customData);
        ConstValues.intToByteArray(height, 4, dataPackage.customData);
        dataPackage.customData[8] = colorFormatToByte(renderTarget.getColorFormat());
    }

    private byte colorFormatToByte(RENDER_TARGET_COLOR_FORMAT colorFormat) {
        return switch (colorFormat)
        {
            case BLACK_WHITE -> DataPackage.COLOR_FORMAT_BW;
            case RGB -> DataPackage.COLOR_FORMAT_RGB;
            case BGR -> DataPackage.COLOR_FORMAT_BGR;
            case DEPTH8 -> DataPackage.COLOR_FORMAT_D8;
            case DEPTH16 -> DataPackage.COLOR_FORMAT_D16;
        };
    }

    private RENDER_TARGET_COLOR_FORMAT getColorFormat(String colorFormat) {
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

    private void createPBO() {
        PBO = glGenBuffers();
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glBufferData(GL_PIXEL_PACK_BUFFER, imageSize, GL_STREAM_READ);
    }

    private void sendImage() {
        Schnittstelle schnittstelle = Schnittstelle.getInstance();
        schnittstelle.senden(dataPackage, maxQueueCount);
    }

    @Override
    public void updatePosition(Transformation transformationCar) {
        super.updatePosition(transformationCar);
        kamera.updateMatrix(position, rotation);
    }

    @Override
    public void execute(long nanos) {
        totalTime += nanos;

        while(sensorExecuteTime <= totalTime) {
            sensorExecuteTime += delayInNanos;

            Renderer renderer = Renderer.getInstance();
            renderer.setKamera(kamera);
            Environment.environment.draw();

            glBindFramebuffer(GL_READ_FRAMEBUFFER, renderTarget.getFramebuffer());
            glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
            glReadPixels(0, 0, width, height, renderTarget.getOpenGlFormat(), renderTarget.getOpenGlType(), 0);

            byteBuffer = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY, imageSize, byteBuffer);
            if (byteBuffer != null)
                byteBuffer.get(dataPackage.customData, 9, imageSize);
            glUnmapBuffer(GL_PIXEL_PACK_BUFFER);

            sendImage();

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
    }

    @Override
    public void destroy() {
        renderTarget.destroy();
        byteBuffer.clear();
        glDeleteBuffers(PBO);
    }
}