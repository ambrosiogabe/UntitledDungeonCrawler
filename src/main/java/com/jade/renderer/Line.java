package com.jade.renderer;

import com.jade.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Line {
    private Vector3f from;
    private Vector3f to;
    private Vector3f color;
    private Vector3f[] verts = new Vector3f[8];
    private float stroke;
    private int lifetime;

    public Line(Vector3f from, Vector3f to, Vector3f color, float stroke, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.stroke = stroke;
        this.lifetime = lifetime;
//        this.normalXZ = new Vector3f(-(to.z - from.z), 0, (to.x - from.x));
//        this.normalXY = new Vector3f(-(to.y - from.y), (to.x - from.x), 0);
//        this.normalYZ = new Vector3f(0, -(to.z - from.z), (to.y - from.y));
//
//        this.normalXZ.y = this.normalYZ.y;
//        this.normalXY.z = this.normalXZ.z;
//        this.normalYZ.x = this.normalXY.x;
//
//        this.normalXZ.normalize();
//        this.normalXY.normalize();
//        this.normalYZ.normalize();
        Vector3f forward = new Vector3f(to).sub(from);
        float length = forward.length();
        forward.normalize();

        // If line is pointing in world UP or DOWN, make sure to cross with a different axis
        // to ensure no zero vectors
        Vector3f right = !forward.equals(Constants.UP) && !forward.equals(Constants.DOWN) ?
                new Vector3f(forward).cross(Constants.UP) : new Vector3f(forward).cross(Constants.BACK);
        Vector3f up = new Vector3f(right).cross(forward);

        float halfStroke = stroke / 2.0f;
        verts[0] = new Vector3f(from).add(new Vector3f(up).mul(halfStroke)).sub(new Vector3f(right).mul(halfStroke));
        verts[1] = new Vector3f(from).add(new Vector3f(up).mul(halfStroke)).add(new Vector3f(right).mul(halfStroke));
        verts[2] = new Vector3f(verts[0]).sub(new Vector3f(up).mul(stroke));
        verts[3] = new Vector3f(verts[1]).sub(new Vector3f(up).mul(stroke));

        Vector3f addVector = new Vector3f(forward.mul(length));
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
}
