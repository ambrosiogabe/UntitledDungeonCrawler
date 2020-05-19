package com.jade.physics.primitives;

import com.jade.Component;
import org.joml.Vector3f;

public class Plane extends Collider {
    private Vector3f normal;
    private float distanceFromOrigin;

    public Plane(Vector3f normal, float distanceFromOrigin) {
        this.normal = normal;
        this.normal.normalize();
        this.distanceFromOrigin = distanceFromOrigin;
    }

    public static float planeEquation(Vector3f point, Plane plane) {
        return point.dot(plane.normal) - plane.distanceFromOrigin;
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public Vector3f normal() {
        return this.normal;
    }

    public float distanceFromOrigin() {
        return distanceFromOrigin;
    }
}
