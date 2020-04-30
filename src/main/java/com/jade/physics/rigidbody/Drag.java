package com.jade.physics.rigidbody;

import org.joml.Vector3f;

public class Drag implements ForceGenerator {

    private float damping;

    public Drag(float damping) {
        this.damping = damping;
    }

    @Override
    public void updateForce(Rigidbody body, float duration) {
        body.addForce(new Vector3f(body.getVelocity()).mul(damping));
    }
}
