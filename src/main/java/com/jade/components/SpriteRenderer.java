package com.jade.components;

import com.jade.Component;
import com.jade.Transform;
import com.jade.renderer.Shader;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import com.jade.util.JMath;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Sprite sprite;
    private Vector4f color = JMath.copy(Constants.COLOR4_WHITE);
    private Shader shader = AssetPool.getShader("shaders/default.glsl");
    private boolean shouldDisplay = true;

    private boolean isDirty;
    private boolean shouldDelete = false;
    private Transform lastTransform;
    private int lastSpriteId;
    private boolean lastVisible;

    public SpriteRenderer() {
        this.sprite = new Sprite("images/defaultSprite.png");

        this.lastSpriteId = this.sprite.getID();
        this.isDirty = true;
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;

        this.lastSpriteId = this.sprite.getID();
        this.isDirty = true;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject != null ? gameObject.transform.copy() : uiObject.transform.copy();
        this.lastVisible = gameObject != null ? gameObject.isVisible() : uiObject.isVisible();
    }

    @Override
    public void update(float dt) {
        if (this.lastSpriteId != this.sprite.getID()) {
            this.isDirty = true;
            this.lastSpriteId = this.sprite.getID();
        }

        if (this.lastVisible != (this.gameObject != null ? this.gameObject.isVisible() : this.uiObject.isVisible())) {
            this.lastVisible = this.gameObject != null ? this.gameObject.isVisible() : this.uiObject.isVisible();
            this.shouldDisplay = this.lastVisible;
            this.isDirty = true;
        }

        if (!this.lastTransform.equals(this.gameObject != null ? this.gameObject.transform : this.uiObject.transform)) {
            this.isDirty = true;
            Transform.copyValues(this.gameObject != null ? this.gameObject.transform : this.uiObject.transform, this.lastTransform);
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

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public void delete() {
        this.shouldDelete = true;
    }

    public boolean shouldDelete() {
        return this.shouldDelete;
    }

    public boolean shouldDisplay() {
        return this.shouldDisplay;
    }

    public void setColor(Vector4f color) {
        JMath.copyValues(color, this.color);
        this.isDirty = true;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public Vector4f getColor() {
        return this.color;
    }
}
