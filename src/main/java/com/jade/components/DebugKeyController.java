package com.jade.components;

import com.jade.Component;
import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.events.KeyListener;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class DebugKeyController extends Component {

    private int activeUiObject = -1;
    private int activeGameObject = -1;
    private List<GameObject> gameObjects;
    private List<UIObject> uiObjects;

    private float keyDebounceTime = 0.2f;
    private float keyDebounceLeft = 0.0f;

    @Override
    public void start() {
        this.gameObjects = Window.getScene().getAllGameObjects();
        this.uiObjects = Window.getScene().getAllUIObjects();
    }

    @Override
    public void update(float dt) {
        this.keyDebounceLeft -= dt;

        if (KeyListener.isKeyPressed(GLFW_KEY_TAB) && this.keyDebounceLeft < 0) {
            incrementActiveGameObject();

            this.keyDebounceLeft = this.keyDebounceTime;
        }
    }

    private void incrementActiveGameObject() {
        if (this.activeUiObject == -1 && this.activeGameObject < this.gameObjects.size() - 1) {
            this.activeGameObject++;
            Window.getScene().setActiveGameObject(activeGameObject);
        } else if (this.activeUiObject < this.uiObjects.size() - 1) {
            this.activeUiObject++;
            this.activeGameObject = -1;
            Window.getScene().setActiveGameObject(activeGameObject);
            Window.getScene().setActiveUiObject(activeUiObject);
        } else if (this.activeUiObject == this.uiObjects.size() - 1) {
            this.activeUiObject = -1;
            Window.getScene().setActiveUiObject(activeUiObject);
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
