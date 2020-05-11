package com.jade.physics.particles.collisions;

import org.joml.Vector3f;

public class ParticleRod extends ParticleLink {
    public float length;

    @Override
    public int addContact(ParticleContact contact, int limit) {
        // Find the length of the rod
        float currentLength = currentLength();

        // Check if we're overextended
        if (currentLength == length) {
            return 0;
        }

        // Otherwise return the contact
        contact.particle[0] = particle[0];
        contact.particle[1] = particle[1];

        // Calculate the normal
        Vector3f normal = particle[1].gameObject.transform.position.sub(particle[0].gameObject.transform.position);
        normal.normalize();

        // The contact normal depends on whether we're extending or compressing
        if (currentLength > length) {
            contact.contactNormal = normal;
            contact.penetration = currentLength - length;
        } else {
            contact.contactNormal = normal.mul(-1);
            contact.penetration = length - currentLength;
        }

        // Always use zero restitution (no bounciness)
        contact.restitution = 0;

        return 1;
    }
}
