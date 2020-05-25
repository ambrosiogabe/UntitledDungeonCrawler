package com.jade.physics;

import com.jade.physics.coRigidbody.Rigidbody;
import com.jade.physics.primitives.Collider;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private List<Rigidbody> bodies;
    private List<Collider> constraints;

    public PhysicsSystem() {
        bodies = new ArrayList<>();
        constraints = new ArrayList<>();
    }

    public void update(float deltaTime) {
        int size = bodies.size();
        for (int i=0; i < size; i++) {
            bodies.get(i).applyForces();
        }

        for (int i=0; i < size; i++) {
            bodies.get(i).physicsUpdate(deltaTime);
        }

        for (int i=0; i < size; i++) {
            bodies.get(i).solveConstraints(constraints);
        }
    }

    public void addRigidbody(Rigidbody body) {
        this.bodies.add(body);
    }

    public void addConstraint(Collider constraint) {
        this.constraints.add(constraint);
    }

    public void clearRigidbodies() {
        this.bodies.clear();
    }

    public void clearConstraints() {
        this.constraints.clear();
    }
}
