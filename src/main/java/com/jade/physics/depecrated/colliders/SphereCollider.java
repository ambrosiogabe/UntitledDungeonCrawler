package com.jade.physics.depecrated.colliders;

import com.jade.Component;
import com.jade.physics.depecrated.collisions.CollisionData;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SphereCollider extends Collider {

    private float radius;

    public SphereCollider(float radius) {
        this.offset = new Matrix4f().identity();
        this.radius = radius;
    }

    @Override
    public Matrix3f getInertiaTensor(float mass) {
        return new Matrix3f().identity();
        //return JMath.createSphereInertiaTensor(mass, this.radius);
    }

    public void debugTestCollision(SphereCollider other, CollisionData data) {
        if (CollisionDetector.sphereAndSphere(this, other, data) > 0) {
            DebugDraw.addSphere(this.getPosition(), this.radius, 0.05f, Constants.COLOR3_RED);
        } else {
            DebugDraw.addSphere(this.getPosition(), this.radius, 0.05f, Constants.COLOR3_GREEN);
        }
    }

    public void debugTestPlaneCollision(Plane other, CollisionData data) {
        if (CollisionDetector.sphereAndHalfSpace(this, other, data) > 0) {
            DebugDraw.addSphere(this.getPosition(), this.radius, 0.05f, Constants.COLOR3_RED);
        } else {
            DebugDraw.addSphere(this.getPosition(), this.radius, 0.05f, Constants.COLOR3_GREEN);
        }
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector3f getPosition() {
        Vector4f tmp = new Vector4f(this.gameObject.transform.position, 0);
        this.offset.transform(tmp);

        return new Vector3f(tmp.x, tmp.y, tmp.z);
    }
}
