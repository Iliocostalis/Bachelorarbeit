package org.example;

import org.example.virtualEnvironment.Environment;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final long window;
    private boolean isOpen;

    private long nanosecondsRemaining;

    Window(int width, int height) {
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

            if(key == GLFW_KEY_W && action == GLFW_PRESS)
                Environment.environment.auto.setSpeed(1f);
            if(key == GLFW_KEY_S && action == GLFW_PRESS)
                Environment.environment.auto.setSpeed(-1f);
            if(key == GLFW_KEY_W && action == GLFW_RELEASE)
                Environment.environment.auto.setSpeed(0f);
            if(key == GLFW_KEY_S && action == GLFW_RELEASE)
                Environment.environment.auto.setSpeed(0f);

            if(key == GLFW_KEY_D && action == GLFW_PRESS)
                Environment.environment.auto.setSteeringAngle(-1f);
            if(key == GLFW_KEY_A && action == GLFW_PRESS)
                Environment.environment.auto.setSteeringAngle(1f);
            if(key == GLFW_KEY_A && action == GLFW_RELEASE)
                Environment.environment.auto.setSteeringAngle(0f);
            if(key == GLFW_KEY_D && action == GLFW_RELEASE)
                Environment.environment.auto.setSteeringAngle(0f);
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

        isOpen = true;

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);
        //glEnable(GL_FRAMEBUFFER_SRGB);
    }

    void update(long nanoseconds) {
        if(!isOpen)
            return;

        long nanosecondDelay = 16666*1000; // update window 60 times per second
        nanosecondsRemaining -= nanoseconds;

        if(nanosecondsRemaining > 0)
            return;

        while(nanosecondsRemaining <= 0)
            nanosecondsRemaining += nanosecondDelay;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        if(glfwWindowShouldClose(window)) {
            close();
            return;
        }

        Environment.environment.visualize();

        glfwSwapBuffers(window); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    void close() {
        glFlush();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        isOpen = false;
    }

    public boolean isOpen() {
        return this.isOpen;
    }
}
