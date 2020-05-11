package com.jade.physics2d.primitives;

import org.joml.Vector2f;

public class Line2D {
    private Vector2f start;
    private Vector2f end;

    public Line2D(Vector2f start, Vector2f end) {
        this.start = start;
        this.end = end;
    }

    public float length() {
        return new Vector2f(end).sub(start).length();
    }

    public float lengthSquared() {
        return new Vector2f(end).sub(start).lengthSquared();
    }

    public Vector2f start() {
        return this.start;
    }

    public Vector2f end() {
        return this.end;
    }
}
