package com.jade.physics.rigidbody;

import org.joml.Vector3f;

public class Gravity implements ForceGenerator {
    Vector3f gravity;

    public Gravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    @Override
    public void updateForce(Rigidbody body, float duration) {
        // Check that we do not have infinite mass
        if (body.hasInfiniteMass()) return;

        body.addForce(new Vector3f(gravity).mul(body.getMass()));
    }
}
