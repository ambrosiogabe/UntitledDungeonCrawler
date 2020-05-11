package com.jade.physics.particles.collisions;


import com.jade.physics.particles.Particle;
import org.joml.Vector3f;

public abstract class ParticleLink { //extends ParticleContactGenerator {
    public Particle[] particle = new Particle[2];

    protected float currentLength() {
        Vector3f relativePos = particle[0].gameObject.transform.position.sub(particle[1].gameObject.transform.position);
        return relativePos.length();
    }

    public abstract int addContact(ParticleContact contact, int limit);
}
