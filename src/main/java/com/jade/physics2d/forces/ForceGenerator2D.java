package com.jade.physics2d.forces;

import com.jade.physics2d.rigidbody.Rigidbody2D;

public interface ForceGenerator2D {
    void updateForce(Rigidbody2D body, float duration);
}