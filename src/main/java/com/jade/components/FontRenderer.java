package com.jade.components;

import com.jade.Component;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.renderer.fonts.FontTexture;
import com.jade.util.Constants;
import imgui.ImGui;
import imgui.ImGuiInputTextData;
import imgui.ImString;
import imgui.enums.ImGuiInputTextFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class FontRenderer extends Component {
    private FontTexture fontTexture;
    private List<UIObject> uiObjects;
    private String text;
    private float width;
    private boolean firstTime = true;
    private ImString imText;

    private Vector4f color;
    private Transform lastTransform;

    public FontRenderer(FontTexture fontTexture, String text) {
        this.color = Constants.WHITE;

        this.fontTexture = fontTexture;
        this.text = text;
        imText = new ImString(text);
        this.uiObjects = new ArrayList<>();
        calculateObjects(this.text);
    }

    @Override
    public void start() {
        this.lastTransform = new Transform();
        Transform.copyValues(uiObject.transform, this.lastTransform);
        calculateObjectPositions();
    }

    private void calculateObjects(String text) {
//        if (uiObjects.size() > 0 && text.length() < this.text.length()) {
//            // IMPORTANT: Be very careful in this loop, it concurrently modifies the
//            // loop while iterating through it!!!
//            int start = text.length();
//            for (int i=start; i < uiObjects.size(); i++) {
//                UIObject u = uiObjects.get(i);
//                u.getComponent(SpriteRenderer.class).delete();
//                uiObjects.remove(i);
//                i--;
//            }
//        }

        float currentX = 0;
        float currentY = 0;

        char[] charArray = text.toCharArray();
        for (int i=0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '\n') {
                currentY -= fontTexture.getLineHeight();
                currentX = 0;
                continue;
            }
            int width = (int)fontTexture.getWidthOf(c);
            int height = (int)fontTexture.getLineHeight();

            Vector2f sourceOffset = fontTexture.getSourceOffset(c);
            Sprite sprite = new Sprite(width, height, (int)sourceOffset.x, (int)sourceOffset.y, 0, fontTexture.getTexture().getFilepath());

            UIObject newObject;
            if (i < uiObjects.size()) {
                newObject = uiObjects.get(i);
                SpriteRenderer spriteRenderer = newObject.getComponent(SpriteRenderer.class);
                modifyObject(newObject, (int)currentX, (int)currentY, width, height, sprite, spriteRenderer);
            } else {
                newObject = new UIObject("Generated", new Vector3f(currentX, currentY, 0), new Vector3f(width, height, 0));
                SpriteRenderer spriteRenderer = new SpriteRenderer(sprite);
                spriteRenderer.setColor(this.color);
                newObject.addComponent(spriteRenderer);
                uiObjects.add(newObject);
                newObject.start();
                Window.getScene().getRenderer().addUIObject(newObject);
            }

            currentX += fontTexture.getWidthOf(c);
            this.width += fontTexture.getWidthOf(c);
        }
    }

    private void calculateObjectPositions() {
        float currentX = uiObject.transform.position.x;
        float currentY = uiObject.transform.position.y;

        char[] charArray = text.toCharArray();
        assert charArray.length <= this.uiObjects.size() : "Error: Cannot resize modified text in FontRenderer.";
        for (int i=0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '\n') {
                currentY -= fontTexture.getLineHeight();
                currentX = uiObject.transform.position.x;
                continue;
            }
            int width = (int)fontTexture.getWidthOf(c);
            int height = (int)fontTexture.getLineHeight();

            UIObject obj = uiObjects.get(i);
            SpriteRenderer spriteRenderer = obj.getComponent(SpriteRenderer.class);
            modifyObject(obj, (int)currentX, (int)currentY, width, height, spriteRenderer.getSprite(), spriteRenderer);

            currentX += fontTexture.getWidthOf(c);
            this.width += fontTexture.getWidthOf(c);
        }
    }

    public void setColor(Vector4f color) {
        this.color = color;
        for (UIObject u : uiObjects) {
            u.getComponent(SpriteRenderer.class).setColor(color);
        }
    }

    public void setPosition(Vector3f position) {
        this.uiObject.transform.position = position;
        calculateObjectPositions();
    }

    private void modifyObject(UIObject u, int currentX, int currentY, int width, int height, Sprite sprite, SpriteRenderer spriteRenderer) {
        u.transform.position.x = currentX;
        u.transform.position.y = currentY;
        u.transform.scale.x = width;
        u.transform.scale.y = height;
        spriteRenderer.setSprite(sprite);
        spriteRenderer.setColor(this.color);
    }

    public void setText(String newText) {
        if (!newText.equals(this.text)) {
            calculateObjects(newText);
            this.text = newText;
            calculateObjectPositions();
        }
    }

    public String getText() {
        return this.text;
    }

    public List<UIObject> getUIObjects() {
        return this.uiObjects;
    }

    @Override
    public void imgui() {
        ImGui.text("Text: ");
        ImGui.sameLine();
        if(ImGui.inputText("", imText, ImGuiInputTextFlags.CallbackResize | ImGuiInputTextFlags.EnterReturnsTrue)) {
            this.setText(imText.get());
        }

        ImGui.text("Color: ");
        ImGui.sameLine();
        float[] imColor = {this.color.x, this.color.y, this.color.z};
        if (ImGui.colorPicker3("", imColor)) {
            this.color.x = imColor[0];
            this.color.y = imColor[1];
            this.color.z = imColor[2];
            this.setColor(this.color);
        }
    }

    @Override
    public void update(float dt) {
        if (!this.uiObject.transform.position.equals(this.lastTransform.position)) {
            this.setPosition(this.uiObject.transform.position);
            Transform.copyValues(this.uiObject.transform, this.lastTransform);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
