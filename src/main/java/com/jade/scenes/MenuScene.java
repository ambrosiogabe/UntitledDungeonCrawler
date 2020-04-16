package com.jade.scenes;

import com.jade.GameObject;
import com.jade.UIObject;
import com.jade.components.FontRenderer;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.ui.Button;
import com.jade.util.Constants;
import org.joml.Vector3f;

public class MenuScene extends Scene {

    float timeToChangeText = 2f;
    UIObject sampleText;
    int i = 0;
    int numCharactersPerLine = 30;

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

        sampleText = new UIObject(new Vector3f(-90.0f));
        sampleText.addComponent(new FontRenderer(Constants.DEFAULT_FONT, "Some Sample Text.\n That is a big string"));
        this.addUIObject(sampleText);

        for (UIObject u : this.uiObjects) {
            u.start();
        }
    }

    @Override
    public void update(float dt) {
        timeToChangeText -= dt;
        if (timeToChangeText <= 0.0f) {
            timeToChangeText = 2f;
            FontRenderer font = sampleText.getComponent(FontRenderer.class);
            font.setText(i == 0 ? "Smaller string." : "Another very large string!\nSTress testing is fun :)!!!");
            i = i == 0 ? 1 : 0;
//            font.setText(font.getText() + (char)( (Math.random() * ('z' - 'a')) + 'a'));
//            if (font.getText().length() % numCharactersPerLine == 0) {
//                font.setText(font.getText() + '\n');
//            }
        }

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
