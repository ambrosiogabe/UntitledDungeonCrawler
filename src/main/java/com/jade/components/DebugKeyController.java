package com.jade.components;

import com.jade.Component;
import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.events.KeyListener;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class DebugKeyController extends Component {

    private int activeUiObject = -1;
    private int activeGameObject = -1;
    private List<GameObject> gameObjects;
    private List<UIObject> uiObjects;

    private float keyDebounceTime = 0.2f;
    private float keyDebounceLeft = 0.0f;
    private boolean firstPass = true;

    @Override
    public void start() {
        this.gameObjects = Window.getScene().getAllGameObjects();
        this.uiObjects = Window.getScene().getAllUIObjects();
    }

    @Override
    public void update(float dt) {
        this.keyDebounceLeft -= dt;

        if (firstPass) {
            incrementActiveGameObject();
            firstPass = false;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && this.keyDebounceLeft < 0) {
            incrementActiveGameObject();

            this.keyDebounceLeft = this.keyDebounceTime;
        }
    }

    private void incrementActiveGameObject() {
        if (this.activeUiObject == -1 && this.activeGameObject < this.gameObjects.size() - 1) {
            this.activeGameObject++;
            Window.getScene().setActiveGameObject(activeGameObject);
            if (!this.gameObjects.get(activeGameObject).isSerializable()) {
                incrementActiveGameObject();
            }
        } else if (this.activeUiObject < this.uiObjects.size() - 1) {
            this.activeUiObject++;
            this.activeGameObject = -1;
            Window.getScene().setActiveGameObject(activeGameObject);
            Window.getScene().setActiveUiObject(activeUiObject);
            if (!this.uiObjects.get(activeUiObject).isSerializable()) {
                incrementActiveGameObject();
            }
        } else if (this.activeUiObject == this.uiObjects.size() - 1) {
            this.activeUiObject = -1;
            Window.getScene().setActiveUiObject(activeUiObject);
        }

        if (this.activeGameObject == -1 && this.activeUiObject == -1) {
            this.activeGameObject = 0;
            Window.getScene().setActiveGameObject(activeGameObject);
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
