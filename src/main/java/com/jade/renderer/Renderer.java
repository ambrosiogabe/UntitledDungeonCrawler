package com.jade.renderer;

import com.jade.Camera;
import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.components.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private final int MAX_BATCH_SIZE = 100;
    private List<GameObject> gameObjects;
    private List<UIBatcher> uiBatches;

    private Camera camera;

    public Renderer(Camera camera) {
        this.camera = camera;
    }

    public void reset() {
        for (GameObject g : gameObjects) {
            Model model;
            if ((model = g.getComponent(Model.class)) != null) {
                model.clear();
            }
        }
        gameObjects.clear();
        for (UIBatcher batch : uiBatches) {
            batch.clear();
        }
        uiBatches.clear();
    }

    public void init() {
        this.gameObjects = new ArrayList<>();
        this.uiBatches = new ArrayList<>();
    }

    public void addGameObject(GameObject g) {
        this.gameObjects.add(g);
    }

    public void addUIObject(UIObject u) {
        SpriteRenderer spriteRenderer;
        boolean wasAdded = false;

        if ((spriteRenderer = u.getComponent(SpriteRenderer.class)) != null) {
            for (int i = 0; i < uiBatches.size(); i++) {
                UIBatcher batch = uiBatches.get(i);
                if (batch.hasRoom() && batch.hasTexture(spriteRenderer.getSprite().getTexture())) {
                    batch.add(spriteRenderer);
                    wasAdded = true;
                    break;
                }
            }

            if (!wasAdded) {
                UIBatcher batch = new UIBatcher(MAX_BATCH_SIZE, this, 0);
                batch.start();
                batch.add(spriteRenderer);
                uiBatches.add(batch);
                wasAdded = true;
            }
        } else if (u.getComponent(FontRenderer.class) != null) {
            wasAdded = true;
        }

        assert wasAdded : "Object was never added to renderer.";
    }

    public void render() {
        for (GameObject go : gameObjects) {
            Model model = go.getComponent(Model.class);
            Billboard billboard;
            if (model != null) {
                model.render();
            } else if ((billboard = go.getComponent(Billboard.class)) != null) {
                billboard.render();
            }
        }

        glDisable(GL_DEPTH_TEST);
        for (UIBatcher batch : uiBatches) {
            batch.render();
        }
        glEnable(GL_DEPTH_TEST);
    }

    public Camera camera() {
        return this.camera;
    }
}
