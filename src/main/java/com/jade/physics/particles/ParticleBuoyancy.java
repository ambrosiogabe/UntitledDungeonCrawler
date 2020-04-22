package com.jade.physics.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ParticleBuoyancy implements ParticleForceGenerator {
    private float maxDepth;
    private float volume;
    private float waterHeight;
    private float liquidDensity;

    public ParticleBuoyancy(float maxDepth, float volume, float waterHeight, float liquidDensity) {
        init(maxDepth, volume, waterHeight, liquidDensity);
    }

    public ParticleBuoyancy(float maxDepth, float volume, float waterHeight) {
        init(maxDepth, volume, waterHeight, 1000.0f);
    }

    private void init(float maxDepth, float volume, float waterHeight, float liquidDensity) {
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.waterHeight = waterHeight;
        this.liquidDensity = liquidDensity;
    }

    @Override
    public void updateForce(Particle particle, float duration) {
        // Calculate the submersion depth
        float depth = particle.getPosition().y;

        // Check if we're out of the water
        // axes are flip negative is up in y and positive is down
        if (depth <= waterHeight + maxDepth) return;
        Vector3f force = new Vector3f(0, 0, 0);

        // Check if we're at the max depth
        if (depth >= waterHeight + maxDepth) {
            force.y = -liquidDensity * volume;
            particle.addForce(force);
            return;
        }

        // Otherwise we are partly submerged
        force.y = -liquidDensity * volume * (depth - maxDepth - waterHeight) / 2 * maxDepth;
        particle.addForce(force);
    }
}
