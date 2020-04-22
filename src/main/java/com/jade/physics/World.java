package com.jade.physics;

import com.jade.physics.rigidbody.ForceRegistry;
import com.jade.physics.rigidbody.Rigidbody;

import java.util.ArrayList;
import java.util.List;

public class World {
    private List<Rigidbody> bodies;
    private ForceRegistry registry;

    public World() {
        registry = new ForceRegistry();
        bodies = new ArrayList<>();
    }

    public void startFrame() {
        for (Rigidbody rb : bodies) {
            rb.clearAccumulators();
            rb.calculateDerivedData();
        }
    }

    public void update(float dt) {
        for (Rigidbody rb : bodies) {
            rb.update(dt);
        }
    }

    public void runPhysics(float dt) {
        // Apply the force generators
        registry.updateForces(dt);

        // Update the objects
        update(dt);
    }
}
