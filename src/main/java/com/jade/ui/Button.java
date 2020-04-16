package com.jade.ui;

import com.jade.Component;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.events.MouseListener;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public abstract class Button extends Component {
    SpriteRenderer renderer;
    Sprite regular, hover, press;

    // 0 = regular, 1 = hover, 2 = pressed
    int state = 0;

    public Button(Sprite regular, Sprite hover, Sprite press) {
        this.regular = regular;
        this.hover = hover;
        this.press = press;
    }

    @Override
    public void start() {
        this.renderer = uiObject.getComponent(SpriteRenderer.class);
    }

    public abstract void clicked();

    @Override
    public void update(float dt) {
        Vector4f mouseScreen = MouseListener.positionScreenCoords();
        if (this.state == 2 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            clicked();
        }

        if (mouseScreen.x > this.uiObject.transform.position.x && mouseScreen.x < this.uiObject.transform.position.x + this.uiObject.transform.scale.x &&
            mouseScreen.y > this.uiObject.transform.position.y && mouseScreen.y < this.uiObject.transform.position.y + this.uiObject.transform.scale.y) {
            if (this.state != 1 && this.state != 2) {
                this.renderer.setSprite(this.hover);
                this.state = 1;
            } else if (this.state != 2 && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                this.state = 2;
                this.renderer.setSprite(this.press);
            } else if (this.state == 2 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                this.state = 1;
                this.renderer.setSprite(this.hover);
            }
        } else if (state != 0) {
            this.renderer.setSprite(this.regular);
            this.state = 0;
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
