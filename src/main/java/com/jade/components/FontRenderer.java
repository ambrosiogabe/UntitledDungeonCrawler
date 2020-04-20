package com.jade.components;

import com.jade.Component;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.renderer.fonts.FontTexture;
import com.jade.util.Constants;
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

    public FontRenderer(FontTexture fontTexture, String text) {
        this.fontTexture = fontTexture;
        this.text = text;
        this.uiObjects = new ArrayList<>();
        calculateObjects(this.text);
    }

    @Override
    public void start() {
        setPosition(uiObject.transform.position);
    }

    private void calculateObjects(String text) {
        if (uiObjects.size() > 0 && text.length() < this.text.length()) {
            // IMPORTANT: Be very careful in this loop, it concurrently modifies the
            // loop while iterating through it!!!
            int start = text.length();
            for (int i=start; i < uiObjects.size(); i++) {
                UIObject u = uiObjects.get(i);
                u.getComponent(SpriteRenderer.class).delete();
                uiObjects.remove(i);
                i--;
            }
        }

        float currentX = uiObject ==  null ? 0 : uiObject.transform.position.x;
        float currentY = uiObject == null ? 0 : uiObject.transform.position.y;

        char[] charArray = text.toCharArray();
        for (int i=0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == '\n') {
                currentY -= fontTexture.getLineHeight();
                currentX = uiObject ==  null ? 0 : uiObject.transform.position.x;
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
                newObject = new UIObject(new Vector3f(currentX, currentY, 0), new Vector3f(width, height, 0));
                SpriteRenderer spriteRenderer = new SpriteRenderer(sprite);
                newObject.addComponent(spriteRenderer);
                uiObjects.add(newObject);
                newObject.start();
                Window.getScene().getRenderer().addUIObject(newObject);
            }

            currentX += fontTexture.getWidthOf(c);
            this.width += fontTexture.getWidthOf(c);
        }
    }

    public void setColor(Vector4f color) {
        for (UIObject u : uiObjects) {
            u.getComponent(SpriteRenderer.class).setColor(color);
        }
    }

    public void setPosition(Vector3f position) {
        for (UIObject u : uiObjects) {
            u.transform.position.x += position.x;
            u.transform.position.y += position.y;
        }
    }

    private void modifyObject(UIObject u, int currentX, int currentY, int width, int height, Sprite sprite, SpriteRenderer spriteRenderer) {
        u.transform.position.x = currentX;
        u.transform.position.y = currentY;
        u.transform.scale.x = width;
        u.transform.scale.y = height;
        spriteRenderer.setSprite(sprite);
    }

    public void setText(String newText) {
        if (!newText.equals(this.text)) {
            calculateObjects(newText);
            this.text = newText;
        }
    }

    public String getText() {
        return this.text;
    }

    public List<UIObject> getUIObjects() {
        return this.uiObjects;
    }

    @Override
    public void update(float dt) {
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
