import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Kamera {
    int width;
    int height;

    int FBO;
    int colorTexture;
    int depthBuffer;
    int uniformID;

    Matrix4f projection;
    Matrix4f view;
    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);


    RenderTarget renderTarget;

    Kamera(RenderTarget renderTarget)
    {
        this.renderTarget = renderTarget;
    }

    public void bindRenderTarget()
    {
        renderTarget.bind();
    }

    public void bindMatrixToShader()
    {
        projection.get(matrixBuffer);
        glUniformMatrix4fv(0, false, matrixBuffer);

        view.get(matrixBuffer);
        glUniformMatrix4fv(1, false, matrixBuffer);
    }
}
