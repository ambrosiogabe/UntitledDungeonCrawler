package com.jade.physics.particles.collisions;

import org.joml.Vector3f;

public class ParticleCable extends ParticleLink {
    // Max length of the cable
    private float maxLength;

    // Bounciness of the cable
    // 0 = no bounce
    // 1 = full bounce
    private float restitution;

    @Override
    public int addContact(ParticleContact contact, int limit) {
        // Find the length of the cable
        float length = currentLength();

        // Check if we're overextended
        if (length < maxLength) {
            return 0;
        }

        // Otherwise return the contact
        contact.particle[0] = particle[0];
        contact.particle[1] = particle[1];

        // Calculate the normal
        Vector3f normal = new Vector3f();
        particle[1].uiObject.transform.position.sub(particle[0].uiObject.transform.position, normal);
        normal.normalize();
        contact.contactNormal = normal;

        contact.penetration = (length - maxLength);
        contact.penetration = (restitution);

        return 1;
    }
}
