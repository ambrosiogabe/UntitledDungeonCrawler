package com.jade.scenes;

import com.jade.Camera;
import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.components.Model;
import com.jade.renderer.Renderer;
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
    }

    public void setActiveUiObject(int i) {
        this.activeUiObject = i;
    }

    public abstract void update(float dt);

    public Camera camera() {
        return this.camera;
    }
}
