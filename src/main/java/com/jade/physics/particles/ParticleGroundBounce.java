package com.jade.physics.particles;

public class ParticleGroundBounce implements ParticleForceGenerator {
    @Override
    public void updateForce(Particle particle, float duration) {
        if (particle.gameObject.transform.position.y - (particle.gameObject.transform.scale.y / 2.0f) < 0) {
            particle.getVelocity().y *= -0.8f;
            particle.gameObject.transform.position.y -= particle.gameObject.transform.position.y - (particle.gameObject.transform.scale.y / 2.0f);
        }
    }
}
