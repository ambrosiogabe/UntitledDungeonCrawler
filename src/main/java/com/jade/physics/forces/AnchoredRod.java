package com.jade.physics.forces;

import com.jade.physics.rigidbody.Rigidbody;
import org.joml.Vector3f;

public class AnchoredRod implements ForceGenerator {

    private Vector3f connectionPoint;
    private Vector3f anchorPoint;
    private float restLength;
    private float springConstant = 100f;

    public AnchoredRod(Vector3f connectionPoint, Vector3f anchorPoint) {
        this.connectionPoint = connectionPoint;
        this.anchorPoint = anchorPoint;
        this.restLength = -1f;
    }

    @Override
    public void updateForce(Rigidbody body, float duration) {
        // Calculate the two ends in world space
        Vector3f lws = body.getPointInWorldSpace(connectionPoint);
        Vector3f ows = new Vector3f(anchorPoint);

        if (this.restLength == -1f) {
            this.restLength = new Vector3f(ows).sub(lws).length();
        }

        // Calculate the vector of the spring (in local space)
        Vector3f force = new Vector3f(lws).sub(ows);

        // Calculate the magnitude of the force
        float magnitude = force.length();
        magnitude = Math.max(magnitude - restLength, 1.6f);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalize();
        force.mul(-magnitude);
        body.addForceAtPoint(force, lws);
    }
}
