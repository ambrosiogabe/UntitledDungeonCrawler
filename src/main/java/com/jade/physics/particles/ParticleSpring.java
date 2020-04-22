package com.jade.physics.particles;


import org.joml.Vector2f;
import org.joml.Vector3f;

public class ParticleSpring implements ParticleForceGenerator {
    Particle other;
    float springConstant;
    float restLength;

    public ParticleSpring(Particle other, float springConstant, float restLength) {
        this.other = other;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        // Calculate the vector of the spring
        Vector3f force = new Vector3f(particle.getPosition());
        force.add(other.getPosition().mul(-1));

        // Calculate the magnitude of the force
        float magnitude = force.length();
        magnitude = Math.abs(magnitude - restLength);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalize();
        force.mul(magnitude);
        particle.addForce(force);
    }
}
