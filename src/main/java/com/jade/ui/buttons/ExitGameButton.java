package com.jade.ui.buttons;

import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.ui.Button;

public class ExitGameButton extends Button {

    public ExitGameButton(Sprite regular, Sprite hover, Sprite press) {
        super(regular, hover, press);
    }

    @Override
    public void clicked() {
        Window.stop();
    }
}
