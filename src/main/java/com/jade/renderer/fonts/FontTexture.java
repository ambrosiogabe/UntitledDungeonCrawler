package com.jade.renderer.fonts;

import com.jade.renderer.Texture;
import com.jade.util.AssetPool;
import org.joml.Vector2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

public class FontTexture {
    private Font font;
    private String charSetName;
    private Map<Character, CharInfo> charMap;
    private float lineHeight;
    private float halfLineHeight;

    private Texture texture;
    private int width, height;

    public FontTexture(Font font, String charSetName) {
        this.font = font;
        this.charSetName = charSetName;
        this.charMap = new HashMap<>();

        // This guy sets the texture and line height
        buildTexture();
    }

    public float getWidthOf(char c) {
        return charMap.get(c).getWidth();
    }

    public float getHalfWidthOf(char c) {
        return charMap.get(c).getHalfWidth();
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public float getHalfLineHeight() {
        return this.halfLineHeight;
    }

    public float getWidthOf(String str) {
        float width = 0.0f;
        for (char c : str.toCharArray()) {
            width += getWidthOf(c);
        }
        return width;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords(char c) {
        return charMap.get(c).getTexCoords();
    }

    public Vector2f getSourceOffset(char c) {
        return charMap.get(c).getSourceOffset();
    }

    private String getAllAvailableChars(String charsetName) {
        CharsetEncoder ce = Charset.forName(charsetName).newEncoder();
        StringBuilder result = new StringBuilder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (ce.canEncode(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    private void buildTexture() {
        // Get the font metrics for each character for the selected font by using image
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = img.createGraphics();
        g2D.setFont(font);
        FontMetrics fontMetrics = g2D.getFontMetrics();

        String allChars = getAllAvailableChars(charSetName);
        int rowLength = (int)Math.sqrt(allChars.length()) + 1;
        this.width = 0;
        this.height = fontMetrics.getHeight();
        this.lineHeight = fontMetrics.getHeight() * 1.3f;
        this.halfLineHeight = this.lineHeight / 2.0f;
        int x = fontMetrics.getFont().getSize() * rowLength;
        int y = 0;
        for (char c : allChars.toCharArray()) {
            x -= fontMetrics.charWidth(c);
            if (x < 0) {
                x = fontMetrics.getFont().getSize() * rowLength;
                y += fontMetrics.getHeight() * 1.4f;
                height += fontMetrics.getHeight() * 1.4f;
            }

            // Get the size for each character and update the global image size
            CharInfo charInfo = new CharInfo(x, y, fontMetrics.charWidth(c), fontMetrics.getHeight());
            charMap.put(c, charInfo);
            width = Math.max(x + fontMetrics.charWidth(c), width);
        }

        for (char c : charMap.keySet()) {
            charMap.get(c).makeTexCoords(width, height);
        }

        g2D.dispose();

        // Create the image associated to the charset
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2D = img.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setFont(font);
        g2D.setColor(Color.WHITE);
        for (char c : charMap.keySet()) {
            g2D.drawString("" + c, charMap.get(c).getSourceX(), charMap.get(c).getSourceY());
        }
        g2D.dispose();

        // Uncomment for testing purposes if you need to check if font is generating properly
//        try {
//            File file =  new java.io.File("temp.png");
//            ImageIO.write(img, "png", file);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(-1);
//        }

        this.texture = new Texture(img, true);
        AssetPool.addTexture(this.texture.getFilepath(), this.texture);
    }
}

