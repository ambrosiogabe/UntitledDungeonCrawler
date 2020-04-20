package com.jade.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

public class Texture {
    private static int GLOBAL_TEX_ID = 0;

    private int texID;
    private int width = 0;
    private int height = 0;
    private String filepath = "";

    public Texture(BufferedImage image, boolean hasAlpha) {
        this.filepath = "Generated-" + GLOBAL_TEX_ID++;
        width = image.getWidth();
        height = image.getHeight();

        // Taken from https://stackoverflow.com/questions/10801016/lwjgl-textures-and-strings
        int bytesPerPixel = 3;
        if (hasAlpha) bytesPerPixel = 4;

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * bytesPerPixel); //4 for RGBA, 3 for RGB

        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                if (hasAlpha) {
                    buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }
        }

        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        texID = glGenTextures(); //Generate texture ID
        glBindTexture(GL11.GL_TEXTURE_2D, texID); //Bind texture ID

        // Set texture parameters/options
        // Repeat image in both directions (x, y)
        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching an image, pixelate
        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking an image, pixelate
        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        buffer.clear();
    }

    public Texture(String resourceName) {
        this.filepath = resourceName;
        int channels = 0;
        ByteBuffer buffer = null;

        // ===========================================
        // Load the image file using STB
        // ===========================================
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            URL url = this.getClass().getClassLoader().getResource(resourceName);
            assert url != null : "Error: Tried loading texture, but no file exists '" + resourceName + "'";

            File file = new File(url.toURI());
            String filepath = file.getAbsolutePath();
            buffer = STBImage.stbi_load(filepath, w, h, c, 4);
            assert buffer != null : "Error loading texture file '" + filepath + "'";

            width = w.get();
            height = h.get();
            channels = c.get();
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "";
        }

        // ===========================================
        // Upload the bytes using openGL
        // ===========================================
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters/options
        // Repeat image in both directions (x, y)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Send texel data to OpenGL
        if (channels == 4) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else if (channels == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
        } else {
            assert false : "Texture error, cannot handle loading texture with '" + channels + "' channels";
        }

        // Free image
        STBImage.stbi_image_free(buffer);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getID() {
        return this.texID;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getFilepath() {
        return this.filepath;
    }
}
