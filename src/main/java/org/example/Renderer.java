package org.example;

import org.example.sensors.Kamera;
import org.example.sensors.RENDER_TARGET_COLOR_FORMAT;
import org.example.shader.*;
import org.example.virtualEnvironment.Mesh;
import org.example.virtualEnvironment.VirtualObject;

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
    BlackWhiteTexturShader blackWhiteTexturShader;
    BlackWhiteNormalShader blackWhiteNormalShader;

    DepthShader depthShader;

    Renderer()
    {
        normalerShader = new NormalerShader();
        texturShader = new TexturShader();
        blackWhiteTexturShader = new BlackWhiteTexturShader();
        blackWhiteNormalShader = new BlackWhiteNormalShader();
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
                kamera.bindMatrixToShader(blackWhiteNormalShader.getProgram());
                kamera.bindMatrixToShader(blackWhiteTexturShader.getProgram());
                break;
        }
    }

    public void draw(VirtualObject virtualObject)
    {
        Mesh mesh = virtualObject.mesh;

        switch (colorFormat)
        {
            case RGB:
            case BGR:
                if(mesh.getShaderTyp() == ShaderTyp.BASIC)
                    normalerShader.draw(virtualObject, mesh);
                else
                    texturShader.draw(virtualObject, mesh);
                break;
            case DEPTH8:
            case DEPTH16:
                depthShader.draw(virtualObject, mesh);
                break;
            case BLACK_WHITE:
                if(mesh.getShaderTyp() == ShaderTyp.BASIC)
                    blackWhiteNormalShader.draw(virtualObject, mesh);
                else
                    blackWhiteTexturShader.draw(virtualObject, mesh);
                break;
        }
    }
}
