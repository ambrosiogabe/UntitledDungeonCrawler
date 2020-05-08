package com.jade.physics.rigidbody.collisions;

import com.jade.physics.rigidbody.Rigidbody;
import com.jade.physics.rigidbody.colliders.*;
import org.joml.Vector3f;

public class ContactResolver {

    private int velocityIterations = 1;
    private int velocityEpsilon = Integer.MIN_VALUE;
    private int positionIterations = 1;
    private int positionEpsilon = Integer.MIN_VALUE;

    public void resolveContacts(Contact[] contacts, int numContacts, float duration) {
        // Make sure we have something to do
        if (numContacts == 0) return;

        // Prepare the contacts for processing
        prepareContacts(contacts, numContacts, duration);

        // Resolve the interpenetration problems with the contacts.
        adjustPositions(contacts, numContacts, duration);

        // Resolve the velocity problems with the contacts
        adjustVelocities(contacts, numContacts, duration);
    }

    private void prepareContacts(Contact[] contacts, int numContacts, float duration) {
        // Generate contact velocity and axis information
        for (int i=0; i < numContacts; i++) {
            Contact contact = contacts[i];
            // Calculate the internal contact data (inertia, basis, etc)
            contact.calculateInternals(duration);
        }
    }

    private void adjustPositions(Contact[] contacts, int numContacts, float duration) {

        int i, index;
        Vector3f[] linearChange = {new Vector3f(), new Vector3f()};
        Vector3f[] angularChange = {new Vector3f(), new Vector3f()};
        float max;
        Vector3f deltaPosition;

        for (i=0; i < numContacts; i++) {
            contacts[i].applyPositionChange(linearChange, angularChange, contacts[i].getPenetrationDepth());
        }

        // Iteratively resolve interpenetrations in order of severity
//        int positionIterationsUsed = 0;
//        while (positionIterationsUsed < positionIterations) {
//            // Find the biggest penetration
//            max = positionEpsilon;
//            index = numContacts;
//            for (i = 0; i < numContacts; i++) {
//                if (contacts[i].getPenetrationDepth() > max) {
//                    max = contacts[i].getPenetrationDepth();
//                    index = i;
//                }
//            }
//            if (index == numContacts) break;
//
//            // Match the awake state at the contact
//            //contacts[index].matchAwakeState();
//
//            // Resolve the penetration
//            contacts[index].applyPositionChange(linearChange, angularChange, max);
//
//            // Again this action may have changed the penetration of other bodies
//            // so we update the contacts.
//            for (i = 0; i < numContacts; i++) {
//                // Check each body in the contact.
//                for (int b = 0; b < 2; b++) {
//                    Rigidbody body = b == 0 ? contacts[i].bodyOne : contacts[i].bodyTwo;
//                    if (body != null) {
//                        // Check for a match with each body in the newly resolved contact
//                        for (int d = 0; d < 2; d++) {
//                            Rigidbody body2 = d == 0 ? contacts[index].bodyOne : contacts[index].bodyTwo;
//                            if (body == body2) {
//                                deltaPosition = new Vector3f(linearChange[d]).add(angularChange[d]).mul(contacts[i].relativeContactPosition()[b]);
//
//                                // The sign of the change is positive if we're dealing with
//                                // the second body in a contact, and negative otherwise (because
//                                // we're subtracting the resolution).
//                                contacts[i].setPenetrationDepth(contacts[i].getPenetrationDepth() + deltaPosition.dot(contacts[i].getContactNormal()) * (b != 0 ? 1 : -1));
//                            }
//                        }
//                    }
//                }
//            }
//            positionIterationsUsed++;
//        }
    }

    private void adjustVelocities(Contact[] contacts, int numContacts, float duration) {
        int i, index;
        Vector3f[] velocityChange = {new Vector3f(), new Vector3f()};
        Vector3f[] angularVelocityChange = {new Vector3f(), new Vector3f()};
        float max;
        Vector3f deltaVel;

        for (i=0; i < numContacts; i++) {
            contacts[i].applyVelocityChange(velocityChange, angularVelocityChange);
        }

        // Iteratively resolve interpenetrations in order of severity
//        int velocityIterationsUsed = 0;
//        while (velocityIterationsUsed < velocityIterations) {
//            // Find the contact with maximum magnitude of probable velocity change
//            max = velocityEpsilon;
//            index = numContacts;
//            for (i = 0; i < numContacts; i++) {
//                if (contacts[i].desiredDeltaVelocity() > max) {
//                    max = contacts[i].desiredDeltaVelocity();
//                    index = i;
//                }
//            }
//            if (index == numContacts) break;
//
//            // Match the awake state at the contact
//            //contacts[index].matchAwakeState();
//
//            // Do the resolution on the contact that came out top
//            contacts[index].applyVelocityChange(velocityChange, angularVelocityChange);
//
//            // With the change in velocity of the two bodies, the update of
//            // contact velocities means that some of the relative closing
//            // velocities need recomputing.
//            for (i = 0; i < numContacts; i++) {
//                // Check each body in the contact.
//                for (int b = 0; b < 2; b++) {
//                    Rigidbody body = b == 0 ? contacts[i].bodyOne : contacts[i].bodyTwo;
//                    if (body != null) {
//                        // Check for a match with each body in the newly resolved contact
//                        for (int d = 0; d < 2; d++) {
//                            Rigidbody body2 = d == 0 ? contacts[index].bodyOne : contacts[index].bodyTwo;
//                            if (body == body2) {
//                                deltaVel = new Vector3f(velocityChange[d]).add(angularVelocityChange[d]).mul(contacts[i].relativeContactPosition()[b]);
//
//                                // The sign of the change is negative if we're dealing
//                                // with the second body in a contact.
//                                contacts[i].getContactVelocity().add(contacts[i].contactToWorld().transformTranspose(deltaVel).mul(b != 0 ? -1 : 1));
//                                contacts[i].caluclateDesiredDeltaVelocity(duration);
//                            }
//                        }
//                    }
//                }
//            }
//            velocityIterationsUsed++;
//        }
    }
}
