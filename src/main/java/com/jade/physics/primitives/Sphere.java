package com.jade.physics.primitives;

import com.jade.Component;

public class Sphere extends Collider {
    private float radius;

    public Sphere(float radius) {
        this.radius = radius;
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public float radius() {
        return this.radius;
    }
}
