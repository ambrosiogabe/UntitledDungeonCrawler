package com.jade.scenes;

import com.jade.*;
import com.jade.components.Model;
import com.jade.renderer.Renderer;
import imgui.ImGui;
import imgui.enums.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    private Renderer renderer;
    protected List<GameObject> gameObjects;
    protected List<UIObject> uiObjects;
    private Camera camera;

    protected int activeGameObject = -1;
    protected int activeUiObject = -1;
    private int selected = -1;

    public Scene() {
        this.camera = new Camera(new Vector3f(0, 0, 0));
        this.renderer = new Renderer(this.camera);
        renderer.init();

        this.gameObjects = new ArrayList<>();
        this.uiObjects = new ArrayList<>();
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public void reset() {
        this.gameObjects.clear();
        this.uiObjects.clear();
        this.camera.transform = new Transform();
        this.renderer.reset();
    }

    public void init() {

    }

    public void addGameObject(GameObject g) {
        this.gameObjects.add(g);
        this.renderer.addGameObject(g);
    }

    public void addUIObject(UIObject u) {
        this.uiObjects.add(u);
        this.renderer.addUIObject(u);
    }

    public void render() {
        this.renderer.render();
    }

    public void imgui() {
        ImGui.setNextWindowSize(400, Window.getWindow().getHeight(), ImGuiCond.Always);
        ImGui.setNextWindowPos(Window.getWindow().getWidth() - 1000, 0, ImGuiCond.Always);
        ImGui.begin("Objects");

        for (int i=0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i).isSerializable()) {
                if (ImGui.selectable(gameObjects.get(i).getName(), selected == i && activeUiObject == -1)) {
                    activeGameObject = i;
                    selected = activeGameObject;
                }
            }
        }
        if (selected != activeGameObject) {
            activeGameObject = -1;
        }

        for (int i=0; i < uiObjects.size(); i++) {
            if (ImGui.selectable(uiObjects.get(i).getName(), selected == i && activeGameObject == -1)) {
                activeUiObject = i;
                selected = activeUiObject;
            }
        }
        ImGui.end();
        if (selected != activeUiObject) {
            activeUiObject = -1;
        }

        if (activeGameObject != -1) {
            gameObjects.get(activeGameObject).imgui();
        } else if (activeUiObject != -1) {
            uiObjects.get(activeUiObject).imgui();
        }
    }

    public List<GameObject> getAllGameObjects() {
        return this.gameObjects;
    }

    public List<UIObject> getAllUIObjects() {
        return this.uiObjects;
    }

    public void setActiveGameObject(int i) {
        this.activeGameObject = i;
        this.selected = i;
    }

    public void setActiveUiObject(int i) {
        this.activeUiObject = i;
        this.selected = i;
    }

    public abstract void update(float dt);

    public Camera camera() {
        return this.camera;
    }
}
