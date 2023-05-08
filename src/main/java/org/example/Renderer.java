package org.example;

import org.lwjgl.opengl.GL40;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
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

    NormalerShader normalerShader;
    TexturShader texturShader;

    Renderer()
    {
        normalerShader = new NormalerShader();
        texturShader = new TexturShader();
    }

    public void setKamera(Kamera kamera)
    {
        kamera.bindRenderTarget();
        kamera.bindMatrixToShader(normalerShader.shaderProgram);
        kamera.bindMatrixToShader(texturShader.shaderProgram);
    }

    public void draw(Objekt objekt)
    {
        switch (objekt.getShaderTyp())
        {
            case NORMAL -> normalerShader.draw(objekt);
            case MIT_TEXTUR -> texturShader.draw(objekt);
        }


    }
}
