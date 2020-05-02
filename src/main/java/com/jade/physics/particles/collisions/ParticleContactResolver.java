package com.jade.physics.particles.collisions;

import java.util.List;

public class ParticleContactResolver {
    // The number of iterations allowed
    protected int iterations;

    protected int iterationsUsed;

    public ParticleContactResolver(int iterations) {
        this.iterations = iterations;
    }

    public void setIterations(int interations) {
        this.iterations = iterations;
    }

    public void resolveContacts(List<ParticleContact> contactArray, float dt) {
        iterationsUsed = 0;
        while (iterationsUsed < iterations) {
            // Find the contact with the largest closing velocity
            float max = Float.MAX_VALUE;
            int maxIndex = contactArray.size();
            for (int i = 0; i < contactArray.size(); i++) {
                float sepVel = contactArray.get(i).calculateSeparatingVelocity();
                if (sepVel < max &&
                    (sepVel < 0 || contactArray.get(i).penetration > 0)) {
                    max = sepVel;
                    maxIndex = i;
                }
            }

            // Do we have anything worth resolving
            if (maxIndex == contactArray.size()) break;

            // Resolve this contact
            contactArray.get(maxIndex).resolve(dt);
            iterationsUsed++;
        }
    }
}
