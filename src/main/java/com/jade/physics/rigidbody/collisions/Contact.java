package com.jade.physics.rigidbody.collisions;

import com.jade.physics.rigidbody.Rigidbody;
import org.joml.Vector3f;

public class Contact {
    Rigidbody bodyOne, bodyTwo;

    private Vector3f contactPoint;
    private Vector3f contactNormal;
    private float penetrationDepth;
    private float friction;
    private float restitution;

    public Contact() {
        this.bodyTwo = null;
        this.bodyOne = null;
        this.contactPoint = null;
        this.contactNormal = null;
        this.penetrationDepth = 0;
        this.friction = 0;
        this.restitution = 0;
    }

    public Contact(Vector3f contactPoint, Vector3f contactNormal, float penetrationDepth) {
        this.contactPoint = contactPoint;
        this.contactNormal = contactNormal;
        this.penetrationDepth = penetrationDepth;
    }

    public Vector3f getContactPoint() {
        return this.contactPoint;
    }

    public void setContactPoint(Vector3f point) {
        this.contactPoint = point;
    }

    public Vector3f getContactNormal() {
        return this.contactNormal;
    }

    public void setContactNormal(Vector3f normal) {
        this.contactNormal = normal;
    }

    public float getPenetrationDepth() {
        return this.penetrationDepth;
    }

    public void setPenetrationDepth(float val) {
        this.penetrationDepth = val;
    }

    public void setBodyData(Rigidbody one, Rigidbody two, float friction, float restitution) {
        this.bodyOne = one;
        this.bodyTwo = two;
        this.friction = friction;
        this.restitution = restitution;
    }
}
