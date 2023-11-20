package org.example.sensors;

import static org.lwjgl.opengl.GL40.*;

public class RenderTarget {
    private int framebuffer;

    private int width;
    private int height;

    private int colorTexture;
    private int depthBuffer;

    private RENDER_TARGET_COLOR_FORMAT farben;

    private boolean createdResources;

    public RenderTarget(int width, int height, RENDER_TARGET_COLOR_FORMAT format) {
        this.width = width;
        this.height = height;
        this.farben = format;

        createFrameBuffer();
        createdResources = true;
    }

    public RenderTarget(int width, int height, int framebufferId, RENDER_TARGET_COLOR_FORMAT format) {
        this.width = width;
        this.height = height;
        this.farben = format;

        framebuffer = framebufferId;
        createdResources = false;
    }

    public void createFrameBuffer() {
        framebuffer = glGenFramebuffers();
        colorTexture = glGenTextures();
        depthBuffer = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        glBindTexture(GL_TEXTURE_2D, colorTexture);

        int format = getOpenGlFormat();
        int internalFormat = getOpenGlInternalFormat();
        int type = getOpenGlType();

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int byteProPixel() {
        return switch (farben) {
            case BLACK_WHITE -> 1;
            case RGB -> 3;
            case BGR -> 3;
            case DEPTH8 -> 1;
            case DEPTH16 -> 2;
            default -> 3;
        };
    }

    public int getOpenGlInternalFormat() {
        return switch (farben) {
            case BLACK_WHITE -> GL_R8;
            case RGB -> GL_RGB;
            case BGR -> GL_BGR;
            case DEPTH8 -> GL_R8;
            case DEPTH16 -> GL_R16;
            default -> GL_RGB;
        };
    }

    public int getOpenGlFormat() {
        return switch (farben) {
            case BLACK_WHITE -> GL_RED;
            case RGB -> GL_RGB;
            case BGR -> GL_BGR;
            case DEPTH8 -> GL_RED;
            case DEPTH16 -> GL_RED;
            default -> GL_RGB;
        };
    }

    public int getOpenGlType() {
        return switch (farben) {
            case BLACK_WHITE -> GL_UNSIGNED_BYTE;
            case RGB -> GL_UNSIGNED_BYTE;
            case BGR -> GL_UNSIGNED_BYTE;
            case DEPTH8 -> GL_UNSIGNED_BYTE;
            case DEPTH16 -> GL_UNSIGNED_SHORT;
            default -> GL_UNSIGNED_BYTE;
        };
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glViewport(0,0, width, height);

        glClearColor(0.6f,0.8f,0.95f,1);
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFramebuffer() {
        return framebuffer;
    }

    public RENDER_TARGET_COLOR_FORMAT getColorFormat() {
        return farben;
    }

    public void destroy() {
        if(createdResources) {
            glDeleteFramebuffers(framebuffer);
            glDeleteTextures(colorTexture);
            glDeleteRenderbuffers(depthBuffer);
        }
    }
}
