package com.jade.physics.rigidbody;

import org.joml.Vector3f;

public class TempForce implements ForceGenerator {
    @Override
    public void updateForce(Rigidbody body, float duration) {
        body.addForce(new Vector3f(0, 10000, 10000));
    }
}
