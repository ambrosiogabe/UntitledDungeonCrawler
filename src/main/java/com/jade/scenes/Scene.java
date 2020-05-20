package com.jade.scenes;

import com.jade.*;
import com.jade.components.Model;
import com.jade.physics.Physics;
import com.jade.physics2d.Physics2D;
import com.jade.renderer.Renderer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.enums.ImGuiCond;
import imgui.enums.ImGuiWindowFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    private Renderer renderer;
    protected Physics physics;
    protected Physics2D physics2D;

    protected List<GameObject> gameObjects;
    private Camera camera;

    protected int activeGameObject = -1;
    protected int activeUiObject = -1;
    private int selected = -1;

    private float lastConsoleHeight = 200;
    private float lastInspectorWidth = 600;
    private float windowAspect = 3840f / 2160f;
    private float gameViewDisplayWidth = 0.0f;
    private float gameViewDisplayHeight = 0.0f;
    private float gameViewXPos = 0.0f;
    private float gameViewYPos = 0.0f;

    private boolean doPhysics = false;

    private boolean isRunning = false;

    public Scene() {
        this.camera = new Camera(new Vector3f(0, 0, 0));
        this.renderer = new Renderer(this.camera);
        renderer.init();

        this.physics = new Physics();
        this.physics2D = new Physics2D();

        this.gameObjects = new ArrayList<>();
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public void reset() {
        this.gameObjects.clear();
        this.camera.transform = new Transform();
        this.renderer.reset();
        isRunning = false;
    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            physics.addGameObject(go);
            physics2D.addGameObject(go);
            renderer.addGameObject(go);
        }

        isRunning = true;
    }

    public void init() {

    }

    public void addGameObject(GameObject g) {
        if (!isRunning) {
            this.gameObjects.add(g);
        } else {
            this.gameObjects.add(g);
            g.start();
            this.renderer.addGameObject(g);
            this.physics.addGameObject(g);
            this.physics2D.addGameObject(g);
        }
    }

    public void deleteGameObject(GameObject g) {
        this.gameObjects.remove(g);
        this.renderer.deleteGameObject(g);
    }

    public void render() {
        this.renderer.render();
    }

    public void imgui() {
        // ==================================================================
        // Object browser window
        // ==================================================================
        ImGui.setNextWindowSizeConstraints(400, Window.getWindow().getHeight(), 10000f, Window.getWindow().getHeight());
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.begin("Objects", ImGuiWindowFlags.NoMove);
        float objectPaneWidth = ImGui.getWindowWidth();

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
        ImGui.end();
        if (selected != activeUiObject) {
            activeUiObject = -1;
        }

        // ==================================================================
        // Render Selected Object inspector window
        // ==================================================================
        ImGui.setNextWindowSizeConstraints(600, Window.getWindow().getHeight(), 10000, Window.getWindow().getHeight());
        ImGui.setNextWindowPos(Window.getWindow().getWidth() - lastInspectorWidth, 0, ImGuiCond.Always);
        ImGui.begin("Inspector", ImGuiWindowFlags.NoMove);
        float inspectorPaneWidth = ImGui.getWindowWidth();
        lastInspectorWidth = inspectorPaneWidth;
        if (activeGameObject != -1) {
            gameObjects.get(activeGameObject).imgui();
        }
        ImGui.end();

        // ==================================================================
        // Render Console Window?
        // TODO: FIGURE OUT WHAT THIS WINDOW WILL BE?
        // ==================================================================
        float consoleWidth = Window.getWindow().getWidth() - objectPaneWidth - inspectorPaneWidth;
        ImGui.setNextWindowSizeConstraints(consoleWidth, 300, consoleWidth, 600);
        ImGui.setNextWindowPos(objectPaneWidth, Window.getWindow().getHeight() - lastConsoleHeight, ImGuiCond.Always);
        ImGui.begin("Console", ImGuiWindowFlags.NoMove);
        lastConsoleHeight = ImGui.getWindowHeight();
        ImGui.end();

        // ==================================================================
        // Render game viewport
        // ==================================================================
        float gameViewHeight = Window.getWindow().getHeight() - lastConsoleHeight;
        ImGui.setNextWindowSize(consoleWidth, gameViewHeight, ImGuiCond.Always);
        ImGui.setNextWindowPos(objectPaneWidth, 0, ImGuiCond.Always);
        ImGui.begin("Game View", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);
        drawFramebufferTexture(consoleWidth, gameViewHeight);
        boolean wantCursor = !ImGui.isMouseHoveringRect(objectPaneWidth, 0, objectPaneWidth + consoleWidth, gameViewHeight);
        ImGui.getIO().setWantCaptureMouse(wantCursor);
        ImGui.end();
    }

    private void drawFramebufferTexture(float consoleWidth, float gameViewHeight) {
        float textureWidth = consoleWidth;
        float textureHeight = (textureWidth / windowAspect);
        if (textureHeight + 55 > gameViewHeight) {
            textureHeight = gameViewHeight - 55;
            textureWidth = windowAspect * gameViewHeight;
        }

        ImVec2 windowSize = new ImVec2();
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImGui.getWindowPos(windowPos);
        final float xPoint = windowPos.x + (windowSize.x - textureWidth) / 2.0f;
        final float yPoint = windowPos.y + (windowSize.y - textureHeight) / 2.0f;
        ImGui.getWindowDrawList().addImage(Window.getWindow().getFramebufferTexID(),
                xPoint, yPoint, xPoint + textureWidth, yPoint + textureHeight, 0, 1, 1, 0);

        this.gameViewDisplayWidth = textureWidth;
        this.gameViewDisplayHeight = textureHeight;
        this.gameViewXPos = xPoint;
        this.gameViewYPos = yPoint;
    }

    public float getGameViewDisplayWidth() {
        return this.gameViewDisplayWidth;
    }

    public float getGameViewDisplayHeight() {
        return this.gameViewDisplayHeight;
    }

    public float getGameViewXPos() {
        return this.gameViewXPos;
    }

    public float getGameViewYPos() {
        return this.gameViewYPos;
    }

    public Physics2D getPhysics2D() {
        return this.physics2D;
    }

    public Physics getPhysics() {
        return this.physics;
    }

    public List<GameObject> getAllGameObjects() {
        return this.gameObjects;
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

    public boolean doPhysics() {
        return doPhysics;
    }

    public void turnPhysicsOn() {
        this.doPhysics = true;
    }

    public void turnPhysicsOff() {
        this.doPhysics = false;
    }
}
