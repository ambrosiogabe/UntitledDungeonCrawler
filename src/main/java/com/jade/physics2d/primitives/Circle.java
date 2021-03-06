package com.jade.physics2d.primitives;

import com.jade.Component;
import com.jade.util.JMath;

public class Circle extends Collider2D {
    private float radius;

    public Circle(float radius) {
        this.radius = radius;
    }

    @Override
    public void update(float dt) {

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

    @Override
    public float getInertiaTensor(float mass) {
        return JMath.createCircleInertiaTensor(mass, this.radius);
    }
}
