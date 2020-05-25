package com.jade.physics.primitives;

import org.joml.Vector3f;

public class RaycastResult {
    private Vector3f point;
    private Vector3f normal;
    private float t;
    private boolean hit;

    public RaycastResult() {
        this.point = new Vector3f();
        this.normal = new Vector3f();
        this.t = t;
        this.hit = false;
    }

    public RaycastResult(Vector3f point, Vector3f normal, float t, boolean hit) {
        this.point = new Vector3f(point);
        this.normal = new Vector3f(normal);
        this.t = t;
        this.hit = hit;
    }

    public void init(Vector3f point, Vector3f normal, float t, boolean hit) {
        this.point.set(point);
        this.normal.set(normal);
        this.t = t;
        this.hit = hit;
    }

    public Vector3f point() {
        return this.point;
    }

    public Vector3f normal() {
        return this.normal;
    }

    public float t() {
        return this.t;
    }

    public boolean hit() {
        return this.hit;
    }

    public static void reset(RaycastResult result) {
        if (result != null) {
            result.point.zero();
            result.normal.set(0, 0, 1);
            result.t = -1;
            result.hit = false;
        }
    }
}
