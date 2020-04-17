package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.components.*;
import com.jade.ui.Button;
import com.jade.ui.buttons.ExitGameButton;
import com.jade.ui.buttons.PlayGameButton;
import com.jade.util.Constants;
import com.jade.util.Time;
import org.joml.Vector3f;

public class MenuScene extends Scene {

    public MenuScene() {
        super();
    }

    @Override
    public void init() {
        GameObject testWall = new GameObject("Test wall", new Transform(new Vector3f(-0.0f, 0.0f, 0.0f)));
        Model test = new Model("mesh-ext/brickWall.fbx");
        testWall.addComponent(test);
        this.addGameObject(testWall);

        UIObject startMenuButton = new UIObject(new Vector3f(750.0f, 500.0f, 0.0f), new Vector3f(400, 100, 0f));
        Sprite noHover = new Sprite("images/button-no-hover.png");
        Sprite hover = new Sprite("images/button-hover.png");
        Sprite press = new Sprite("images/button-press.png");

        Button button = new PlayGameButton(noHover, hover, press);
        SpriteRenderer menuButtonRenderer = new SpriteRenderer(noHover);
        startMenuButton.addComponent(menuButtonRenderer);
        startMenuButton.addComponent(button);
        this.addUIObject(startMenuButton);

        UIObject exitGameButton = new UIObject(new Vector3f(750.0f, 300.0f, 0.0f), new Vector3f(400, 100, 0f));
        noHover = (Sprite)noHover.copy();
        Button exitButton = new ExitGameButton(noHover, (Sprite)hover.copy(), (Sprite)press.copy());
        exitGameButton.addComponent(exitButton);
        exitGameButton.addComponent(new SpriteRenderer(noHover));
        this.addUIObject(exitGameButton);

        UIObject startGameText = new UIObject(new Vector3f(800.0f, 520.0f, 0.0f));
        FontRenderer playGameLabel = new FontRenderer(Constants.DEFAULT_FONT, "Start Game");
        playGameLabel.setColor(Constants.BLACK);
        startGameText.addComponent(playGameLabel);
        this.addUIObject(startGameText);

        UIObject exitGameText = new UIObject(new Vector3f(890.0f, 320.0f, 0.0f));
        FontRenderer exitGameLabel = new FontRenderer(Constants.DEFAULT_FONT, "Exit");
        exitGameLabel.setColor(Constants.BLACK);
        exitGameText.addComponent(exitGameLabel);
        this.addUIObject(exitGameText);

        UIObject gameTitle = new UIObject(new Vector3f(545.0f, 800.0f, 0.0f));
        FontRenderer gameTitleLabel = new FontRenderer(Constants.EXTRA_LARGE_FONT, "Dungeon Crawler!");
        gameTitle.addComponent(gameTitleLabel);
        this.addUIObject(gameTitle);

        for (UIObject u : this.uiObjects) {
            u.start();
        }
    }

    @Override
    public void update(float dt) {
        float speed = 0.5f;
        float radius = 40.0f;
        this.camera().position.x = (float)Math.sin(Time.getTime() * speed) * radius;
        this.camera().position.z = (float)Math.cos(Time.getTime() * speed) * radius;

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
