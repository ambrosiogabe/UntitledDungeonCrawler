package com.jade.physics2d.rigidbody;

import com.jade.Component;
import com.jade.Window;
import com.jade.util.JMath;
import org.joml.Vector2f;

public class Rigidbody2D extends Component {

    private float mass, inverseMass, inertiaTensor, inverseInertiaTensor;
    private float linearDamping, angularDamping;

    private Vector2f velocity;
    private Vector2f acceleration;
    private float angularVelocity;
    private float angularAcceleration;

    private Vector2f forceAccum;
    private float torqueAccum;

    private boolean isStatic;

    public Rigidbody2D(float mass, float linearDamping, float angularDamping) {
        init(mass, linearDamping, angularDamping, false);
    }

    public Rigidbody2D(boolean isStatic) {
        init(0, 0, 0, isStatic);
    }

    public void init(float mass, float linearDamping, float angularDamping, boolean isStatic) {
        this.mass = mass;
        this.inverseMass = this.mass != 0 ? 1f / this.mass : 0f;
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;

        this.velocity = new Vector2f();
        this.acceleration = new Vector2f();
        this.angularVelocity = 0;
        this.angularAcceleration = 0;

        this.forceAccum = new Vector2f();
        this.torqueAccum = 0;
        this.isStatic = isStatic;
    }

    @Override
    public void start() {
        this.inertiaTensor = 0f;
        this.inverseInertiaTensor = 0f;
    }

    @Override
    public void update(float dt) {
        this.applyForces(1f/60f);
        this.integrate(1f/60f);
    }

    public void applyForces(float dt) {
        if (!Window.getScene().doPhysics()) {
            return;
        }

        // Apply velocity
        this.acceleration = new Vector2f(forceAccum).mul(inverseMass).mul(50);
        velocity.add(new Vector2f(this.acceleration).mul(dt));

        // Apply angular velocity
        //this.torqueAccum += angularVelocity * angularDamping;
        this.angularAcceleration = torqueAccum * inverseInertiaTensor;
        this.angularVelocity += this.angularVelocity * dt;

        // Impose drag
        this.velocity.mul((float)Math.pow(linearDamping, dt));
        this.angularVelocity *= (float)Math.pow(angularDamping, dt);

        clearAccumulators();
    }

    public void integrate(float dt) {
        // Update linear position
        this.gameObject.transform.position.add(JMath.vector3fFrom2f(velocity).mul(dt));

        // Update orientation
        float deltaTheta = this.angularVelocity * dt;
        this.gameObject.transform.rotation.z += deltaTheta;
    }

    public void addLinearForce(Vector2f force) {
        this.forceAccum.add(force);
    }

    public void zeroForces() {

    }

    public void clearAccumulators() {
        this.forceAccum.zero();
        this.torqueAccum = 0;
    }

    public boolean hasInfiniteMass() {
        return this.inverseMass == 0f;
    }

    public float mass() {
        return this.mass;
    }

    public float inverseMass() {
        return this.inverseMass;
    }

    public Vector2f velocity() {
        return this.velocity;
    }

    public float angularVelocity() {
        return this.angularVelocity;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
