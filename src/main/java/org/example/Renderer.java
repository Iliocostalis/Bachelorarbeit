package org.example;

import org.example.sensors.Kamera;
import org.example.sensors.RENDER_TARGET_COLOR_FORMAT;
import org.example.shader.DepthShader;
import org.example.shader.NormalerShader;
import org.example.shader.ShaderTyp;
import org.example.shader.TexturShader;
import org.example.virtualEnvironment.Mesh;
import org.example.virtualEnvironment.Objekt;

import static org.lwjgl.opengl.GL20.*;

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
                kamera.bindMatrixToShader(normalerShader.getProgram());
                kamera.bindMatrixToShader(texturShader.getProgram());
                break;
            case DEPTH8:
            case DEPTH16:
                kamera.bindMatrixToShader(depthShader.getProgram());
                glUniform3f(3, kamera.position.x, kamera.position.y, kamera.position.z);
                glUniform1f(4, kamera.getZFar());
                break;
            case BLACK_WHITE:
                break;
        }
    }

    public void draw(Objekt objekt)
    {
        Mesh mesh = objekt.mesh;

        switch (colorFormat)
        {
            case RGB:
            case BGR:
                if(mesh.getShaderTyp() == ShaderTyp.NORMAL)
                    normalerShader.draw(objekt, mesh);
                else
                    texturShader.draw(objekt, mesh);
                break;
            case DEPTH8:
            case DEPTH16:
                depthShader.draw(objekt, mesh);
                break;
            case BLACK_WHITE:
                break;
        }
    }
}
