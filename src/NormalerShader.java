import org.lwjgl.opengl.GL40;

import static org.lwjgl.opengl.GL40.*;


public class NormalerShader implements Shader{

    String vertexShaderSource = "#version 460 core\n" +
            "layout (location = 0) in vec3 aPos;\n"+
            "layout(location = 0) uniform mat4 projection;\n"+
            "layout(location = 1) uniform mat4 view;\n"+
            "layout(location = 2) uniform mat4 model;\n"+
            "out vec3 oPos;\n"+
            "void main()\n"+
            "{\n"+
            "   gl_Position = projection * view * model * vec4(aPos, 1.0);\n"+
            "   oPos = vec3(aPos.x+1.0, aPos.y+1.0, aPos.z+1.0);\n"+
            "}";

    String fragmentShaderSource = "#version 460 core\n" +
            "in vec3 oPos;\n"+
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = vec4(oPos.x, oPos.y, oPos.z, 1.0f);\n" +
            "} ";

    int shaderProgram;

    NormalerShader() {
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

    }

    @Override
    public void draw(Objekt objekt) {
        glUseProgram(shaderProgram);

        glBindVertexArray(objekt.mesh.VAO);
        objekt.transformation.calculateMatrix();
        glUniformMatrix4fv(2, false, objekt.transformation.getMatrix());
        glDrawArrays(GL_TRIANGLES, 0, objekt.mesh.vertexCount);
    }
}
