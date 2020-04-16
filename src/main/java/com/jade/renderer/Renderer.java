package com.jade.renderer;

import com.jade.Camera;
import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.components.FontRenderer;
import com.jade.components.Mesh;
import com.jade.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 100;
    private List<GameObject> gameObjects;
    private List<UIBatcher> uiBatches;

    private Camera camera;

    public Renderer(Camera camera) {
        this.camera = camera;
    }

    public void init() {
        this.gameObjects = new ArrayList<>();
        this.uiBatches = new ArrayList<>();
    }

    public void addGameObject(GameObject g) {
        this.gameObjects.add(g);
    }

    public void addUIObject(UIObject u) {
        SpriteRenderer spriteRenderer = u.getComponent(SpriteRenderer.class);
        FontRenderer fontRenderer = u.getComponent(FontRenderer.class);
        boolean wasAdded = false;

        if (spriteRenderer != null) {
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
        } else if (fontRenderer != null) {
            wasAdded = true;
            for (UIObject uiObject : fontRenderer.getUIObjects()) {
                addUIObject(uiObject);
            }
        }

        assert wasAdded : "Object was never added to renderer.";
    }

    public void render() {
//        for (GameObject go : gameObjects) {
//            Mesh mesh;
//            if ((mesh = go.getComponent(Mesh.class)) != null) {
//                glBindVertexArray(mesh.getVAO());
//                for (int i=0; i < mesh.getVertexAttributeLength(); i++) {
//                    glEnableVertexAttribArray(i);
//                }
//
//                glDrawElements(GL_TRIANGLES, mesh.getTriangleCount(), GL_UNSIGNED_INT, 0);
//
//                for (int i=0; i < mesh.getVertexAttributeLength(); i++) {
//                    glDisableVertexAttribArray(i);
//                }
//            }
//        }

        for (UIBatcher batch : uiBatches) {
            batch.render();
        }
    }

    public Camera camera() {
        return this.camera;
    }
}
