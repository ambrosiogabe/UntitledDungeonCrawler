package com.jade.physics.forces;

import com.jade.physics.rigidbody.Rigidbody;
import org.joml.Vector3f;

public class Spring implements ForceGenerator {

    // The point of connection of the spring in local coordinates
    Vector3f connectionPoint;

    // The point of connection of the spring to the other object
    // in that object's local coordinates
    Vector3f otherPointWorldSpace;

    // The particle at the other end of the spring
    //Rigidbody other;

    float springConstant, restLength;

    public Spring(Vector3f localConnectionPoint, Vector3f otherPointWorldSpace, float springConstant, float restLength) {
        this.connectionPoint = localConnectionPoint;
        this.otherPointWorldSpace = otherPointWorldSpace;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    @Override
    public void updateForce(Rigidbody body, float duration) {
        // Calculate the two ends in world space
        Vector3f lws = body.getPointInWorldSpace(connectionPoint);
        Vector3f ows = new Vector3f(otherPointWorldSpace);

        // Calculate the vector of the spring (in local space)
        Vector3f force = new Vector3f(lws).sub(ows);

        // Calculate the magnitude of the force
        float magnitude = force.length();
        magnitude = Math.abs(magnitude - restLength);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalize();
        force.mul(-magnitude);
        body.addForceAtPoint(force, lws);
    }
}
