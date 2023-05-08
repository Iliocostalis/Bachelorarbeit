package org.example;

import org.lwjgl.opengl.GL40;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL40.*;

public class Zwei_D_Kamera extends Sensor{

    int width;
    int height;

    int FBO;
    int PBO;
    int colorTexture;
    int depthBuffer;
    int uniformID;
    byte[] data;

    Zwei_D_Kamera_Farben farben;

    public Zwei_D_Kamera()
    {

    }

    public void erstellen()
    {
        data = new byte[width*height*byteProPixel()];
        createFrameBuffer();
        createPBO();
    }

    public void createFrameBuffer()
    {
        FBO = glGenFramebuffers();
        colorTexture = glGenTextures();
        depthBuffer = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, FBO);

        glBindTexture(GL_TEXTURE_2D, colorTexture);

        int farbformat;
        switch (farben)
        {
            case SCHWARZ_WEISS -> farbformat = GL_RED;
            case RGB -> farbformat = GL_RGB;
            case BGR -> farbformat = GL_BGR;
            default -> farbformat = GL_RGB;
        }
        glTexImage2D(GL_TEXTURE_2D, 0, farbformat, width, height, 0, farbformat, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    void createPBO()
    {
        PBO = glGenBuffers();
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glBufferData(GL_PIXEL_PACK_BUFFER, width*height*byteProPixel(), GL_STREAM_READ);
    }

    public void zerstoeren()
    {

    }

    int byteProPixel()
    {
        switch (farben)
        {
            case SCHWARZ_WEISS:
                return 1;
            case RGB:
                return 3;
            case BGR:
                return 3;
            default:
                return 3;
        }
    }

    @Override
    public void ausfuehren(float vergangeneZeit)
    {
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glViewport(0,0, width, height);

        glClearColor(0,0.5f,1,1);
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);



        //glUniform3f(uniformID, val, 0,0);

        //draw all

        glFlush();

        //read
        glBindFramebuffer(GL_READ_FRAMEBUFFER, FBO);
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glReadPixels(0,0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        ByteBuffer byteBuffer = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY);
        byteBuffer.get(data);
        glUnmapBuffer(GL_PIXEL_PACK_BUFFER);

        Schnittstelle.getInstance();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
