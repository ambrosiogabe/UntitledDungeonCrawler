package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import org.joml.Vector3f;

public class TestScene2D extends Scene {

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

        Sprite defaultSprite = new Sprite("images/defaultSprite.png");

        GameObject ground = new GameObject("Ground", new Transform(new Vector3f(1920f / 2.0f, 0, 0), new Vector3f(3840, 50, 0)));
        ground.addComponent(new SpriteRenderer(defaultSprite));
        this.addGameObject(ground);

        GameObject boxOne = new GameObject("Box One", new Transform(new Vector3f(500f, 500f, 0f), new Vector3f(64f, 64f, 0f)));
        boxOne.addComponent(new SpriteRenderer(defaultSprite));
        boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        this.addGameObject(boxOne);
    }

    @Override
    public void update(float dt) {
        for (GameObject go : gameObjects) {
            go.update(dt);
        }
    }
}
