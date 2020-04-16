package com.jade.ui.buttons;

import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.ui.Button;

public class PlayGameButton extends Button {

    public PlayGameButton(Sprite regular, Sprite hover, Sprite press) {
        super(regular, hover, press);
    }

    @Override
    public void clicked() {
        Window.changeScene(1);
    }
}
