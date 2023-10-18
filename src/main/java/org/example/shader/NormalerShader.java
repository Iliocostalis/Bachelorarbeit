package org.example.shader;

import org.example.virtualEnvironment.Mesh;
import org.example.virtualEnvironment.Objekt;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL40.*;


public class NormalerShader implements Shader {

    private final String vertexShaderSource = "#version 460 core\n" +
            "layout (location = 0) in vec3 vPos;\n"+
            "layout (location = 1) in vec3 vNormals;\n"+
            "layout(location = 0) uniform mat4 projection;\n"+
            "layout(location = 1) uniform mat4 view;\n"+
            "layout(location = 2) uniform mat4 model;\n"+
            "out vec3 normals;\n"+
            "void main()\n"+
            "{\n"+
            "   vec4 position = model * vec4(vPos, 1.0);\n"+
            "   gl_Position = projection * view * position;\n"+
            "   normals = vNormals;\n"+
            "}";

    private final String fragmentShaderSource = "#version 460 core\n" +
            "layout(location = 3) uniform vec3 vColor;\n"+
            "in vec3 normals;\n"+
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "   vec3 lightPos = vec3(0.0, 10000, 0.0);\n"+
            "   vec3 lightDir = normalize(lightPos);\n"+
            "   float diff = max(dot(normals, lightDir), 0.0);\n"+
            "   float diffuseScale = 0.7;\n"+
            "   float ambientStrength = 0.3;\n"+
            "   float brightness = diff * diffuseScale + ambientStrength;\n"+
            "   FragColor = brightness * vec4(vColor, 1.0);\n" +
            "} ";

    private int shaderProgram;

    private FloatBuffer matrixBuffer;

    public NormalerShader() {
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
    public int getProgram() {
        return shaderProgram;
    }

    @Override
    public void draw(Objekt objekt, Mesh mesh) {
        glUseProgram(shaderProgram);

        objekt.transformation.getMatrix().get(matrixBuffer);

        glBindVertexArray(mesh.getVAO());
        glUniformMatrix4fv(2, false, matrixBuffer);
        glUniform3f(3, mesh.r, mesh.g, mesh.b);
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());
    }
}
