package com.jade.physics.rigidbody;

import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import org.joml.Vector3f;

public class Spring implements ForceGenerator {

    // The point of connection of the spring in local coordinates
    Vector3f connectionPoint;

    // The point of connection of the spring to the other object
    // in that object's local coordinates
    Vector3f otherConnectionPoint;

    // The particle at the other end of the spring
    Rigidbody other;

    float springConstant, restLength;

    public Spring(Vector3f localConnectionPoint, Rigidbody other, Vector3f otherConnectionPoint, float springConstant, float restLength) {
        this.connectionPoint = localConnectionPoint;
        this.other = other;
        this.otherConnectionPoint = otherConnectionPoint;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    @Override
    public void updateForce(Rigidbody body, float duration) {
        // Calculate the two ends in world space
        Vector3f lws = body.getPointInWorldSpace(connectionPoint);
        Vector3f ows = other.getPointInWorldSpace(otherConnectionPoint);

        // Calculate the vector of the spring (in local space)
        Vector3f force = new Vector3f(lws).sub(ows);

        // Calculate the magnitude of the force
        float magnitude = force.length();
        magnitude = Math.abs(magnitude - restLength);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalize();
        force.mul(-magnitude);
        body.addGlobalForceAtLocalBodyPoint(force, connectionPoint);
    }
}
