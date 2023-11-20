package org.example;

import org.example.sensors.Camera;
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

    NormalShader normalShader;
    TexturShader texturShader;
    BlackWhiteTexturShader blackWhiteTexturShader;
    BlackWhiteNormalShader blackWhiteNormalShader;

    DepthShader depthShader;

    Renderer()
    {
        normalShader = new NormalShader();
        texturShader = new TexturShader();
        blackWhiteTexturShader = new BlackWhiteTexturShader();
        blackWhiteNormalShader = new BlackWhiteNormalShader();
        depthShader = new DepthShader();
    }

    public void setKamera(Camera camera)
    {
        camera.bindRenderTarget();

        colorFormat = camera.getColorFormat();
        switch (colorFormat)
        {
            case RGB:
            case BGR:
                camera.bindMatrixToShader(normalShader.getProgram());
                camera.bindMatrixToShader(texturShader.getProgram());
                break;
            case DEPTH8:
            case DEPTH16:
                camera.bindMatrixToShader(depthShader.getProgram());
                glUniform3f(3, camera.position.x, camera.position.y, camera.position.z);
                glUniform1f(4, camera.getZFar());
                break;
            case BLACK_WHITE:
                camera.bindMatrixToShader(blackWhiteNormalShader.getProgram());
                camera.bindMatrixToShader(blackWhiteTexturShader.getProgram());
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
                    normalShader.draw(virtualObject, mesh);
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
