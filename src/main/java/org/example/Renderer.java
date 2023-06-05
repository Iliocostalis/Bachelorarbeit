package org.example;

import org.example.shader.DepthShader;
import org.lwjgl.opengl.GL40;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {

    private static Renderer renderer;

    public static Renderer getInstance()
    {
        if(renderer == null)
            renderer = new Renderer();

        return renderer;
    }

    RENDER_TARGET_COLOR_FORMAT colorFormat;

    NormalerShader normalerShader;
    TexturShader texturShader;

    DepthShader depthShader;

    Renderer()
    {
        normalerShader = new NormalerShader();
        texturShader = new TexturShader();
        depthShader = new DepthShader();
    }

    public void setKamera(Kamera kamera)
    {
        kamera.bindRenderTarget();

        colorFormat = kamera.getColorFormat();
        switch (colorFormat)
        {
            case RGB:
            case BGR:
            case RGBA:
                kamera.bindMatrixToShader(normalerShader.shaderProgram);
                kamera.bindMatrixToShader(texturShader.shaderProgram);
            case DEPTH8:
            case DEPTH16:
                kamera.bindMatrixToShader(depthShader.shaderProgram);
                glUniform3f(3, kamera.position.x, kamera.position.y, kamera.position.z);
                glUniform1f(4, kamera.getZFar());
                break;
            case SCHWARZ_WEISS:
                break;
        }
    }

    public void draw(Objekt objekt)
    {
        Mesh mesh = UmgebungsLader.getMesh(objekt.meshHash);

        switch (colorFormat)
        {
            case RGB:
            case BGR:
            case RGBA:
                if(mesh.getShaderTyp() == ShaderTyp.NORMAL)
                    normalerShader.draw(objekt, mesh);
                else
                    texturShader.draw(objekt, mesh);
                break;
            case DEPTH8:
            case DEPTH16:
                depthShader.draw(objekt, mesh);
                break;
            case SCHWARZ_WEISS:
                break;
        }
    }
}
