package com.jade.physics.rigidbody;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CollisionManifold {
    private boolean colliding;
    private Vector3f normal;
    private float depth;
    private List<Vector3f> contacts;
    private boolean shouldFlipColliders;

    public CollisionManifold() {
        this.shouldFlipColliders = false;
        this.colliding = false;
        this.normal = new Vector3f();
        this.depth = 0f;
        this.contacts = new ArrayList<>();
    }

    public void reset() {
        this.shouldFlipColliders = false;
        this.colliding = false;
        this.normal.set(0, 1, 0);
        this.depth = 0;
        this.contacts.clear();
    }

    public void init(boolean colliding, Vector3f normal, float depth) {
        this.colliding = colliding;
        this.normal = normal;
        this.depth = depth;
    }

    public void init(boolean colliding, Vector3f normal) {
        this.colliding = colliding;
        this.normal = normal;
    }

    public void addContactPoint(Vector3f contact) {
        this.contacts.add(contact);
    }

    public List<Vector3f> contacts() {
        return this.contacts;
    }

    public Vector3f normal() {
        return this.normal;
    }

    public float depth() {
        return this.depth;
    }

    public boolean colliding() {
        return this.colliding;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public void setNormal(Vector3f normal) {
        this.normal.set(normal);
        this.normal.normalize();
    }

    public void setShouldFlipColliders(boolean val) {
        this.shouldFlipColliders = val;
    }

    public boolean shouldFlipColliders() {
        return this.shouldFlipColliders;
    }
}
