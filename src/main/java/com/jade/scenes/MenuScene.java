package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.*;
import com.jade.events.KeyListener;
import com.jade.ui.Button;
import com.jade.ui.buttons.ExitGameButton;
import com.jade.ui.buttons.PlayGameButton;
import com.jade.util.Constants;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;

public class MenuScene extends Scene {

    public MenuScene() {
        super();
    }

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

        UIObject startMenuButton = new UIObject("Start Button", new Vector3f(750.0f, 500.0f, 0.0f), new Vector3f(400, 100, 0f));
        Sprite noHover = new Sprite("images/button-no-hover.png");
        Sprite hover = new Sprite("images/button-hover.png");
        Sprite press = new Sprite("images/button-press.png");

        Button button = new PlayGameButton(noHover, hover, press);
        SpriteRenderer menuButtonRenderer = new SpriteRenderer(noHover);
        startMenuButton.addComponent(menuButtonRenderer);
        startMenuButton.addComponent(button);
        this.addUIObject(startMenuButton);

        UIObject exitGameButton = new UIObject("Exit Button", new Vector3f(750.0f, 300.0f, 0.0f), new Vector3f(400, 100, 0f));
        noHover = (Sprite)noHover.copy();
        Button exitButton = new ExitGameButton(noHover, (Sprite)hover.copy(), (Sprite)press.copy());
        exitGameButton.addComponent(exitButton);
        exitGameButton.addComponent(new SpriteRenderer(noHover));
        this.addUIObject(exitGameButton);

        UIObject startGameText = new UIObject("Start Label", new Vector3f(800.0f, 520.0f, 0.0f));
        FontRenderer playGameLabel = new FontRenderer(Constants.DEFAULT_FONT, "Start Game");
        playGameLabel.setColor(Constants.BLACK);
        startGameText.addComponent(playGameLabel);
        this.addUIObject(startGameText);

        UIObject exitGameText = new UIObject("Exit Label", new Vector3f(890.0f, 320.0f, 0.0f));
        FontRenderer exitGameLabel = new FontRenderer(Constants.DEFAULT_FONT, "Exit");
        exitGameLabel.setColor(Constants.BLACK);
        exitGameText.addComponent(exitGameLabel);
        this.addUIObject(exitGameText);

        UIObject gameTitle = new UIObject("Game Title", new Vector3f(545.0f, 800.0f, 0.0f));
        FontRenderer gameTitleLabel = new FontRenderer(Constants.EXTRA_LARGE_FONT, "Dungeon Crawler!");
        gameTitle.addComponent(gameTitleLabel);
        this.addUIObject(gameTitle);

        GameObject debugKeyController = new GameObject("Debug Key Controller", new Transform());
        debugKeyController.addComponent(new DebugKeyController());
        debugKeyController.setNonserializable();
        this.addGameObject(debugKeyController);

        for (int i=0; i < this.gameObjects.size(); i++) {
            GameObject g = this.gameObjects.get(i);
            g.start();
        }

        for (int i=0; i < this.uiObjects.size(); i++) {
            UIObject u = this.uiObjects.get(i);
            u.start();
        }
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_F1)) {
            Window.changeScene(2);
        }

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
