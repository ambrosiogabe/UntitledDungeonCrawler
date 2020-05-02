package com.jade.physics.rigidbody;

import com.jade.Component;
import com.jade.Window;
import com.jade.physics.colliders.Collider;
import com.jade.util.Constants;
import org.joml.*;

import java.lang.Math;

public class Rigidbody extends Component {
    private float inverseMass, mass;
    private float linearDamping, angularDamping;

    private Vector3f velocity;
    private Vector3f angularVelocity;
    private Matrix4f transformMatrix, inverseTransform;
    private Matrix3f inverseInertiaTensor;

    private boolean isAwake;
    private Vector3f forceAccum;
    private Vector3f torqueAccum;

    private Vector3f lastFrameAcceleration, acceleration;

    public Rigidbody(float mass, float linearDamping, float angularDamping) {
        init(mass, linearDamping, angularDamping);
    }

    private void init(float mass, float linearDamping, float angularDamping) {
        this.mass = mass;
        this.inverseMass = this.mass <= 0.0f ? 0 : 1.0f / this.mass;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;
        this.angularVelocity = new Vector3f();

        this.transformMatrix = new Matrix4f().identity();
        this.inverseTransform = new Matrix4f().identity();
        this.forceAccum = new Vector3f();
        this.torqueAccum = new Vector3f();
        this.lastFrameAcceleration = new Vector3f();
        this.acceleration = new Vector3f();
        this.velocity = new Vector3f();
    }

    @Override
    public void start() {
        this.inverseInertiaTensor = new Matrix3f().identity();
        this.gameObject.getComponent(Collider.class).getInertiaTensor(this.mass).invert(this.inverseInertiaTensor);
    }

    public boolean hasInfiniteMass() {
        return inverseMass == 0.0f;
    }

    public float getMass() {
        return this.mass;
    }

    @Override
    public void update(float dt) {
        if (!Window.getScene().doPhysics()) {
            return;
        }

        // Adjust velocities
        // Update linear velocity from both acceleration and impulse
        velocity.add(acceleration.add(new Vector3f(forceAccum).mul(dt).mul(inverseMass)));

        // Update angular velocity from both acceleration and impulse
        Vector3f angularAcceleration = new Vector3f(inverseInertiaTensor.transform(torqueAccum));
        angularVelocity.fma(dt * dt * dt, angularAcceleration);

        // Impose drag
        velocity.mul((float)Math.pow(linearDamping, dt));
        this.angularVelocity.mul((float)Math.pow(angularDamping, dt));

        // Adjust positions
        // Update linear positions
        this.gameObject.transform.position.add(velocity.mul(dt));

        // Update angular position
        Quaternionf q = new Quaternionf(angularVelocity.x, angularVelocity.y, angularVelocity.z, 0);
        q.scale(0.5f).mul(this.gameObject.transform.orientation);
        this.gameObject.transform.orientation.add(q);

        // Normalize the orientation, and update the matrices with the new
        // position and orientation
        calculateDerivedData();

        // Clear accumulators
        clearAccumulators();
    }

    @Override
    public Component copy() {
        return null;
    }

    public void calculateDerivedData() {
        this.gameObject.transform.orientation.normalize();
        this.transformMatrix.identity();
        this.transformMatrix.translate(this.gameObject.transform.position);
        this.transformMatrix.rotate(this.gameObject.transform.orientation);
        this.transformMatrix.invert(this.inverseTransform);
        // TODO: INCLUDE SCALE IN CALCULATIONS
        //this.transformMatrix.scale(this.gameObject.transform.scale);
    }

    public void setInertiaTensor(Matrix3f inertiaTensor) {
        inertiaTensor.invert(inverseInertiaTensor);
    }

    public void addForce(Vector3f force) {
        forceAccum.add(force);
        isAwake = true;
    }

    public void clearAccumulators() {
        forceAccum.zero();
        torqueAccum.zero();
    }

    public void integrate(float duration) {
        // Clear accumulators
        clearAccumulators();
    }

    // Add a force at world coordinates
    // Useful for external forces acting on the body
    public void addForceAtPoint(Vector3f force, Vector3f point) {
        // Convert to coordinates relative to center of mass
        Vector3f localPoint = point.sub(gameObject.transform.position);
        addGlobalForceAtBodyPoint(force, localPoint);
    }

    // Add a force in the model's local coordinates
    // Useful for springs and such attached to the body
    public void addForceAtBodyPoint(Vector3f force, Vector3f point) {
//        forceAccum.add(force);

        if (Constants.DEBUG_BUILD) {
//            Vector3f forceWorld = applyRotation(force);
//
//            Vector3f pointWorld = getPointInWorldSpace(point);
//            DebugDraw.addLine(new Vector3f(pointWorld), new Vector3f(pointWorld).add(forceWorld), 0.1f, Constants.COLOR3_GREEN);
//            Vector3f tmpForce = new Vector3f(forceWorld).cross(pointWorld);
//            DebugDraw.addLine(new Vector3f(pointWorld), new Vector3f(pointWorld).add(tmpForce), 0.1f, Constants.COLOR3_CYAN);
        }

        torqueAccum.add(force.cross(point).mul(-1));

        isAwake = true;
    }

    public void addGlobalForceAtBodyPoint(Vector3f globalForce, Vector3f localPoint) {
        forceAccum.add(globalForce);

        if (Constants.DEBUG_BUILD) {
//            Vector3f pointWorld = getPointInWorldSpace(localPoint);
//            DebugDraw.addLine(new Vector3f(pointWorld), new Vector3f(pointWorld).add(globalForce), 0.1f, Constants.COLOR3_GREEN);
//            Vector3f tmpForce = new Vector3f(globalForce).cross(pointWorld);
//            DebugDraw.addLine(new Vector3f(pointWorld), new Vector3f(pointWorld).add(tmpForce), 0.1f, Constants.COLOR3_CYAN);
        }

        globalForce = applyInverseRotation(globalForce);
        torqueAccum.add(globalForce.cross(localPoint).mul(-1));

        isAwake = true;
    }

    public Vector3f getPointInWorldSpace(Vector3f point) {
        Vector4f tmp = transformMatrix.transform(new Vector4f(point, 1.0f));
        return new Vector3f(tmp.x, tmp.y, tmp.z);
    }

    public Vector3f applyRotation(Vector3f point) {
        return this.gameObject.transform.orientation.transform(new Vector3f(point));
    }

    public Vector3f applyInverseRotation(Vector3f point) {
        return this.gameObject.transform.orientation.transformInverse(new Vector3f(point));
    }

    public void zeroForces() {
        this.torqueAccum.zero();
        this.forceAccum.zero();
        this.velocity.zero();
        this.angularVelocity.zero();
        this.acceleration.zero();
        this.lastFrameAcceleration.zero();
    }

    public Vector3f getVelocity() {
        return this.velocity;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
