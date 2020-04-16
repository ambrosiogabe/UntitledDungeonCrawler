package com.jade.util;

import com.jade.renderer.Shader;
import com.jade.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
//    static Map<String, Spritesheet> spritesheets = new HashMap<>();
    static Map<String, Shader> shaders = new HashMap<>();
    static Map<String, Texture> textures = new HashMap<>();
//    static Map<String, Sound> sounds = new HashMap<>();

    public static Texture getTexture(String resourceName) {
        if (!textures.containsKey(resourceName)) {
            Texture texture = new Texture(resourceName);
            AssetPool.textures.put(resourceName, texture);
            return AssetPool.textures.get(resourceName);
        }
        return textures.get(resourceName);
    }

    public static void addTexture(String resourceName, Texture texture) {
        if (!textures.containsKey(resourceName)) {
            textures.put(resourceName, texture);
        }
    }

//    public static Sound getSound(String soundFile) {
//        File file = new File(soundFile);
//        if (hasSound(soundFile)) {
//            return sounds.get(file.getAbsolutePath());
//        } else {
//            assert false : "Sound file not added '" + soundFile + "'.";
//        }
//        return null;
//    }
//
//    public static Sound addSound(String soundFile, boolean loops) {
//        File file = new File(soundFile);
//        if (hasSound(soundFile)) {
//            return sounds.get(file.getAbsolutePath());
//        } else {
//            Sound sound = new Sound(file.getAbsolutePath(), loops);
//            AssetPool.sounds.put(file.getAbsolutePath(), sound);
//            return AssetPool.sounds.get(file.getAbsolutePath());
//        }
//    }
//
    public static Shader getShader(String shaderPath) {
        File file = new File(shaderPath);
        if (!shaders.containsKey(shaderPath)) {
            Shader shader = new Shader(shaderPath);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }

        return AssetPool.shaders.get(file.getAbsolutePath());
    }
//
//    public static Spritesheet getSpritesheet(String pictureFile) {
//        File file = new File(pictureFile);
//        if (AssetPool.hasSpritesheet(file.getAbsolutePath())) {
//            return AssetPool.spritesheets.get(file.getAbsolutePath());
//        } else {
//            assert false : "Spritesheet '" + file.getAbsolutePath() + "' does not exist.";
//        }
//        return null;
//    }
//
//    public static void addSpritesheet(String pictureFile, int tileWidth, int tileHeight,
//                                      int spacing, int columns, int size) {
//        File file = new File(pictureFile);
//        if (!AssetPool.hasSpritesheet(file.getAbsolutePath())) {
//            Spritesheet spritesheet = new Spritesheet(pictureFile, tileWidth, tileHeight,
//                    spacing, columns, size);
//            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
//        }
//    }
}