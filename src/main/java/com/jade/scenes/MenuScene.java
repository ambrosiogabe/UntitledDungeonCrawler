package com.jade.scenes;

import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.util.Constants;
import org.joml.Vector3f;

public class MenuScene extends Scene {
    UIObject startMenuButton;

    public MenuScene() {
        super();
    }

    @Override
    public void init() {
        startMenuButton = new UIObject(new Vector3f(0.0f), new Vector3f(0.5f, 0.5f, 0.0f));
        SpriteRenderer menuButtonRenderer = new SpriteRenderer();
        menuButtonRenderer.setColor(Constants.RED);
        startMenuButton.addComponent(menuButtonRenderer);
        this.addUIObject(startMenuButton);

        for (UIObject u : this.uiObjects) {
            u.start();
        }
    }

    @Override
    public void update(float dt) {
        startMenuButton.update(dt);
        startMenuButton.transform.position.x += 0.1f * dt;

    }
}
