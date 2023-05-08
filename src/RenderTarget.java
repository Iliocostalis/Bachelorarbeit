import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL40.*;

public class RenderTarget {
    int framebuffer;

    int width;
    int height;

    int colorTexture;
    int depthBuffer;

    RENDER_TARGET_COLOR_FORMAT farben;

    public RenderTarget(int width, int height, RENDER_TARGET_COLOR_FORMAT format)
    {
        this.width = width;
        this.height = height;
        this.farben = format;

        createFrameBuffer();
    }

    public RenderTarget(int width, int height, int framebufferId, RENDER_TARGET_COLOR_FORMAT format)
    {
        this.width = width;
        this.height = height;
        this.farben = format;

        framebuffer = framebufferId;
    }

    public void createFrameBuffer()
    {
        framebuffer = glGenFramebuffers();
        colorTexture = glGenTextures();
        depthBuffer = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        glBindTexture(GL_TEXTURE_2D, colorTexture);

        int farbformat;
        switch (farben)
        {
            case SCHWARZ_WEISS -> farbformat = GL_RED;
            case RGB -> farbformat = GL_RGB;
            case BGR -> farbformat = GL_BGR;
            case RGBA -> farbformat = GL_RGBA;
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

    public void zerstoeren()
    {

    }

    public int byteProPixel()
    {
        return switch (farben) {
            case SCHWARZ_WEISS -> 1;
            case RGB -> 3;
            case BGR -> 3;
            case RGBA -> 4;
            default -> 3;
        };
    }

    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glViewport(0,0, width, height);

        glClearColor(0,0.5f,1,1);
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
