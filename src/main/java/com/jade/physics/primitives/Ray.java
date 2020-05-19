package com.jade.physics.primitives;

import org.joml.Vector3f;

public class Ray {
    private Vector3f origin;
    private Vector3f normalDirection;

    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.normalDirection = direction;
        this.normalDirection.normalize();
    }

    public static Ray fromPoints(Vector3f from, Vector3f to) {
        return new Ray(from, new Vector3f(to).sub(from));
    }

    public Vector3f origin() {
        return origin;
    }

    public Vector3f normalDirection() {
        return this.normalDirection;
    }
}
