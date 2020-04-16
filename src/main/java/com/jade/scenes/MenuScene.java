package com.jade.scenes;

import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.ui.Button;
import com.jade.util.Constants;
import org.joml.Vector3f;

public class MenuScene extends Scene {

    public MenuScene() {
        super();
    }

    @Override
    public void init() {
        UIObject startMenuButton = new UIObject(new Vector3f(0.0f), new Vector3f(256, 64, 0f));
        Sprite noHover = new Sprite("images/button-no-hover.png");
        Sprite hover = new Sprite("images/button-hover.png");
        Sprite press = new Sprite("images/button-press.png");

        Button button = new Button(noHover, hover, press);
        SpriteRenderer menuButtonRenderer = new SpriteRenderer(noHover);
        startMenuButton.addComponent(menuButtonRenderer);
        startMenuButton.addComponent(button);
        this.addUIObject(startMenuButton);

        for (UIObject u : this.uiObjects) {
            u.start();
        }
    }

    @Override
    public void update(float dt) {
        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
