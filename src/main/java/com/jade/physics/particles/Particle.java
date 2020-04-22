package com.jade.physics.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Particle {
    private Vector3f position;
    private Vector3f velocity;

    private Vector3f forceAccum;

    private float inverseMass, mass;
    private float lifeLeft;

    private Color color;
    private boolean isInfinite = false;
    private Vector3f acceleration;

    public Particle(Vector3f position, Vector3f velocity, float mass, float lifetime) {
        init(position, velocity, mass, lifetime, false);
    }

    public Particle(Vector3f position, Vector3f velocity, float mass, boolean isInfinite) {
        init(position, velocity, mass, 0.0f, isInfinite);
    }

    private void init(Vector3f position, Vector3f velocity, float mass, float lifetime, boolean isInfinite) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.inverseMass = mass > 0.0f ? 1.0f / mass : 0.0f;
        this.lifeLeft = lifetime;
        this.isInfinite = isInfinite;

        this.forceAccum = new Vector3f(0.0f, 0.0f, 0.0f);
        this.color = Color.RED;
        this.acceleration = forceAccum;
    }

    public void update(float dt) {
        if (inverseMass <= 0.0f || (this.lifeLeft <= 0.0f && !isInfinite)) return;
        this.lifeLeft -= dt;

        this.velocity.add(forceAccum.mul(dt).mul(this.inverseMass));
        this.clearAccumulator();

        this.position.add(new Vector3f(this.velocity).mul(dt).mul(100));
    }

    public void addForce(Vector3f force) {
        this.forceAccum.add(force);
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

    public Vector3f getAcceleration() {
        return this.acceleration;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f pos) {
        this.position.set(pos);
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public float getMass() {
        return this.mass;
    }

    public float getInverseMass() {
        return this.inverseMass;
    }

    public void draw(Graphics2D g2) {
        if (this.lifeLeft <= 0.0f && !isInfinite) return;

        g2.setColor(this.color);
        g2.draw(new Rectangle2D.Float(this.position.x, this.position.y, 5, 5));
    }

    private void clearAccumulator() {
        this.forceAccum.x = 0.0f;
        this.forceAccum.y = 0.0f;
        this.forceAccum.z = 0.0f;
    }
}
