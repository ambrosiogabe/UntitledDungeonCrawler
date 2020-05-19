package com.jade.physics.primitives;

import org.joml.Vector3f;

public class Triangle {
    private float[] pointValues = new float[9];

    public Triangle(Vector3f p1, Vector3f p2, Vector3f p3) {
        this.pointValues[0] = p1.x;
        this.pointValues[1] = p1.y;
        this.pointValues[2] = p1.z;

        this.pointValues[3] = p2.x;
        this.pointValues[4] = p2.y;
        this.pointValues[5] = p2.z;

        this.pointValues[6] = p3.x;
        this.pointValues[7] = p3.y;
        this.pointValues[8] = p3.z;
    }

    public Vector3f a() {
        return new Vector3f(this.pointValues[0], this.pointValues[1], this.pointValues[2]);
    }

    public Vector3f b() {
        return new Vector3f(this.pointValues[3], this.pointValues[4], this.pointValues[5]);
    }

    public Vector3f c() {
        return new Vector3f(this.pointValues[6], this.pointValues[7], this.pointValues[8]);
    }

    public Vector3f[] points() {
        Vector3f[] points = {
                a(), b(), c()
        };
        return points;
    }
}
