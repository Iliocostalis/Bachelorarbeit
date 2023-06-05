package org.example.shader;

import org.example.*;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL40.*;


public class DepthShader implements Shader {

    String vertexShaderSource = "#version 460 core\n" +
            "layout (location = 0) in vec3 vPos;\n"+
            "layout (location = 1) in vec3 vNormals;\n"+
            "layout(location = 0) uniform mat4 projection;\n"+
            "layout(location = 1) uniform mat4 view;\n"+
            "layout(location = 2) uniform mat4 model;\n"+
            "out vec3 mPosition;\n"+
            "void main()\n"+
            "{\n"+
            "   vec4 position = model * vec4(vPos, 1.0);\n"+
            "   mPosition = vec3(position);\n"+
            "   gl_Position = projection * view * position;\n"+
            "}";

    String fragmentShaderSource = "#version 460 core\n" +
            "layout(location = 3) uniform vec3 eyePosition;\n"+
            "layout(location = 4) uniform float maxDistance;\n"+
            "in vec3 mPosition;\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "   float depth = distance(eyePosition, mPosition);\n"+
            "   depth = depth / maxDistance;\n"+
            "   FragColor = vec4(depth, depth, depth, 1.0);\n" +
            "} ";

    public int shaderProgram;

    private FloatBuffer matrixBuffer;

    public DepthShader() {
        matrixBuffer = BufferUtils.createFloatBuffer(16);

        int vertexShader;
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        int[] success = new int[1];
        glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success);

        if(success[0] != 1)
        {
            String log = glGetShaderInfoLog(vertexShader);
            System.out.println(log);
        }

        int fragmentShader;
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, success);

        if(success[0] != 1)
        {
            String log = glGetShaderInfoLog(fragmentShader);
            System.out.println(log);
        }

        shaderProgram = glCreateProgram();

        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glGetProgramiv(shaderProgram, GL_LINK_STATUS, success);

        if(success[0] != 1)
        {
            String log = glGetShaderInfoLog(shaderProgram);
            System.out.println(log);
        }

        //uniformID = glGetUniformLocation(shaderProgram,"aPosOffset");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glUseProgram(shaderProgram);
    }

    @Override
    public void draw(Objekt objekt, Mesh mesh) {
        glUseProgram(shaderProgram);

        objekt.transformation.getMatrix().get(matrixBuffer);

        glBindVertexArray(mesh.getVAO());
        glUniformMatrix4fv(2, false, matrixBuffer);
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());
    }
}
