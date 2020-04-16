package com.jade;

import com.jade.events.KeyListener;
import com.jade.events.MouseListener;
import com.jade.scenes.MenuScene;
import com.jade.scenes.Scene;
import com.jade.scenes.WorldScene;
import com.jade.util.Constants;
import com.jade.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.GraphicsEnvironment;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance = null;

    private long glfwWindow = 0;
    private int width, height, halfWidth, halfHeight;
    private String title;
    private static Scene currentScene = null;

    public static void framebufferSizeCallback(long window, int width, int height) {
        Window.getWindow().setWidth(width);
        Window.getWindow().setHeight(height);
        //Window.getWindow().setAspect(width / height);
        if (Window.getScene() != null) {
            glViewport(0, 0, width, height);
            Window.getScene().camera().adjustPerspective();
        }
    }

    private Window() {
        this.width = Constants.INITIAL_WINDOW_WIDTH;
        this.height = Constants.INITIAL_WINDOW_HEIGHT;
        this.title = Constants.WINDOW_TITLE;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new MenuScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new WorldScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
    }

    public static Window getWindow() {
        if (Window.instance == null) {
            Window.instance = new Window();
        }

        return Window.instance;
    }

    public static void stop() {
        glfwSetWindowShouldClose(getWindow().glfwWindow, true);
    }

    public static Scene getScene() {
        return getWindow().currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetFramebufferSizeCallback(glfwWindow, Window::framebufferSizeCallback);
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Window.changeScene(0);
    }

    private void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            glClearColor(Constants.WINDOW_CLEAR_COLOR.x, Constants.WINDOW_CLEAR_COLOR.y, Constants.WINDOW_CLEAR_COLOR.z, Constants.WINDOW_CLEAR_COLOR.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
                currentScene.render();
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getAspect() {
        return (float)this.width / (float)this.height;
    }

    public void setWidth(float val) {
        this.width = (int)val;
    }

    public void setHeight(float val) {
        this.height = (int)val;
    }
}
