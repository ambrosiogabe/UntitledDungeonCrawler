package com.jade.util;

import com.jade.renderer.fonts.FontTexture;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.Font;

public class Constants {
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
    public static FontTexture DEFAULT_FONT = new FontTexture(new Font("LLPixel", Font.PLAIN, 48), "US-ASCII");
    public static FontTexture EXTRA_LARGE_FONT = new FontTexture(new Font("LLPixel", Font.PLAIN, 96), "US-ASCII");
    public static FontTexture DEBUG_FONT = new FontTexture(new Font("Arial", Font.PLAIN, 24), "US-ASCII");

    // ======================================================================================================
    // COLORS
    // ======================================================================================================
    public static Vector4f WINDOW_CLEAR_COLOR = new Vector4f(0.15f, 0.25f, 0.41f, 1.0f);
    public static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector4f RED = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Vector4f GREEN = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f BLUE = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Vector4f YELLOW = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f CYAN = new Vector4f(0.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector4f PURPLE = new Vector4f(1.0f, 0.0f, 1.0f, 1.0f);
    public static final Vector4f BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
}
