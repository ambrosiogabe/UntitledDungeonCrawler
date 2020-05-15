package com.jade.physics2d.rigidbody;

import org.joml.Vector2f;

public class Contact2D {
    private Vector2f contactPoint;
    private Vector2f collisionNormal;
    private float penetrationDepth;

    public Contact2D(Vector2f contactPoint, Vector2f collisionNormal, float penetrationDepth) {
        this.contactPoint = contactPoint;
        this.collisionNormal = collisionNormal;
        this.penetrationDepth = penetrationDepth;
    }
}
