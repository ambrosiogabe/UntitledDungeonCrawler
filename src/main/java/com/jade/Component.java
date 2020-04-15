package com.jade;

import com.jade.file.Serialize;

public abstract class Component extends Serialize {

    public GameObject gameObject = null;
    public UIObject uiObject = null;

    public void update(double dt) {
        return;
    }

    public void start() {
        return;
    }

    public void update(float dt) {
        return;
    }

    public abstract Component copy();
}
