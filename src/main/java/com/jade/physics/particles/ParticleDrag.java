package com.jade.physics.particles;

import org.joml.Vector3f;

public class ParticleDrag implements ParticleForceGenerator {

    private float c_d, rho;

    public ParticleDrag(float c_d, float rho) {
        this.c_d = c_d;
        this.rho = rho;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        if (particle.getVelocity().lengthSquared() > 0) {
            Vector3f force = new Vector3f(particle.getVelocity());

            float speed = force.length() * force.length();
            float area = (float) Math.PI * 10 * 10 / 10000.0f;
            float dragCoeff = -0.5f * speed * area * c_d * rho;

            force.mul(dragCoeff);
            particle.addForce(force);
        }
    }
}
