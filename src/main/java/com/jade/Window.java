package com.jade;

import com.jade.events.KeyListener;
import com.jade.events.MouseListener;
import com.jade.renderer.Texture;
import com.jade.scenes.*;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.Time;
import imgui.*;
import imgui.callbacks.ImStrConsumer;
import imgui.callbacks.ImStrSupplier;
import imgui.enums.*;
import imgui.gl3.ImGuiImplGl3;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance = null;

    private long glfwWindow = 0;
    private int width, height, halfWidth, halfHeight;
    private String title;
    private static Scene currentScene = null;
    private static boolean cursorIsLocked = false;

    private int fboID;
    private Texture framebufferTex;

    // =================================================================
    // IMGUI stuff
    // =================================================================
    // LWJGL3 rendered itself (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    // Those are used to track window size properties
    private final int[] winWidth = new int[1];
    private final int[] winHeight = new int[1];
    private final int[] fbWidth = new int[1];
    private final int[] fbHeight = new int[1];

    // For mouse tracking
    private final double[] mousePosX = new double[1];
    private final double[] mousePosY = new double[1];

    // Mouse cursors provided by GLFW
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    // Local variables for application goes here
    private final String imguiDemoLink = "https://raw.githubusercontent.com/ocornut/imgui/v1.75/imgui_demo.cpp"; // Link to put into clipboard
    private final byte[] testPayload = "Test Payload".getBytes(); // Test data for payload. Should be represented as raw byt array.
    private String dropTargetText = "Drop Here";
    private float[] backgroundColor = new float[]{0.5f, 0, 0}; // To modify background color dynamically
    private int clickCount = 0;
    private final ImString resizableStr = new ImString(5);
    private final ImBool showDemoWindow = new ImBool();
    private int dukeTexture;
    private ImVec2 windowSize = new ImVec2(); // Vector to store "Custom Window" size
    private ImVec2 windowPos = new ImVec2(); // Vector to store "Custom Window" position


    public static void framebufferSizeCallback(long window, int width, int height) {
        Window.getWindow().setWidth(width);
        Window.getWindow().setHeight(height);
        //Window.getWindow().setAspect(width / height);
        if (Window.getScene() != null) {
            glViewport(0, 0, 3840, 2160);
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
                if (currentScene != null)
                    currentScene.reset();
                currentScene = new MenuScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                if (currentScene != null)
                    currentScene.reset();
                currentScene = new WorldScene();
                currentScene.init();
                currentScene.start();
                break;
            case 2:
                if (currentScene != null)
                    currentScene.reset();
                currentScene = new TestScene();
                currentScene.init();
                currentScene.start();
                break;
            case 3:
                if (currentScene != null)
                    currentScene.reset();
                currentScene = new TestScene2D();
                currentScene.init();
                currentScene.start();
                break;
            case 4:
                if (currentScene != null)
                    currentScene.reset();
                currentScene = new TestScene3D();
                currentScene.init();
                currentScene.start();
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

    public static boolean windowCreated() {
        return Window.instance != null;
    }

    public static void stop() {
        glfwSetWindowShouldClose(getWindow().glfwWindow, true);
    }

    public static Scene getScene() {
        return getWindow().currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        initGlfw();
        initImGui();
        Window.changeScene(4);
        loop();
        destroyImGui();
        destroyGlfw();
    }

    private void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void destroyGlfw() {
        glDeleteFramebuffers(fboID);

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ImGui provides 3 different color schemas for styling. We will use the classic one here.
        // Try others with ImGui.styleColors*() methods.
        ImGui.styleColorsClassic();

        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null); // We don't want to save .ini file
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
        io.setBackendPlatformName("imgui_java_impl_glfw"); // For clarity reasons
        io.setBackendRendererName("imgui_java_impl_lwjgl"); // For clarity reasons

        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
        io.setKeyMap(keyMap);

        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

        // ------------------------------------------------------------
        // Here goes GLFW callbacks to update user input in Dear ImGui

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));


            KeyListener.keyCallback(w, key, scancode, action, mods);
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse()) {
                ImGui.setWindowFocus(null);
                MouseListener.mouseButtonCallback(w, button, action, mods);
            }
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
            if (!io.getWantCaptureMouse()) {
                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
            }
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                return glfwGetClipboardString(glfwWindow);
            }
        });

        // ------------------------------------------------------------
        // Fonts configuration

        // -------------------
        // Fonts merge example

        final ImFontAtlas fontAtlas = io.getFonts();

        final ImFontConfig fontConfig = new ImFontConfig(); // Keep in mind that creation of the ImFontConfig will allocate native memory
//        fontConfig.setMergeMode(true); // All fonts added while this mode is turned on will be merged with the previously added font
        fontConfig.setPixelSnapH(true);
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic()); // Additional glyphs could be added like this or in addFontFrom*() methods

        // We merge font loaded from resources with the default one. Thus we will get an absent cyrillic glyphs
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("basis33.ttf"), 16, fontConfig);
        fontAtlas.addFontFromFileTTF("C:/Windows/Fonts/SegoeUI.ttf", 32, fontConfig);

        // Disable merged mode and add all other fonts normally
        fontConfig.setMergeMode(false);
        fontConfig.setPixelSnapH(false);

        // ------------------------------
        // Fonts from file/memory example

        fontConfig.setRasterizerMultiply(1.2f); // This will make fonts a bit more readable

        // First of all we add a default font, which is 'ProggyClean.ttf, 13px'
        fontAtlas.addFontDefault(fontConfig);

        // We can add new fonts directly from file
//        fontAtlas.addFontFromFileTTF("src/test/resources/DroidSans.ttf", 13, fontConfig);
//        fontAtlas.addFontFromFileTTF("src/test/resources/DroidSans.ttf", 14, fontConfig);

        // Or directly from memory
//        fontConfig.setName("Roboto-Regular.ttf, 13px"); // This name will be displayed in Style Editor
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), 13, fontConfig);
//        fontConfig.setName("Roboto-Regular.ttf, 14px"); // We can apply a new config value every time we add a new font
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), 14, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // IMPORTANT!!!
        // Method initializes renderer itself.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init();
    }

    private void initGlfw() {
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

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glViewport(0, 0, 3840, 2160);

        // Initialize frame buffer
        createFrameBuffer();
    }

    private void loop() {
        float time = 0;
        float dt;

        while (!glfwWindowShouldClose(glfwWindow)) {
            float currentTime = (float)glfwGetTime();
            dt = currentTime - time;
            time = currentTime;
            if (dt < (1f / 60f) || dt <= 0) {
                if (dt <= 0) {
                    //System.out.println("FPS: '" + (1f / dt) + "'. Missed target frame rate.");
                }
                dt = 1f / 60f;
            }

            DebugDraw.beginFrame();

            glBindFramebuffer(GL_FRAMEBUFFER, fboID);
            glClearColor(Constants.WINDOW_CLEAR_COLOR.x, Constants.WINDOW_CLEAR_COLOR.y, Constants.WINDOW_CLEAR_COLOR.z, Constants.WINDOW_CLEAR_COLOR.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            currentScene.update(dt);
            currentScene.render();

            //glDisable(GL_DEPTH_TEST);
            DebugDraw.endFrame();
            //glEnable(GL_DEPTH_TEST);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glClearColor(Constants.WINDOW_CLEAR_COLOR.x, Constants.WINDOW_CLEAR_COLOR.y, Constants.WINDOW_CLEAR_COLOR.z, Constants.WINDOW_CLEAR_COLOR.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // ============================================================================================================
            // ImGUI stuff
            // ============================================================================================================
            // Get window size properties and mouse position
            glfwGetWindowSize(glfwWindow, winWidth, winHeight);
            glfwGetFramebufferSize(glfwWindow, fbWidth, fbHeight);
            glfwGetCursorPos(glfwWindow, mousePosX, mousePosY);

            // IMPORTANT!!
            // We SHOULD call those methods to update ImGui state for current frame
            final ImGuiIO io = ImGui.getIO();
            io.setDisplaySize(winWidth[0], winHeight[0]);
            io.setDisplayFramebufferScale((float) fbWidth[0] / winWidth[0], (float) fbHeight[0] / winHeight[0]);
            io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
            io.setDeltaTime(dt);

            // Update mouse cursor
            final int imguiCursor = ImGui.getMouseCursor();
            glfwSetCursor(glfwWindow, mouseCursors[imguiCursor]);

            // IMPORTANT!!
            // Any Dear ImGui code SHOULD go between NewFrame()/Render() methods
            ImGui.newFrame();
            //showUi();
            currentScene.imgui();
            ImGui.render();

            // After ImGui#render call we provide draw data into LWJGL3 renderer.
            // At that moment ImGui will be rendered to the current OpenGL context.
            imGuiGl3.render(ImGui.getDrawData());
            // ============================================================================================================
            // ============================================================================================================

            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            // Poll events
            glfwPollEvents();
        }
    }

    private void createFrameBuffer() {
        // Generate framebuffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // Create texture to render data too and attach it to framebuffer
        this.framebufferTex = new Texture(3840, 2160);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.framebufferTex.getID(), 0);

        // Create renderbuffer to store depth_stencil info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, 3840, 2160);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer is not complete.";
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFramebufferTexID() {
        return this.fboID;
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

    public static void lockCursor() {
        if (!cursorIsLocked) {
            // Lock cursor
            glfwSetInputMode(getWindow().glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            cursorIsLocked = true;
        }
    }

    public static void unlockCursor() {
        if (cursorIsLocked) {
            // Lock cursor
            glfwSetInputMode(getWindow().glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            cursorIsLocked = false;
        }
    }

    private void showUi() {
        ImGui.setNextWindowSize(600, 300, ImGuiCond.Once);
        ImGui.setNextWindowPos(10, 10, ImGuiCond.Once);

        ImGui.begin("Custom window");  // Start Custom window

        // Example of how to draw an image in the bottom-right corner of the window
//        ImGui.getWindowSize(windowSize);
//        ImGui.getWindowPos(windowPos);
//        final float xPoint = windowPos.x + windowSize.x - 100;
//        final float yPoint = windowPos.y + windowSize.y;
//        ImGui.getWindowDrawList().addImage(dukeTexture, xPoint, yPoint - 180, xPoint + 100, yPoint);

        // Simple checkbox to show demo window
        ImGui.checkbox("Show demo window", showDemoWindow);

        ImGui.separator();

        // Drag'n'Drop functionality
        ImGui.button("Drag me");
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("payload_type", testPayload, testPayload.length);
            ImGui.text("Drag started");
            ImGui.endDragDropSource();
        }
        ImGui.sameLine();
        ImGui.text(dropTargetText);
        if (ImGui.beginDragDropTarget()) {
            final byte[] payload = ImGui.acceptDragDropPayload("payload_type");
            if (payload != null) {
                dropTargetText = new String(payload);
            }
            ImGui.endDragDropTarget();
        }

        // Color picker
        ImGui.alignTextToFramePadding();
        ImGui.text("Background color:");
        ImGui.sameLine();
        ImGui.colorEdit3("##click_counter_col", backgroundColor, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop);
        Constants.WINDOW_CLEAR_COLOR.x = backgroundColor[0]; Constants.WINDOW_CLEAR_COLOR.y = backgroundColor[1]; Constants.WINDOW_CLEAR_COLOR.z = backgroundColor[2];

        // Simple click counter
        if (ImGui.button("Click")) {
            clickCount++;
        }
        if (ImGui.isItemHovered()) {
            ImGui.setMouseCursor(ImGuiMouseCursor.Hand);
        }
        ImGui.sameLine();
        ImGui.text("Count: " + clickCount);

        ImGui.separator();

        // Input field with auto-resize ability
        ImGui.text("You can use text inputs with auto-resizable strings!");
        ImGui.inputText("Resizable input", resizableStr, ImGuiInputTextFlags.CallbackResize);
        ImGui.text("text len:");
        ImGui.sameLine();
        ImGui.textColored(.12f, .6f, 1, 1, Integer.toString(resizableStr.getLength()));
        ImGui.sameLine();
        ImGui.text("| buffer size:");
        ImGui.sameLine();
        ImGui.textColored(1, .6f, 0, 1, Integer.toString(resizableStr.getBufferSize()));

        ImGui.separator();
        ImGui.newLine();

        // Link to the original demo file
        ImGui.text("Consider to look the original ImGui demo: ");
        ImGui.setNextItemWidth(500);
        ImGui.textColored(0, .8f, 0, 1, imguiDemoLink);
        ImGui.sameLine();
        if (ImGui.button("Copy")) {
            ImGui.setClipboardText(imguiDemoLink);
        }

        ImGui.end();  // End Custom window

        if (showDemoWindow.get()) {
            ImGui.showDemoWindow(showDemoWindow);
        }
    }
}
