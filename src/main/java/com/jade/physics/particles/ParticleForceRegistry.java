package com.jade.physics.particles;

import com.jade.physics.structs.ParticleForceRegistration;

import java.util.ArrayList;
import java.util.List;

public class ParticleForceRegistry {
    protected List<ParticleForceRegistration> registry;

    public ParticleForceRegistry() {
        this.registry = new ArrayList<>();
    }

    public void add(Particle particle, ParticleForceGenerator fg) {
        ParticleForceRegistration fr = new ParticleForceRegistration(particle, fg);
        registry.add(fr);
    }

    public void remove(Particle particle, ParticleForceGenerator fg) {
        ParticleForceRegistration fr = new ParticleForceRegistration(particle, fg);
        registry.remove(fr);
    }

    public void clear() {
        registry.clear();
    }

    public void updateForces(float dt) {
        for (ParticleForceRegistration fr : registry) {
            fr.fg.updateForce(fr.particle, dt);
        }
    }
}