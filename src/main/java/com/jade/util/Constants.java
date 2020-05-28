package com.jade.util;

import com.jade.Window;
import com.jade.renderer.fonts.FontTexture;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;

import java.awt.Font;

public class Constants {
    public static final boolean DEBUG_BUILD = true;

    public static final int INITIAL_WINDOW_WIDTH = 2560;
    public static final int INITIAL_WINDOW_HEIGHT = 1440;
    public static final String WINDOW_TITLE = "Minecraft";

    public static final Vector3f LEFT = new Vector3f(-1.0f, 0.0f, 0.0f);
    public static final Vector3f RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);
    public static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f DOWN = new Vector3f(0.0f, -1.0f, 0.0f);
    public static final Vector3f FORWARD = new Vector3f(0.0f, 0.0f, 1.0f);
    public static final Vector3f BACK = new Vector3f(0.0f, 0.0f, -1.0f);

    // ======================================================================================================
    // FONTS
    // ======================================================================================================
    public static final FontTexture DEFAULT_FONT = Window.windowCreated() ? new FontTexture(new Font("LLPixel", Font.PLAIN, 48), "US-ASCII") : null;
    public static final FontTexture EXTRA_LARGE_FONT = Window.windowCreated() ? new FontTexture(new Font("LLPixel", Font.PLAIN, 96), "US-ASCII") : null;
    public static final FontTexture DEBUG_FONT = Window.windowCreated() ? new FontTexture(new Font("Arial", Font.PLAIN, 24), "US-ASCII") : null;

    // ======================================================================================================
    // COLORS
    // ======================================================================================================
    //public static Vector4f WINDOW_CLEAR_COLOR = new Vector4f(0.15f, 0.25f, 0.41f, 1.0f);
    public static Vector4f WINDOW_CLEAR_COLOR = new Vector4f(0.529f, 0.807f, 0.921f, 1.0f);
    public static final Vector4f COLOR4_WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector4f COLOR4_RED = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Vector4f COLOR4_GREEN = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f COLOR4_BLUE = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Vector4f COLOR4_YELLOW = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f COLOR4_CYAN = new Vector4f(0.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector4f COLOR4_PURPLE = new Vector4f(1.0f, 0.0f, 1.0f, 1.0f);
    public static final Vector4f COLOR4_BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public static final Vector3f COLOR3_WHITE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f COLOR3_RED = new Vector3f(1.0f, 0.0f, 0.0f);
    public static final Vector3f COLOR3_GREEN = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f COLOR3_BLUE = new Vector3f(0.0f, 0.0f, 1.0f);
    public static final Vector3f COLOR3_YELLOW = new Vector3f(1.0f, 1.0f, 0.0f);
    public static final Vector3f COLOR3_CYAN = new Vector3f(0.0f, 1.0f, 1.0f);
    public static final Vector3f COLOR3_PURPLE = new Vector3f(1.0f, 0.0f, 1.0f);
    public static final Vector3f COLOR3_BLACK = new Vector3f(0.0f, 0.0f, 0.0f);
}
