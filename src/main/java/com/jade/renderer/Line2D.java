package com.jade.renderer;

import com.jade.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {
    private Vector2f from;
    private Vector2f to;
    private Vector3f color;
    private Vector2f[] verts = new Vector2f[4];
    private float stroke;
    private int lifetime;

    public Line2D(Vector2f from, Vector2f to, Vector3f color, float stroke, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.stroke = stroke;
        this.lifetime = lifetime;

        // Calculate the forward, right, and up vectors
        Vector2f line = new Vector2f(from).sub(to);
        Vector2f normal = new Vector2f(line.y, -line.x);
        normal.normalize();

        // Create 8 vertices to store the rectangular prism for the line
        float halfStroke = stroke / 2.0f;
        verts[0] = new Vector2f(from).add(new Vector2f(normal).mul(halfStroke));
        verts[1] = new Vector2f(from).add(new Vector2f(normal).mul(-halfStroke));
        verts[2] = new Vector2f(verts[0]).sub(new Vector2f(line));
        verts[3] = new Vector2f(verts[1]).sub(new Vector2f(line));
    }

    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }

    public Vector2f getFrom() {
        return this.from;
    }

    public Vector2f getTo() {
        return this.to;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public float getStroke() {
        return this.stroke;
    }

    public Vector2f[] getVerts() {
        return this.verts;
    }
}
