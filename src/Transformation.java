import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Transformation {
    public Vector3f position = new Vector3f();
    public Quaternionf quaternion = new Quaternionf();
    public Matrix4f matrix = new Matrix4f();

    private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public void calculateMatrix()
    {
        quaternion.get(matrix);
        matrix.transformPosition(position);
        matrix.get(matrixBuffer);
    }

    public FloatBuffer getMatrix()
    {
        return matrixBuffer;
    }
}
