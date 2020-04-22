package com.jade.physics.particles;

import org.joml.Vector3f;

public class ParticleGravity implements ParticleForceGenerator {

    private Vector3f gravity;

    public ParticleGravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        if (!particle.hasFiniteMass()) return;

        particle.addForce(new Vector3f(gravity).mul(particle.getMass()));
    }
}
