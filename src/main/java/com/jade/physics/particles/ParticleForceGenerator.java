package com.jade.physics.particles;

public interface ParticleForceGenerator {
    void updateForce(Particle particle, float duration);
}
