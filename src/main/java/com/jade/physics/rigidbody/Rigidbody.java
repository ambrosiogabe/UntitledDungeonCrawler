package com.jade.physics.rigidbody;

import org.joml.*;

import java.lang.Math;

public class Rigidbody {
    float inverseMass, mass;
    float linearDamping, angularDamping;
    Vector3f position;
    Quaternionf orientation;
    Vector3f velocity;
    Vector3f rotation;
    Matrix4f transformMatrix;
    Matrix3f inverseInertiaTensor;
    Matrix3f inverseInertiaTensorWorld;

    boolean isAwake;
    Vector3f forceAccum;
    Vector3f torqueAccum;

    Vector3f lastFrameAcceleration, acceleration;

    public Rigidbody(float mass, Vector3f position, Vector3f rotation, float linearDamping, float angularDamping) {
        init(mass, position, rotation, linearDamping, angularDamping);
    }

    public Rigidbody(float mass, Vector3f position, float linearDamping, float angularDamping) {
        init(mass, position, new Vector3f(0.0f, 0.0f, 0.0f), linearDamping, angularDamping);
    }

    public Rigidbody(float mass, Vector3f position) {
        init(mass, position, new Vector3f(0.0f, 0.0f, 0.0f), 0.9f, 0.9f);
    }

    private void init(float mass, Vector3f position, Vector3f rotation, float linearDamping, float angularDamping) {
        this.mass = mass;
        this.inverseMass = this.mass <= 0.0f ? 0 : 1.0f / this.mass;
        this.position = position;
        this.rotation = rotation;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;
        transformMatrix = new Matrix4f();
        inverseInertiaTensor = new Matrix3f();
        inverseInertiaTensorWorld = new Matrix3f();
        forceAccum = new Vector3f();
        torqueAccum = new Vector3f();
        orientation = new Quaternionf();
        lastFrameAcceleration = new Vector3f();
        acceleration = new Vector3f();
        velocity = new Vector3f();
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public boolean hasInfiniteMass() {
        return inverseMass == 0.0f;
    }

    public float getMass() {
        return this.mass;
    }

    public void update(float dt) {
        // Calculate the linear acceleration from force inputs
        lastFrameAcceleration = acceleration;
        lastFrameAcceleration.add(forceAccum.mul(inverseMass));

        // Calculate the acceleration from torque inputs
        Vector3f angularAcceleration = inverseInertiaTensorWorld.transform(torqueAccum);

        // Adjust velocities
        // Update linear velocity from both acceleration and impulse
        velocity.add(lastFrameAcceleration.mul(dt));

        // Update angular velocity from both acceleration and impulse
        rotation.add(angularAcceleration.mul(dt));

        // Impose drag
        velocity.mul((float)Math.pow(linearDamping, dt));
        rotation.mul((float)Math.pow(angularDamping, dt));

        // Adjust positions
        // Update linear positions
        position.add(velocity.mul(dt));

        // Update angular position
        Vector3f tmp = rotation.mul(dt);
        orientation.add(tmp.x, tmp.y, tmp.z, 0.0f);

        // Normalize the orientation, and update the matrices with the new
        // position and orientation
        calculateDerivedData();

        // Clear accumulators
        clearAccumulators();
    }

    public void calculateDerivedData() {
        orientation.normalize();

//        // Calculate transform matrix for the body
//        transformMatrix.data[0] = 1-2*orientation.j*orientation.j-
//                2*orientation.k*orientation.k;
//        transformMatrix.data[1] = 2*orientation.i*orientation.j -
//                2*orientation.r*orientation.k;
//        transformMatrix.data[2] = 2*orientation.i*orientation.k +
//                2*orientation.r*orientation.j;
//        transformMatrix.data[3] = position.x;
//
//        transformMatrix.data[4] = 2*orientation.i*orientation.j +
//                2*orientation.r*orientation.k;
//        transformMatrix.data[5] = 1-2*orientation.i*orientation.i-
//                2*orientation.k*orientation.k;
//        transformMatrix.data[6] = 2*orientation.j*orientation.k -
//                2*orientation.r*orientation.i;
//        transformMatrix.data[7] = position.y;
//
//        transformMatrix.data[8] = 2*orientation.i*orientation.k -
//                2*orientation.r*orientation.j;
//        transformMatrix.data[9] = 2*orientation.j*orientation.k +
//                2*orientation.r*orientation.i;
//        transformMatrix.data[10] = 1-2*orientation.i*orientation.i-
//                2*orientation.j*orientation.j;
//        transformMatrix.data[11] = position.z;
//
//        // Calculate the inertia tensor in world space
//        float t4 = transformMatrix.data[0]*inverseInertiaTensor.data[0]+
//                transformMatrix.data[1]*inverseInertiaTensor.data[3]+
//                transformMatrix.data[2]*inverseInertiaTensor.data[6];
//        float t9 = transformMatrix.data[0]*inverseInertiaTensor.data[1]+
//                transformMatrix.data[1]*inverseInertiaTensor.data[4]+
//                transformMatrix.data[2]*inverseInertiaTensor.data[7];
//        float t14 = transformMatrix.data[0]*inverseInertiaTensor.data[2]+
//                transformMatrix.data[1]*inverseInertiaTensor.data[5]+
//                transformMatrix.data[2]*inverseInertiaTensor.data[8];
//        float t28 = transformMatrix.data[4]*inverseInertiaTensor.data[0]+
//                transformMatrix.data[5]*inverseInertiaTensor.data[3]+
//                transformMatrix.data[6]*inverseInertiaTensor.data[6];
//        float t33 = transformMatrix.data[4]*inverseInertiaTensor.data[1]+
//                transformMatrix.data[5]*inverseInertiaTensor.data[4]+
//                transformMatrix.data[6]*inverseInertiaTensor.data[7];
//        float t38 = transformMatrix.data[4]*inverseInertiaTensor.data[2]+
//                transformMatrix.data[5]*inverseInertiaTensor.data[5]+
//                transformMatrix.data[6]*inverseInertiaTensor.data[8];
//        float t52 = transformMatrix.data[8]*inverseInertiaTensor.data[0]+
//                transformMatrix.data[9]*inverseInertiaTensor.data[3]+
//                transformMatrix.data[10]*inverseInertiaTensor.data[6];
//        float t57 = transformMatrix.data[8]*inverseInertiaTensor.data[1]+
//                transformMatrix.data[9]*inverseInertiaTensor.data[4]+
//                transformMatrix.data[10]*inverseInertiaTensor.data[7];
//        float t62 = transformMatrix.data[8]*inverseInertiaTensor.data[2]+
//                transformMatrix.data[9]*inverseInertiaTensor.data[5]+
//                transformMatrix.data[10]*inverseInertiaTensor.data[8];
//
//        inverseInertiaTensorWorld.data[0] = t4*transformMatrix.data[0]+
//                t9*transformMatrix.data[1]+
//                t14*transformMatrix.data[2];
//        inverseInertiaTensorWorld.data[1] = t4*transformMatrix.data[4]+
//                t9*transformMatrix.data[5]+
//                t14*transformMatrix.data[6];
//        inverseInertiaTensorWorld.data[2] = t4*transformMatrix.data[8]+
//                t9*transformMatrix.data[9]+
//                t14*transformMatrix.data[10];
//        inverseInertiaTensorWorld.data[3] = t28*transformMatrix.data[0]+
//                t33*transformMatrix.data[1]+
//                t38*transformMatrix.data[2];
//        inverseInertiaTensorWorld.data[4] = t28*transformMatrix.data[4]+
//                t33*transformMatrix.data[5]+
//                t38*transformMatrix.data[6];
//        inverseInertiaTensorWorld.data[5] = t28*transformMatrix.data[8]+
//                t33*transformMatrix.data[9]+
//                t38*transformMatrix.data[10];
//        inverseInertiaTensorWorld.data[6] = t52*transformMatrix.data[0]+
//                t57*transformMatrix.data[1]+
//                t62*transformMatrix.data[2];
//        inverseInertiaTensorWorld.data[7] = t52*transformMatrix.data[4]+
//                t57*transformMatrix.data[5]+
//                t62*transformMatrix.data[6];
//        inverseInertiaTensorWorld.data[8] = t52*transformMatrix.data[8]+
//                t57*transformMatrix.data[9]+
//                t62*transformMatrix.data[10];
    }

    public void setInertiaTensor(Matrix3f inertiaTensor) {
        inverseInertiaTensor.invert();
        //inverseInertiaTensor.setInverse(inertiaTensor);
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
        Vector3f pt = point;
        pt.sub(position);

        forceAccum.sub(force);
        torqueAccum.sub(pt.cross(force));

        isAwake = true;
    }

    // Add a force in the model's local coordinates
    // Useful for springs and such attached to the body
    public void addForceAtBodyPoint(Vector3f force, Vector3f point) {
        // Convert to coordinates relative to center of mass
        Vector3f pt = getPointInWorldSpace(point);
        addForceAtPoint(force, pt);

        isAwake = true;
    }

    public Vector3f getPointInWorldSpace(Vector3f point) {
        Vector4f tmp = transformMatrix.transform(new Vector4f(point, 0.0f));
        return new Vector3f(tmp.x, tmp.y, tmp.z);
    }
}
