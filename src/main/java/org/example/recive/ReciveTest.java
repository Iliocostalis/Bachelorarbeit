package org.example.recive;

import org.example.ConstValues;
import org.example.sensors.DataPackage;
import org.example.sensors.RENDER_TARGET_COLOR_FORMAT;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ReciveTest {
    // The window handle
    private long window;
    private Socket clientSocket;
    private OutputStream os;
    private InputStream is;

    int width = 721;
    int height = 481;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);



        try {
            clientSocket = new Socket("127.0.0.1", 26134);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setWindowSize(int width, int height)
    {
        glFlush();

        this.width = width;
        this.height = height;
        imageSize = width*height*3;
        byteBuffer = BufferUtils.createByteBuffer(imageSize);
        image = new byte[imageSize];

        glfwSetWindowSize(window, width, height);
    }

    int imageSize;
    ByteBuffer byteBuffer;
    byte[] image;

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        /*final int imageSize = width*height*3;
        final int readSize = width*height*3 + 9;
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(imageSize);
        byte[] image = new byte[readSize];*/

        //int PBO = glGenBuffers();
        //glBindBuffer(GL_PIXEL_UNPACK_BUFFER, PBO);
        //glBufferData(GL_PIXEL_UNPACK_BUFFER, imageSize, GL_STREAM_DRAW);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        DataPackage dataPackage = new DataPackage(32);
        byte[] data = new byte[1];
        byte[] dataTmp = new byte[1024*1024];

        long time = System.currentTimeMillis();
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            try {
                if(is.available() != 0)
                {
                    int read = 0;
                    while(read < dataPackage.header.length)
                        read += is.read(dataPackage.header, read, dataPackage.header.length - read);

                    int type = dataPackage.header[0];
                    int address = dataPackage.header[1];
                    int readSize = ConstValues.byteArrayToInt(2, dataPackage.header);

                    if(!(type == DataPackage.TYPE_2D_CAM || type == DataPackage.TYPE_3D_CAM))
                    {
                        read = 0;
                        while(read < readSize)
                            read += is.read(dataTmp, read, readSize - read);

                        continue;
                    }

                    if(data.length != readSize)
                        data = new byte[readSize];

                    read = 0;
                    while(read < readSize)
                        read += is.read(data, read, readSize - read);

                    int rWidth = ConstValues.byteArrayToInt(0, data);
                    int rHeight = ConstValues.byteArrayToInt(4, data);
                    int colorFormat = data[8];

                    if(rWidth != width || rHeight != height)
                    {
                        setWindowSize(rWidth, rHeight);
                    }

                    if(type == DataPackage.TYPE_2D_CAM)
                    {
                        if(colorFormat == DataPackage.COLOR_FORMAT_BW)
                        {
                            for (int i = 0; i < width*height; i++) {
                                image[i*3] = data[i+9];
                                image[i*3+1] = data[i+9];
                                image[i*3+2] = data[i+9];
                            }
                        }
                        else
                        {
                            for(int i = 0; i < imageSize; i++)
                            {
                                image[i] = data[i+9];
                            }
                        }
                    }
                    else if(type == DataPackage.TYPE_3D_CAM) {

                        if(colorFormat == DataPackage.COLOR_FORMAT_D8)
                        {
                            for (int i = 0; i < width*height; i++) {
                                image[i*3] = data[i+9];
                                image[i*3+1] = data[i+9];
                                image[i*3+2] = data[i+9];
                            }
                        }
                        else if(colorFormat == DataPackage.COLOR_FORMAT_D16)
                        {
                            for (int i = 0; i < width*height; i++) {
                                image[i*3] = data[i*2+10];
                                image[i*3+1] = data[i*2+10];
                                image[i*3+2] = data[i*2+10];
                            }
                        }
                    }

                    byteBuffer.put(0, image, 0, imageSize);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //for(int i = 0; i < 4*300*5; i++)
            //{
            //    image[i] = (byte) 0xff;
            //}
            //byteBuffer.put(0, image);

            if(byteBuffer != null)
                glDrawPixels(width, height, GL_RGB, GL_UNSIGNED_BYTE, byteBuffer);

            glfwSwapBuffers(window); // swap the color buffers
            System.out.println(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new ReciveTest().run();
    }
}
