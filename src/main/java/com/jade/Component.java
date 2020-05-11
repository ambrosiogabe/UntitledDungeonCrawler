package com.jade;

import com.jade.file.Serialize;

public abstract class Component extends Serialize {

    public GameObject gameObject = null;

    public void start() {
        return;
    }

    public void update(float dt) {
        return;
    }

    public void imgui() {

    }

    public void drawGizmo() {

    }

    public abstract Component copy();
}
