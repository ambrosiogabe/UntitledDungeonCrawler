package com.jade.physics.particles;

import com.jade.Component;
import com.jade.UIObject;
import com.jade.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Particle extends Component {

    private Vector3f velocity;

    private Vector3f forceAccum;
    private float torque = 0;

    private float inverseMass, mass;
    private float lifeLeft;

    private Vector4f color;
    private boolean isInfinite = false;
    private Vector3f acceleration;
    private float angularVelocity = 0;
    private float alpha = 0;
    private float angle = 0;
    private float inertiaTensor;

    public Particle(Vector3f velocity, float mass, float lifetime) {
        init(velocity, mass, lifetime, false);
    }

    public Particle(Vector3f velocity, float mass, boolean isInfinite) {
        init(velocity, mass, 0.0f, isInfinite);
    }

    private void init(Vector3f velocity, float mass, float lifetime, boolean isInfinite) {
        this.velocity = velocity;
        this.mass = mass;
        this.inverseMass = mass > 0.0f ? 1.0f / mass : 0.0f;
        this.lifeLeft = lifetime;
        this.isInfinite = isInfinite;

        this.forceAccum = new Vector3f(0.0f, 0.0f, 0.0f);
        this.color = Constants.RED;
        this.acceleration = forceAccum;
    }

    @Override
    public void start() {
        this.inertiaTensor = this.mass *
                (this.uiObject.transform.scale.y * this.uiObject.transform.scale.y + this.uiObject.transform.scale.x * this.uiObject.transform.scale.x) / 12000;
    }

    @Override
    public void update(float dt) {
        if (inverseMass <= 0.0f || (this.lifeLeft <= 0.0f && !isInfinite)) return;
        this.lifeLeft -= dt;

        this.velocity.add(forceAccum.mul(dt).mul(this.inverseMass));
        this.clearAccumulator();

        uiObject.transform.position.add(new Vector3f(this.velocity).mul(dt).mul(100));

        torque += this.angularVelocity;
        this.alpha = this.torque / this.inertiaTensor;
        this.angularVelocity += this.alpha * dt;
        uiObject.transform.rotation.z += this.angularVelocity * dt;

        this.torque = 0;
    }

    @Override
    public Component copy() {
        return null;
    }

    public void addForce(Vector3f force) {
        this.forceAccum.add(force);
    }

    public void addTorque(float torque) {
        this.torque += torque;
    }

    public boolean hasFiniteMass() {
        return this.inverseMass > 0.0f;
    }

    public void setVelocity(Vector3f vel) {
        this.velocity.set(vel);
    }

    public Vector3f getVelocity() {
        return this.velocity;
    }

    public float getAngularVelocity() {
        return this.angularVelocity;
    }

    public Vector3f getAcceleration() {
        return this.acceleration;
    }

    public void zeroForces() {
        this.velocity.zero();
        this.angularVelocity = 0;
        this.forceAccum.zero();
        this.torque = 0;
    }

    public float getMass() {
        return this.mass;
    }

    public float getInverseMass() {
        return this.inverseMass;
    }

    private void clearAccumulator() {
        this.forceAccum.x = 0.0f;
        this.forceAccum.y = 0.0f;
        this.forceAccum.z = 0.0f;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
