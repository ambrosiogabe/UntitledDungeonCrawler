package com.jade.physics.particles;

import com.jade.physics.collisions.ParticleContact;

public interface ParticleContactGenerator {
    int addContact(ParticleContact contact, int limit);
}
