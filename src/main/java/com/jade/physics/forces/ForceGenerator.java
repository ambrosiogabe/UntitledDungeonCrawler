package com.jade.physics.forces;

import com.jade.physics.rigidbody.Rigidbody;

public interface ForceGenerator {
    void updateForce(Rigidbody body, float duration);
}
