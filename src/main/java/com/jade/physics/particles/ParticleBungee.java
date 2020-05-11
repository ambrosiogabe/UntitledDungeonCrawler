package com.jade.physics.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ParticleBungee implements ParticleForceGenerator {
    private Particle other;
    private float springConstant;
    private float restLength;

    public ParticleBungee(Particle other, float springConstant, float restLength) {
        this.other = other;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        // Calculate the force of the spring
        Vector3f force = new Vector3f(particle.gameObject.transform.position);
        force.add(other.gameObject.transform.position.mul(-1));

        // Check if the bungee is compressed
        float magnitude = force.length();
        if (magnitude <= restLength) return;

        // Calculate the magnitude of the force
        magnitude = springConstant * (restLength - magnitude);

        // Calculate the final force and apply it
        force.normalize();
        force.mul(magnitude);
        particle.addForce(force);
    }
}
