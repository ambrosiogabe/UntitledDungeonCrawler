package com.jade.components;

import com.jade.Component;
import com.jade.file.Parser;
import com.jade.renderer.Texture;
import com.jade.util.AssetPool;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Sprite extends Component {
    private String pictureFile;

    private int offsetX, offsetY, width, height, id;
    private Vector2f[] texCoords = {
            new Vector2f(0.0f, 0.0f),
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(1.0f, 1.0f)
    };
    private Texture texture = null;

    public Sprite(String pictureFile) {
        init(0, 0, 0, 0, 0, pictureFile);
    }

    public Sprite(int width, int height, int offsetX, int offsetY, int id, String pictureFile) {
        init(width, height, offsetX, offsetY, id, pictureFile);
    }

    private void init(int width, int height, int offsetX, int offsetY, int id, String pictureFile) {
        this.id = id;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.pictureFile = pictureFile;
        this.texture = AssetPool.getTexture(pictureFile);
        this.width = width == 0 ? texture.getWidth() : width;
        this.height = height == 0 ? texture.getHeight() : height;

        // Calculate texture coordinates
        float texWidth = (float)this.texture.getWidth();
        float texHeight = (float)this.texture.getHeight();
        Vector2f topLeft = new Vector2f(offsetX, offsetY);
        Vector2f bottomRight = new Vector2f(offsetX + width, offsetY + height);
        this.texCoords = new Vector2f[4];
        this.texCoords[0] = new Vector2f(topLeft.x / texWidth, topLeft.y / texHeight);                             // Top Left
        this.texCoords[1] = new Vector2f((topLeft.x + (float)width) / texWidth, topLeft.y / texHeight);            // Top Right
        this.texCoords[2] = new Vector2f((bottomRight.x - (float)width) / texWidth, bottomRight.y / texHeight);    // Bottom Left
        this.texCoords[3] = new Vector2f(bottomRight.x / texWidth, bottomRight.y / texHeight);                     // Bottom Right
    }

    @Override
    public Component copy() {
        return new Sprite(width, height, offsetX, offsetY, id, pictureFile);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Sprite", tabSize));
        builder.append(addStringProperty("FilePath", pictureFile, tabSize + 1, true, true));
        builder.append(addIntProperty("OffsetX", offsetX, tabSize + 1, true, true));
        builder.append(addIntProperty("OffsetY", offsetY, tabSize + 1, true, true));
        builder.append(addIntProperty("Width", width, tabSize + 1, true, true));
        builder.append(addIntProperty("Height", height, tabSize + 1, true, true));
        builder.append(addIntProperty("ID", id, tabSize + 1, true, false));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static Sprite deserialize() {
        String filePath = Parser.consumeStringProperty("FilePath");
        Parser.consume(',');
        int offsetX = Parser.consumeIntProperty("OffsetX");
        Parser.consume(',');
        int offsetY = Parser.consumeIntProperty("OffsetY");
        Parser.consume(',');
        int width = Parser.consumeIntProperty("Width");
        Parser.consume(',');
        int height = Parser.consumeIntProperty("Height");
        Parser.consume(',');
        int id = Parser.consumeIntProperty("ID");

        return new Sprite(offsetX, offsetY, width, height, id, filePath);
    }


    public int getID() {
        return this.id;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public String getPictureFile() {
        return this.pictureFile;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }
}
