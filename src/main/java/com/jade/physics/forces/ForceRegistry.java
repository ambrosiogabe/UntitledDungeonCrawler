package com.jade.physics.forces;

import com.jade.physics.rigidbody.Rigidbody;

import java.util.ArrayList;
import java.util.List;

public class ForceRegistry {
    protected List<ForceRegistration> registry;

    public ForceRegistry() {
        this.registry = new ArrayList<>();
    }

    public void add(Rigidbody body, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(body, fg);
        registry.add(fr);
    }

    public void remove(Rigidbody body, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(body, fg);
        registry.remove(fr);
    }

    public void clear() {
        registry.clear();
    }

    public void updateForces(float dt) {
        for (ForceRegistration fr : registry) {
            fr.fg.updateForce(fr.body, dt);
        }
    }

    public void zeroForces() {
        for (ForceRegistration fr : registry) {
            fr.body.zeroForces();
        }
    }
}
