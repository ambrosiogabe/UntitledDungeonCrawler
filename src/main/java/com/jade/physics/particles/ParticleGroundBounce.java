package com.jade.physics.particles;

public class ParticleGroundBounce implements ParticleForceGenerator {
    @Override
    public void updateForce(Particle particle, float duration) {
        if (particle.uiObject.transform.position.y - (particle.uiObject.transform.scale.y / 2.0f) < 0) {
            particle.getVelocity().y *= -0.8f;
            particle.uiObject.transform.position.y -= particle.uiObject.transform.position.y - (particle.uiObject.transform.scale.y / 2.0f);
        }
    }
}
