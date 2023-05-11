package org.example;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TexturShader implements Shader{
    String vertexShaderSource = "#version 460 core\n" +
            "layout (location = 0) in vec3 aPos;\n"+
            "layout (location = 1) in vec2 aTex;\n"+
            "layout(location = 0) uniform mat4 projection;\n"+
            "layout(location = 1) uniform mat4 view;\n"+
            "layout(location = 2) uniform mat4 model;\n"+
            "out vec2 TexCoord;\n"+
            "void main()\n"+
            "{\n"+
            "   gl_Position = projection * view * model * vec4(aPos, 1.0);\n"+
            "   TexCoord = aTex;\n"+
            "}";

    String fragmentShaderSource = "#version 460 core\n" +
            "in vec2 TexCoord;\n"+
            "out vec4 FragColor;\n" +
            "uniform sampler2D ourTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = texture(ourTexture, TexCoord);\n" +
            "} ";

    int shaderProgram;

    TexturShader() {
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

        glGetShaderiv(shaderProgram, GL_LINK_STATUS, success);

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

        glBindVertexArray(mesh.getVAO());
        glUniformMatrix4fv(2, false, objekt.transformation.getMatrix());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mesh.getTextureId());
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());
    }
}
