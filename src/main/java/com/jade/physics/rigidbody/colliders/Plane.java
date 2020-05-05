package com.jade.physics.rigidbody.colliders;

import com.jade.Component;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Plane extends Collider {
    private Vector3f normal;
    float offset;

    public Plane(Vector3f normal, float offset) {
        this.normal = normal;
        this.offset = offset;
    }

    @Override
    public Matrix3f getInertiaTensor(float mass) {
        return JMath.createPlaneInertiaTensor(mass, new Vector2f(1, 1));
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public void drawGizmo() {
        DebugDraw.addBox(this.gameObject.transform.position, this.gameObject.transform.scale, this.gameObject.transform);
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public Vector3f getNormal() {
        return this.normal;
    }

    public float getOffset() {
        return this.offset;
    }
}
