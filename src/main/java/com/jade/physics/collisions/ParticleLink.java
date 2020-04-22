package com.jade.physics.collisions;


import com.jade.physics.particles.Particle;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class ParticleLink { //extends ParticleContactGenerator {
    public Particle[] particle = new Particle[2];

    protected float currentLength() {
        Vector3f relativePos = particle[0].uiObject.transform.position.sub(particle[1].uiObject.transform.position);
        return relativePos.length();
    }

    public abstract int addContact(ParticleContact contact, int limit);
}
