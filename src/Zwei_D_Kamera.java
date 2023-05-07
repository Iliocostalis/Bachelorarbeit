import org.lwjgl.opengl.GL40;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL40.*;

public class Zwei_D_Kamera extends Sensor{

    String vertexShaderSource = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n"+
            "uniform vec3 aPosOffset;\n"+
            "out vec3 oPos;\n"+
            "void main()\n"+
            "{\n"+
            "   gl_Position = vec4(aPos.x + aPosOffset.x, aPos.y, aPos.z, 1.0);\n"+
            "   oPos = vec3(aPos.x+1.0, aPos.y+1.0, aPos.z+1.0);\n"+
            "}\0";

    String fragmentShaderSource = "#version 330 core\n" +
            "in vec3 oPos;\n"+
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = vec4(oPos.x, oPos.y, oPos.z, 1.0f);\n" +
            "} ";

    int shaderProgram;
    int VAO;
    int uniformID;

    public Zwei_D_Kamera()
    {

    }

    public void erstellen()
    {
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

        uniformID = glGetUniformLocation(shaderProgram,"aPosOffset");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);


        glUseProgram(shaderProgram);

        /*
        // 0. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        // 1. then set the vertex attributes pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3, 0);
        glEnableVertexAttribArray(0);*/

        int VBO = glGenBuffers();
        //glBindBuffer(GL_ARRAY_BUFFER, VBO);
        //glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


        VAO = glGenVertexArrays();
        // 1. bind Vertex Array Object
        glBindVertexArray(VAO);
        // 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        // 3. then set our vertex attributes pointers
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);
        glEnableVertexAttribArray(0);


        createFrameBuffer();
        createPBO();

        if(sendImage)
        {
            try {
                serverSocket = new ServerSocket(25131);
                clientSocket = serverSocket.accept();
                os = clientSocket.getOutputStream();
                is = clientSocket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void zerstoeren()
    {

    }

    @Override
    public void ausfuehren(float vergangeneZeit)
    {
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glViewport(0,0, width, height);

        glClearColor(0,0.5f,1,1);
        glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(shaderProgram);

        glBindVertexArray(VAO);
        glUniform3f(uniformID, val, 0,0);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glFlush();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        //read
        glBindFramebuffer(GL_READ_FRAMEBUFFER, FBO);
        glBindBuffer(GL_PIXEL_PACK_BUFFER, PBO);
        glReadPixels(0,0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        ByteBuffer byteBuffer = glMapBuffer(GL_PIXEL_PACK_BUFFER, GL_READ_ONLY);
        byteBuffer.get(data);
        glUnmapBuffer(GL_PIXEL_PACK_BUFFER);



        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
