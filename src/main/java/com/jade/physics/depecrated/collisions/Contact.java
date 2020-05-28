package com.jade.physics.depecrated.collisions;

import com.jade.physics.depecrated.Rigidbody;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class Contact {
    Rigidbody bodyOne, bodyTwo;

    private Matrix3f contactToWorld;
    private Vector3f contactVelocity;
    private float desiredDeltaVelocity;
    private Vector3f[] relativeContactPosition = new Vector3f[2];

    private Vector3f contactPoint;
    private Vector3f contactNormal;
    private float penetrationDepth;
    private float friction;
    private float restitution = 0.2f;

    public Contact() {
        this.bodyTwo = null;
        this.bodyOne = null;
        this.contactPoint = null;
        this.contactNormal = null;
        this.penetrationDepth = 0;
        this.friction = 0;
    }

    public Contact(Vector3f contactPoint, Vector3f contactNormal, float penetrationDepth) {
        this.contactPoint = contactPoint;
        this.contactNormal = contactNormal;
        this.penetrationDepth = penetrationDepth;
    }

    /*
     * Creates an orthonormal transformation basis, based upon the contact normal.
     * Chooses arbitrary y and z axes.
     */
    private void calculateContactBasis() {
        Vector3f[] contactTangent = new Vector3f[2];

        // Check whether the Z-axis is nearer to the X- or Y-axis
        if (Math.abs(contactNormal.x) > Math.abs(contactNormal.y)) {
            // Scaling factor to ensure the results are normalized
            float s = 1.0f / (float)Math.sqrt(contactNormal.z * contactNormal.z + contactNormal.x * contactNormal.x);

            // The new X-axis is at right angles to the world Y-axis
            contactTangent[0] = new Vector3f(contactNormal.z * s, 0, -contactNormal.x * s);

            // The new Y-axis is at right angles to the new X- and Z-axes
            contactTangent[1] = new Vector3f();
            contactTangent[1].x = contactNormal.y * contactTangent[0].x;
            contactTangent[1].y = contactNormal.z * contactTangent[0].x - contactNormal.x * contactTangent[0].z;
            contactTangent[1].z = -contactNormal.y * contactTangent[0].x;
        } else {
            // Scaling factor to ensure the results are normalized
            float s = 1.0f / (float)Math.sqrt(contactNormal.z * contactNormal.z + contactNormal.y * contactNormal.y);

            // The new X-axis is at right angles to the world Y-axis
            contactTangent[0] = new Vector3f(0, -contactNormal.z * s, -contactNormal.y * s);

            // The new Y-axis is at right angles to the new X- and Z-axes
            contactTangent[1] = new Vector3f();
            contactTangent[1].x = contactNormal.y * contactTangent[0].z - contactNormal.z * contactTangent[0].y;
            contactTangent[1].y = -contactNormal.x * contactTangent[0].z;
            contactTangent[1].z = contactNormal.x * contactTangent[0].y;
        }

        // Make a matrix from the three vectors
        contactToWorld = new Matrix3f(contactNormal, contactTangent[0], contactTangent[1]);
    }

    public void applyPositionChange(Vector3f[] linearChange, Vector3f[] angularChange, float penetration) {
        float angularLimit = 0.2f;
        float[] angularMove = new float[2];
        float[] linearMove = new float[2];

        float[] angularInertia = new float[2];
        float[] linearInertia = new float[2];
        float totalInertia = 0;

        // We need to work out the inertia of each object in the direction of the
        // contact normal due to angular inertia only.
        for (int i=0; i < 2; i++) {
            Rigidbody body = i == 0 ? bodyOne : bodyTwo;
            if (body != null) {
                Matrix3f inverseInertiaTensor = body.getInverseInertiaTensorWorld();

                // Use the same procedure as for calculating frictionless velocity
                // change to work out the angular inertia
                Vector3f angularInertiaWorld = new Vector3f(relativeContactPosition[i]).cross(contactNormal);
                inverseInertiaTensor.transform(angularInertiaWorld);
                angularInertiaWorld.cross(relativeContactPosition[i]);
                angularInertia[i] = angularInertiaWorld.dot(contactNormal);

                // The linear component is simply the inverse mass
                linearInertia[i] = body.getInverseMass();

                // Keep track of the total inertia from all components
                totalInertia += linearInertia[i] + angularInertia[i];

                // We break the loop here so that the totalInertia value is
                // completely calculated (by both iterations) before
                // continuing.
            }
        }

        // Loop through again calculating and applying the changes
        for (int i=0; i < 2; i++) {
            Rigidbody body = i == 0 ? bodyOne : bodyTwo;

            // The linear and angular movements required are in proportion to the two inverse inertias
            float sign = (i == 0) ? 1 : -1;
            angularMove[i] = 0;//sign * penetration * (angularInertia[i] / totalInertia);
            linearMove[i] = sign * penetration;// * (linearInertia[i] / totalInertia);

            // To avoid angular projections that are too great (when mass is large but inertia
            // tensor is small) limit the angular move
            Vector3f projection = relativeContactPosition[i];
            projection.fma(-relativeContactPosition[i].dot(contactNormal), new Vector3f(contactNormal));

            // Use the small angle approximation for the sine of the angle (i.e. the magnitude would be
            // sine(angularLimit) * projection.mangitude but we approximate sine(angularLimit) to angularLimit).
            float maxMagnitude = angularLimit * projection.length();

            if (angularMove[i] < -maxMagnitude) {
                float totalMove = angularMove[i] + linearMove[i];
                angularMove[i] = -maxMagnitude;
                linearMove[i] = totalMove - angularMove[i];
            } else if (angularMove[i] > maxMagnitude) {
                float totalMove = angularMove[i] + linearMove[i];
                angularMove[i] = maxMagnitude;
                linearMove[i] = totalMove - angularMove[i];
            }

            // We have the linear amount of movement required by turning the
            // rigidbody (in angularMove[i]). We now need to calculate the
            // desired rotation to achieve that
            if (angularMove[i] == 0) {
                // Easy case - no angular movement means no rotation
                angularChange[i].zero();
            } else {
                // Work out the direction we'd like to rotate in
                Vector3f targetAngularDirection = new Vector3f(relativeContactPosition[i]).mul(contactNormal);

                Matrix3f inverseInertiaTensor = body.getInverseInertiaTensor();

                // Work out the direction we'd need to rotate to achieve that
                angularChange[i] = inverseInertiaTensor.transform(targetAngularDirection).mul(angularMove[i] / angularInertia[i]);
            }

            // Velocity change is easier - it is just the linear movement along the contact normal
            linearChange[i] = new Vector3f(contactNormal).mul(linearMove[i]);

            if (body != null && !body.isStatic()) {
                // Now we can start to apply the values we've calculated.
                // Apply the linear movement
                Vector3f pos = body.gameObject.transform.position;
                pos.add(linearChange[i]);

                // And the change in orientation
                // TODO: PROBABLY WRONG (this calculation probably isn't right, should be scale add vector)
//                Quaternionf q = new Quaternionf(angularChange[i].x, angularChange[i].y, angularChange[i].z, 0);
//                q.scale(1.0f).mul(body.gameObject.transform.orientation);
//                body.gameObject.transform.orientation.add(q);

                // We need to calculate the derived data for any body that is asleep
                // so that the changes are reflected in the object's data. Otherwise
                // the resolution will not change the position of the object,
                // and the next collision detection round will have the same penetration
                //if (!body.getAwake()) body.calculateDerivedData();
            }
        }
    }

    public void calculateInternals(float duration) {
        // Check if the first object is null, and swap if it is
        if (bodyOne == null || bodyOne.isStatic()) swapBodies();
        assert bodyOne != null : "Error: Calculating internals, two null bodies in collision...";

        // Calculate a set of axes at the contact point
        calculateContactBasis();

        // Store the relative position of the contact relative to each body.
        relativeContactPosition[0] = new Vector3f(contactPoint).sub(bodyOne.gameObject.transform.position);
        if (bodyTwo != null) {
            relativeContactPosition[1] = new Vector3f(contactPoint).sub(bodyTwo.gameObject.transform.position);
        }

        // Find the relative velocity of the bodies at the contact point.
        contactVelocity = calculateLocalVelocity(0, duration);
        if (bodyTwo != null && !bodyTwo.isStatic()) {
            contactVelocity.sub(calculateLocalVelocity(1, duration));
        }

        // Calculate the desired change in velocity for resolution
        caluclateDesiredDeltaVelocity(duration);
    }

    private Vector3f calculateLocalVelocity(int bodyIndex, float duration) {
        Rigidbody thisBody = bodyIndex == 0 ? bodyOne : bodyTwo;

        // Work out the velocity of the contact point
        Vector3f velocity = new Vector3f(thisBody.getAngularVelocity()).cross(relativeContactPosition[bodyIndex]);
        velocity.add(thisBody.getVelocity());

        // Turn the velocity into contact coordinates
        Vector3f contactVelocity = new Vector3f();
        contactToWorld.transformTranspose(velocity, contactVelocity);

        // Calculate the amount of velocity that is due to forces without reactions
        //Vector3f accVelocity = new Vector3f(thisBody.getLastFrameAcceleration()).mul(duration);

        // We ignore any component of acceleration in the contact normal
        // direction, we are only interested in planar acceleration
        //accVelocity.x = 0;

        // Add the planar velocities - if there's enough friction they will
        // be removed during velocity resolution
        //contactVelocity.add(accVelocity);

        // And return it
        return contactVelocity;
    }

    public void caluclateDesiredDeltaVelocity(float duration) {

        float velocityFromAcc = 0;
        velocityFromAcc += new Vector3f(bodyOne.getLastFrameAcceleration()).mul(duration).dot(contactNormal);

        // If the velocity is very slow, limit the restitution
        float thisRestitution = restitution;
        float velocityLimit = 0.25f;
        if (Math.abs(contactVelocity.x) < velocityLimit) {
            thisRestitution = 0.0f;
        }

        // Combine the bounce velocity with the removed acceleration velocity
        desiredDeltaVelocity = -contactVelocity.x * (1 + thisRestitution);
    }

    private Vector3f calculateFrictionlessImpulse(Matrix3f[] inverseInertiaTensor) {
        // Build a vector that shows the change in velocity in world space
        // for a unit impulse in the direction of the contact normal.
        Vector3f deltaVelWorld = new Vector3f(relativeContactPosition[0]).cross(contactNormal);
        inverseInertiaTensor[0].transform(deltaVelWorld);
        deltaVelWorld.cross(relativeContactPosition[0]);

        // Work out the change in velocity in contact coordinates
        float deltaVelocity = deltaVelWorld.dot(contactNormal);

        // Add the linear component of velocity change
        deltaVelocity += bodyOne.getInverseMass();

        // Check as necessary the second body's data
        if (bodyTwo != null && !bodyTwo.isStatic()) {
            // Go through the same transformation sequence again
            deltaVelWorld = new Vector3f(relativeContactPosition[1]).cross(contactNormal);
            inverseInertiaTensor[1].transform(deltaVelWorld);
            deltaVelWorld.cross(relativeContactPosition[1]);

            // Add the change in body due to the rotation
            deltaVelocity += deltaVelWorld.dot(contactNormal);

            // Add the change in velocity due to linear motion.
            deltaVelocity += bodyTwo.getInverseMass();
        }

        // Calculate the required size of the impulse
        Vector3f impulseContact = new Vector3f(deltaVelocity / desiredDeltaVelocity, 0, 0);
        return impulseContact;
    }

    public void applyVelocityChange(Vector3f[] velocityChange, Vector3f[] angularVelocityChange) {
        // Get hold of the inverse mass and inverse inertia tensor, both in world coordinates
        Matrix3f[] inverseInertiaTensor = new Matrix3f[2];
        inverseInertiaTensor[0] = bodyOne.getInverseInertiaTensorWorld();
        if (bodyTwo != null && !bodyTwo.isStatic()) {
            inverseInertiaTensor[1] = bodyTwo.getInverseInertiaTensorWorld();
        }

        // We will calculate the impulse for each contact axis
        Vector3f impulseContact = calculateFrictionlessImpulse(inverseInertiaTensor);

        // Convert impulse to world coordinates
        Vector3f impulse = contactToWorld.transform(new Vector3f(impulseContact));

        // Split the impulse into linear and rotational components
        Vector3f impulsiveTorque = new Vector3f(new Vector3f(relativeContactPosition[0])).cross(new Vector3f(impulse));
        angularVelocityChange[0] = new Vector3f(impulsiveTorque);
        inverseInertiaTensor[0].transform(angularVelocityChange[0]);
        velocityChange[0].zero();
        velocityChange[0].fma(bodyOne.getInverseMass(), impulse);

        // Apply the changes
        bodyOne.addVelocity(velocityChange[0]);
//        bodyOne.addAngularVelocity(angularVelocityChange[0]);

        if (bodyTwo != null && !bodyTwo.isStatic()) {
            // Work out body two's linear and angular changes
            impulsiveTorque = new Vector3f(relativeContactPosition[1]).cross(new Vector3f(impulse));
            angularVelocityChange[1] = new Vector3f(impulsiveTorque);
            inverseInertiaTensor[1].transform(angularVelocityChange[1]);
            velocityChange[1].zero();
            velocityChange[1].fma(-bodyTwo.getInverseMass(), impulse);

            // Apply the changes
            bodyTwo.addVelocity(velocityChange[1]);
//            bodyTwo.addAngularVelocity(angularVelocityChange[1]);
        }
    }

    private void swapBodies() {
        contactNormal.mul(-1);

        Rigidbody tmp = bodyOne;
        bodyOne = bodyTwo;
        bodyTwo = tmp;
    }

    public Vector3f getContactPoint() {
        return this.contactPoint;
    }

    public void setContactPoint(Vector3f point) {
        this.contactPoint = point;
    }

    public Vector3f getContactNormal() {
        return this.contactNormal;
    }

    public void setContactNormal(Vector3f normal) {
        this.contactNormal = normal;
    }

    public float getPenetrationDepth() {
        return this.penetrationDepth;
    }

    public void setPenetrationDepth(float val) {
        this.penetrationDepth = val;
    }

    public Vector3f[] relativeContactPosition() {
        return this.relativeContactPosition;
    }

    public float desiredDeltaVelocity() {
        return this.desiredDeltaVelocity;
    }

    public Vector3f getContactVelocity() {
        return this.contactVelocity;
    }

    public Matrix3f contactToWorld() {
        return this.contactToWorld;
    }

    public void setBodyData(Rigidbody one, Rigidbody two, float friction, float restitution) {
        this.bodyOne = one;
        this.bodyTwo = two;
        this.friction = friction;
        this.restitution = restitution;
    }
}
