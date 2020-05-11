package com.jade.renderer;

import com.jade.Camera;
import com.jade.GameObject;
import com.jade.components.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

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
        SpriteRenderer spriteRenderer = g.getComponent(SpriteRenderer.class);
        FontRenderer fontRenderer;

        // Check if it is 2D object, if it has no 2D rendering components
        // it must be a 3D object
        if (spriteRenderer != null) {
            boolean wasAdded = false;
            for (int i = 0; i < uiBatches.size(); i++) {
                UIBatcher batch = uiBatches.get(i);
                if (batch.hasRoom() && batch.zIndex() == spriteRenderer.zIndex() && batch.hasTexture(spriteRenderer.getSprite().getTexture())) {
                    batch.add(spriteRenderer);
                    wasAdded = true;
                    break;
                }
            }

            if (!wasAdded) {
                UIBatcher batch = new UIBatcher(MAX_BATCH_SIZE, this, spriteRenderer.zIndex());
                batch.start();
                batch.add(spriteRenderer);
                uiBatches.add(batch);
                uiBatches.sort(Collections.reverseOrder());
            }
        } else if((fontRenderer = g.getComponent(FontRenderer.class)) != null) {
            // Must be a 2D object
            return;
        } else {
            this.gameObjects.add(g);
        }
    }

    public void deleteGameObject(GameObject g) {
        SpriteRenderer spriteRenderer = g.getComponent(SpriteRenderer.class);
        FontRenderer fontRenderer;

        // Check if it is 2D object, if it has no 2D rendering components
        // it must be a 3D object
        if (spriteRenderer != null) {
            for (int i = 0; i < uiBatches.size(); i++) {
                UIBatcher batch = uiBatches.get(i);
                if (batch.hasSpriteRenderer(spriteRenderer)) {
                    batch.deleteSpriteRenderer(spriteRenderer);
                    break;
                }
            }
        } else if((fontRenderer = g.getComponent(FontRenderer.class)) != null) {
            // Must be a 2D object
            return;
        } else {
            this.gameObjects.remove(g);
        }
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

        for (UIBatcher batch : uiBatches) {
            batch.render();
        }
    }

    public Camera camera() {
        return this.camera;
    }
}
