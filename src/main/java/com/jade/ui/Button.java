package com.jade.ui;

import com.jade.Component;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.events.MouseListener;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public abstract class Button extends Component {
    SpriteRenderer renderer;
    Sprite regular, hover, press;

    // 0 = regular, 1 = hover, 2 = pressed
    int state = 0;
    private Vector2f min;
    private Vector2f max;

    public Button(Sprite regular, Sprite hover, Sprite press) {
        this.regular = regular;
        this.hover = hover;
        this.press = press;
        this.min = new Vector2f();
        this.max = new Vector2f();
    }

    @Override
    public void start() {
        this.renderer = uiObject.getComponent(SpriteRenderer.class);
    }

    private void calculateMin() {
        this.min.x = uiObject.transform.position.x - (this.uiObject.transform.scale.x / 2.0f);
        this.min.y = uiObject.transform.position.y - (this.uiObject.transform.scale.y / 2.0f);
        JMath.rotate(this.min, this.uiObject.transform.rotation.z, this.uiObject.transform.position);
    }

    private void calculateMax() {
        this.max.x = uiObject.transform.position.x + (this.uiObject.transform.scale.x / 2.0f);
        this.max.y = uiObject.transform.position.y + (this.uiObject.transform.scale.y / 2.0f);
        JMath.rotate(this.max, this.uiObject.transform.rotation.z, this.uiObject.transform.position);
    }

    public abstract void clicked();

    @Override
    public void update(float dt) {
        calculateMin();
        calculateMax();

        Vector4f mouseScreen = MouseListener.positionScreenCoords();
        if (this.state == 2 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            clicked();
        }

        if (mouseScreen.x > left() && mouseScreen.x < right() &&
            mouseScreen.y > bottom() && mouseScreen.y < top()) {
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

    private float left() {
        return this.min.x;
    }

    private float right() {
        return this.max.x;
    }

    private float top() {
        return this.max.y;
    }

    private float bottom() {
        return this.min.y;
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
