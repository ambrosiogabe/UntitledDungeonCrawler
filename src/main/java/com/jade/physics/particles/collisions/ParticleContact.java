package com.jade.physics.particles.collisions;

import com.jade.physics.particles.Particle;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ParticleContact {
    public Particle[] particle = new Particle[2];

    // Restitution coefficient
    public float restitution;
    // The contact normal of the colliding objects
    public Vector3f contactNormal;
    // The depth of the penetration of the two colliding objects
    public float penetration;

    private Vector3f[] particleMovement = new Vector3f[2];

    protected void resolve(float dt) {
        resolveVelocity(dt);
        resolveInterpenetration(dt);
    }

    protected float calculateSeparatingVelocity() {
        Vector3f relativeVelocity = particle[0].getVelocity();
        if (particle[1] != null) relativeVelocity.add(particle[1].getVelocity().mul(-1));
        return relativeVelocity.dot(contactNormal);
    }

    private void resolveInterpenetration(float dt) {
        // If we don't have any penetration, skip this step
        if (penetration <= 0) return;

        // The movement of each object is proportional to their mass
        float totalInverseMass = particle[0].getInverseMass();
        if (particle[1] != null) totalInverseMass += particle[1].getInverseMass();

        // If all particles have infinite mass, then we do nothing
        if (totalInverseMass <= 0) return;

        // Find the amount of penetration resolution per unit
        Vector3f movePerIMass = contactNormal.mul(penetration / totalInverseMass);

        // Calculate the movement amounts
        particleMovement[0] = movePerIMass.mul(particle[0].getInverseMass());
        if (particle[1] != null) {
            particleMovement[1] = movePerIMass.mul(-particle[1].getInverseMass());
        } else {
            particleMovement[1].zero();
        }

        // Apply the penetration resolution
        particle[0].uiObject.transform.position.add(particleMovement[0]);

        if (particle[1] != null) {
                particle[1].uiObject.transform.position.add(particleMovement[1]);
        }
    }

    private void resolveVelocity(float dt) {
        // Find the velocity in the direction of the contact
        float separatingVelocity = calculateSeparatingVelocity();

        // Check if it needs to be resolved
        if (separatingVelocity > 0) {
            // The contact is either separating, or stationary;
            // No impulse is required
            return;
        }

        // Calculate the new separating velocity
        float newSepVelocity = -separatingVelocity * restitution;

        // Check the velocity buildup due to acceleration only
        Vector3f accCausedVelocity = particle[0].getAcceleration();
        if (particle[1] != null) accCausedVelocity.add(particle[1].getAcceleration().mul(-1));
        float accCausedSepVelocity = accCausedVelocity.dot(contactNormal) * dt;

        // If we got a closing velocity due to acceleration buildup
        // remove it from the new separating velocity
        if (accCausedSepVelocity < 0) {
            newSepVelocity += restitution * accCausedSepVelocity;

            // Make sure we haven't removed more than there was to remove
            if (newSepVelocity < 0) newSepVelocity = 0;
        }

        float deltaVelocity = newSepVelocity - separatingVelocity;

        // We apply the change in velocity in proportion to their inverse mass
        float totalInverseMass = particle[0].getInverseMass();
        if (particle[1] != null) totalInverseMass += particle[1].getInverseMass();

        // If all particles have infinite mass, then impulses have no effect
        if (totalInverseMass <= 0) return;

        // Calculate the impule to apply
        float impulse = deltaVelocity / totalInverseMass;

        // Find the amount of impulse per unit mass
        Vector3f impulsePerMass = contactNormal.mul(impulse);

        // Apply impulses: they are applied in the direction of the contact,
        // and are proportional to the inverse mass
        particle[0].setVelocity(
                particle[0].getVelocity().add(
                            impulsePerMass.mul(-particle[0].getInverseMass())
                )
        );

        if (particle[1] != null) {
            particle[1].setVelocity(
                    particle[1].getVelocity().add(
                            impulsePerMass.mul(-particle[1].getInverseMass())
                    )
            );
        }
    }
}
