package com.jade.physics2d.primitives;

import com.jade.Component;
import com.jade.GameObject;
import org.joml.Vector2f;

public class Raycast2D extends Collider2D {
    private Vector2f origin;
    private Vector2f direction;
    private float maxDistance;
    private GameObject ignore;

    public Raycast2D(Vector2f origin, Vector2f direction, float maxDistance, GameObject ignore) {
        this.origin = origin;
        this.direction = direction;
        direction.normalize();

        this.maxDistance = maxDistance;
        this.ignore = ignore;
    }

    @Override
    public float getInertiaTensor(float mass) {
        return 0;
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public Vector2f origin() {
        return this.origin;
    }

    public Vector2f direction() {
        return this.direction;
    }

    public float maxDistance() {
        return this.maxDistance;
    }

    public GameObject ignore() {
        return this.ignore;
    }
}
