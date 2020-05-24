package com.jade.renderer;

import com.jade.util.Constants;
import org.joml.Vector3f;

public class Line {
    private Vector3f from;
    private Vector3f to;
    private Vector3f color;
    private Vector3f[] verts = new Vector3f[8];
    private float stroke;
    private int lifetime;

    public Line(Vector3f from, Vector3f to) {
        this.from = from;
        this.to = to;
        this.color = null;
        this.stroke = 0f;
        this.lifetime = 0;
    }

    public Line(Vector3f from, Vector3f to, Vector3f color, float stroke, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.stroke = stroke;
        this.lifetime = lifetime;

        // Calculate the forward, right, and up vectors
        Vector3f forward = new Vector3f(to).sub(from);
        float length = forward.length();
        forward.normalize();

        // Make sure to cross with non-parallel axis. We check which axis is greater
        // then our 'tolerance' angle, then cross with that axis
        float tolerance = 0.1f;
        Vector3f right;
        if (Math.abs(forward.dot(Constants.UP)) < tolerance) {
            right = new Vector3f(forward).cross(Constants.UP);
        } else if (Math.abs(forward.dot(Constants.FORWARD)) < tolerance) {
            right = new Vector3f(forward).cross(Constants.FORWARD);
        } else {
            right = new Vector3f(forward).cross(Constants.RIGHT);
        }
        Vector3f up = new Vector3f(right).cross(forward);

        // Create 8 vertices to store the rectangular prism for the line
        float halfStroke = stroke / 2.0f;
        verts[0] = new Vector3f(from).add(new Vector3f(up).mul(halfStroke)).sub(new Vector3f(right).mul(halfStroke));
        verts[1] = new Vector3f(from).add(new Vector3f(up).mul(halfStroke)).add(new Vector3f(right).mul(halfStroke));
        verts[2] = new Vector3f(verts[0]).sub(new Vector3f(up).mul(stroke));
        verts[3] = new Vector3f(verts[1]).sub(new Vector3f(up).mul(stroke));

        Vector3f addVector = new Vector3f(forward).mul(length);
        verts[4] = new Vector3f(verts[0]).add(addVector);
        verts[5] = new Vector3f(verts[1]).add(addVector);
        verts[6] = new Vector3f(verts[2]).add(addVector);
        verts[7] = new Vector3f(verts[3]).add(addVector);
    }

    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }

    public Vector3f getFrom() {
        return this.from;
    }

    public Vector3f getTo() {
        return this.to;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public float getStroke() {
        return this.stroke;
    }

    public Vector3f[] getVerts() {
        return this.verts;
    }

    public Vector3f end() {
        return this.to;
    }

    public Vector3f start() {
        return this.from;
    }

    public float lengthSquared() {
        return new Vector3f(this.to).sub(this.from).lengthSquared();
    }

    public float length() {
        return new Vector3f(this.to).sub(this.from).length();
    }
}
