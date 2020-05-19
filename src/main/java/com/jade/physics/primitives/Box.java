package com.jade.physics.primitives;

import com.jade.Component;
import org.joml.Vector3f;

public class Box extends Collider {
    private Vector3f size;
    private Vector3f halfSize;

    public Box(Vector3f size) {
        this.size = size;
        this.halfSize = new Vector3f(size).mul(0.5f);
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public float getHalfSize(int i) {
        return this.halfSize.get(i);
    }
}
