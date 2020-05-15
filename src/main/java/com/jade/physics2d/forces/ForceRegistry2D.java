package com.jade.physics2d.forces;

import com.jade.physics2d.rigidbody.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

public class ForceRegistry2D {
    protected List<ForceRegistration2D> registry;

    public ForceRegistry2D() {
        this.registry = new ArrayList<>();
    }

    public void add(Rigidbody2D body, ForceGenerator2D fg) {
        ForceRegistration2D fr = new ForceRegistration2D(body, fg);
        this.registry.add(fr);
    }

    public void remove(Rigidbody2D body, ForceGenerator2D fg) {
        ForceRegistration2D fr = new ForceRegistration2D(body, fg);
        this.registry.remove(fr);
    }

    public void clear() {
        this.registry.clear();
    }

    public void updateForces(float dt) {
        for (ForceRegistration2D fr : registry) {
            fr.fg().updateForce(fr.body(), dt);
        }
    }

    public void zeroForces() {
        for (ForceRegistration2D fr : registry) {
            fr.body().zeroForces();
        }
    }
}
