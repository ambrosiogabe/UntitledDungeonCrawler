package com.jade.physics;

import com.jade.physics.particles.collisions.ParticleContact;
import com.jade.physics.particles.collisions.ParticleContactResolver;
import com.jade.physics.particles.Particle;
import com.jade.physics.particles.collisions.ParticleContactGenerator;
import com.jade.physics.particles.ParticleForceRegistry;

import java.util.List;

public class ParticleWorld {
    protected List<Particle> particles;

    private ParticleForceRegistry registry;
    private ParticleContactResolver resolver;
    private List<ParticleContactGenerator> contactGenerators;
    private List<ParticleContact> contacts;
    private int maxContacts, iterations;

    public ParticleWorld(int maxContacts, int iterations) {
        this.maxContacts = maxContacts;
        this.iterations = iterations;
    }

    public ParticleWorld(int maxContacts) {
        this.maxContacts = maxContacts;
        this.iterations = maxContacts * 2;
    }

    public void startFrame() {

    }

    public void update(float dt) {
        for (Particle p : particles) {
            // Update the particle
            p.update(dt);
        }
    }

    public void runPhysics(float dt) {
        // First, apply the force generators
        registry.updateForces(dt);

        // Then integrate the objects
        update(dt);

        // Generate contacts
        int usedContacts = generateContacts();

        // And process them
        if (usedContacts > 0) {
            resolver.setIterations(usedContacts * 2);
            resolver.resolveContacts(contacts, dt);
        }
    }

    private int generateContacts() {
        int limit = maxContacts;
        int nextContactIndex = 0;
        ParticleContact nextContact = contacts.get(nextContactIndex);

        for (ParticleContactGenerator g : contactGenerators) {
            int used = g.addContact(nextContact, limit);
            limit -= used;
            nextContactIndex += used;
            nextContact = contacts.get(nextContactIndex);

            // We've run out of contacts to use, this means we're missing contacts
            if (limit <= 0) break;
        }

        // Return the number of contacts used
        return maxContacts - limit;
    }
}
