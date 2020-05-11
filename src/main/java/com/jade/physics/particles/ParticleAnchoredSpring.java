package com.jade.physics.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ParticleAnchoredSpring implements ParticleForceGenerator {
    private Vector3f anchor;
    private float springConstant;
    private float restLength;

    public ParticleAnchoredSpring(Vector3f anchor, float springConstant, float restLength) {
        this.anchor = anchor;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        // Calculate vector of the spring
        Vector3f force = new Vector3f(particle.gameObject.transform.position);
        Vector3f tmpAnchor = new Vector3f(anchor);
        force.add(tmpAnchor.mul(-1));

        // Calculate magnitude of the force
        float magnitude = force.length();
        magnitude = (restLength - magnitude) * springConstant;

        // Calculate the final force and apply it
        force.normalize();
        force.mul(magnitude);
        particle.addForce(force);
    }
}
