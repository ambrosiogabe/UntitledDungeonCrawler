package com.jade.physics2d.forces;

import com.jade.physics2d.rigidbody.Rigidbody2D;
import org.joml.Vector2f;

public class Gravity2D implements ForceGenerator2D {

    private Vector2f gravity;

    public Gravity2D(Vector2f gravity) {
        this.gravity = gravity;
    }

    @Override
    public void updateForce(Rigidbody2D body, float duration) {
        if (body.hasInfiniteMass()) return;
        Vector2f force = new Vector2f(gravity).mul(body.mass());

        body.addLinearForce(force);
    }
}
