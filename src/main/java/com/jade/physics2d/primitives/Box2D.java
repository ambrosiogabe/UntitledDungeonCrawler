package com.jade.physics2d.primitives;

import com.jade.Component;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Box2D extends Collider2D {

    private Vector2f center;
    private Vector2f size;

    public Box2D() {
        this.center = null;
        this.size = null;
    }

    public Box2D(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.center = new Vector2f(min).add(new Vector2f(this.size).div(2.0f));
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public Vector2f getMin() {
        if (gameObject != null) {
            return JMath.vector2fFrom3f(this.gameObject.transform.position).sub(JMath.vector2fFrom3f(this.gameObject.transform.scale).div(2.0f));
        } else {
            return new Vector2f(this.center).sub(new Vector2f(this.size).div(2.0f));
        }
    }

    public Vector2f getMax() {
        if (gameObject != null) {
            return JMath.vector2fFrom3f(this.gameObject.transform.position).add(JMath.vector2fFrom3f(this.gameObject.transform.scale).div(2.0f));
        } else {
            return new Vector2f(this.center).add(new Vector2f(this.size).div(2.0f));
        }
    }

    public Vector2f getHalfSize() {
        if (gameObject != null) {
            return new Vector2f(this.gameObject.transform.scale.x / 2.0f, this.gameObject.transform.scale.y / 2.0f);
        } else {
            return new Vector2f(this.size).div(2.0f);
        }
    }

    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (gameObject != null) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, this.gameObject.transform.rotation.z, this.gameObject.transform.position);
            }
        }

        return vertices;
    }

    public float getRotation() {
        if (this.gameObject == null) {
            return 0;
        } else {
            return this.gameObject.transform.rotation.z;
        }
    }

    public Vector3f getCenter() {
        if (this.gameObject == null) {
            return JMath.vector3fFrom2f(this.center);
        } else {
            return this.gameObject.transform.position;
        }
    }

    @Override
    public float getInertiaTensor(float mass) {
        return JMath.createSquareInertiaTensor(mass, this.gameObject == null ? this.size : JMath.vector2fFrom3f(this.gameObject.transform.scale));
    }
}
