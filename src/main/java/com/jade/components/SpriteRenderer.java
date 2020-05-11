package com.jade.components;

import com.jade.Component;
import com.jade.Transform;
import com.jade.Window;
import com.jade.renderer.Shader;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import com.jade.util.JMath;
import imgui.ImGui;
import imgui.ImVec4;
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
    private int zIndex;

    private boolean addToRenderer = false;

    public SpriteRenderer() {
        this.sprite = new Sprite("images/defaultSprite.png");

        this.lastSpriteId = this.sprite.getID();
        this.isDirty = true;
        this.zIndex = 0;
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;

        this.lastSpriteId = this.sprite.getID();
        this.isDirty = true;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
        this.lastVisible = gameObject.isVisible();
    }

    @Override
    public void update(float dt) {
        if (this.lastSpriteId != this.sprite.getID()) {
            this.isDirty = true;
            this.lastSpriteId = this.sprite.getID();
        }

        if (this.lastVisible != this.gameObject.isVisible()) {
            this.lastVisible = this.gameObject.isVisible();
            this.shouldDisplay = this.lastVisible;
            this.isDirty = true;
        }

        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.isDirty = true;
            Transform.copyValues(this.gameObject.transform, this.lastTransform);
        }

        if (this.addToRenderer) {
            Window.getScene().getRenderer().addGameObject(this.gameObject);
            this.isDirty = true;
            this.addToRenderer = false;
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

    @Override
    public void imgui() {
        int[] imZIndex = {zIndex};
        ImGui.dragInt("Z-Index", imZIndex, 0.05f, -2, 2);
        if (imZIndex[0] != zIndex) {
            zIndex = imZIndex[0];
            Window.getScene().getRenderer().deleteGameObject(this.gameObject);
            this.addToRenderer = true;
            this.isDirty = true;
        }

        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Color", imColor)) {
            color.x = imColor[0];
            color.y = imColor[1];
            color.z = imColor[2];
            color.w = imColor[3];
            this.isDirty = true;
        }
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

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int zIndex() {
        return this.zIndex;
    }
}
