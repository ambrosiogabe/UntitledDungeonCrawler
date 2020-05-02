package com.jade.physics.particles.collisions;

public interface ParticleContactGenerator {
    int addContact(ParticleContact contact, int limit);
}
