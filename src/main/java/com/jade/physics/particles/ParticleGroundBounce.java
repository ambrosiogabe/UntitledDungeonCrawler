package com.jade.physics.particles;

public class ParticleGroundBounce implements ParticleForceGenerator {
    @Override
    public void updateForce(Particle particle, float duration) {
        if (particle.getPosition().y < 0) {
            particle.getVelocity().y *= -0.8f;
            particle.getPosition().y -= particle.getPosition().y;
        }
    }
}
