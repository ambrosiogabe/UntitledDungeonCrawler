package com.jade.physics;

import com.jade.physics.coRigidbody.CollisionManifold;
import com.jade.physics.coRigidbody.Collisions;
import com.jade.physics.coRigidbody.Rigidbody;
import com.jade.physics.primitives.Collider;
import com.jade.physics.rigidbody.colliders.CollisionDetector;
import com.jade.physics2d.rigidbody.CollisionDetector2D;
import com.jade.util.JMath;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private List<Rigidbody> bodies;
    private List<Collider> constraints;

    private List<Rigidbody> colliders1;
    private List<Rigidbody> colliders2;
    private List<CollisionManifold> results;

    // Keep between 0.2 and 0.8
    float linearProjectionPercent;
    float penetrationSlack;
    // [1 to 20], Larger = more accurate
    int impulseIteration;

    public PhysicsSystem() {
        bodies = new ArrayList<>();
        constraints = new ArrayList<>();
        this.colliders1 = new ArrayList<>();
        this.colliders2 = new ArrayList<>();
        this.results = new ArrayList<>();

        this.linearProjectionPercent = 0.45f;
        this.penetrationSlack = 0.01f;
        this.impulseIteration = 6;
    }

    public void update(float deltaTime) {
        colliders1.clear();
        colliders2.clear();
        results.clear();

        // Find any collisions
        int size = bodies.size();
        for (int i=0; i < size; i++) {
            for (int j=i; j < size; j++) {
                if (i == j) continue;

                CollisionManifold result = new CollisionManifold();
                result.reset();
                Rigidbody m1 = bodies.get(i);
                Rigidbody m2 = bodies.get(j);
                Collider c1 = m1.gameObject.getComponent(Collider.class);
                Collider c2 = m2.gameObject.getComponent(Collider.class);

                if (c1 != null && c2 != null && !(m1.hasInfiniteMass() && m2.hasInfiniteMass())) {
                    result = Collisions.findCollisionFeatures(c1, c2);
                }

                if (result.colliding()) {
                    colliders1.add(bodies.get(i));
                    colliders2.add(bodies.get(j));
                    results.add(result);
                }
            }
        }

        // Apply all forces
        for (int i=0; i < size; i++) {
            bodies.get(i).applyForces();
        }

        // Resolve collisions
        for (int k=0; k < impulseIteration; k++) {
            for (int i=0; i < results.size(); i++) {
                int jSize = results.get(i).contacts().size();
                for (int j=0; j < jSize; j++) {
                    Rigidbody m1 = colliders1.get(i);
                    Rigidbody m2 = colliders2.get(i);
                    applyImpulse(m1, m2, results.get(i), j);
                }
            }
        }

        // Update velocities of bodies
        for (int i=0; i < size; i++) {
            bodies.get(i).physicsUpdate(deltaTime);
        }

        // Perform linear projection
        for (int i=0; i < results.size(); i++) {
            Rigidbody m1 = colliders1.get(i);
            Rigidbody m2 = colliders2.get(i);
            float totalInverseMass = m1.inverseMass() + m2.inverseMass();
            if (totalInverseMass == 0f) {
                continue;
            }

            float depth = Math.max(results.get(i).depth() - penetrationSlack, 0f);
            float scalar = depth / totalInverseMass;
            Vector3f correction = new Vector3f(results.get(i).normal()).mul(scalar).mul(linearProjectionPercent);

            m1.position().sub(new Vector3f(correction).mul(m1.inverseMass()));
            m2.position().add(new Vector3f(correction).mul(m2.inverseMass()));

            m1.synchCollisionVolumes();
            m2.synchCollisionVolumes();
        }

        // Solve constraints if applicable
        for (int i=0; i < size; i++) {
            bodies.get(i).solveConstraints(constraints);
        }
    }

    public void applyImpulse(Rigidbody a, Rigidbody b, CollisionManifold m, int c) {
        // Linear velocity
        float invMass1 = a.inverseMass();
        float invMass2 = b.inverseMass();
        float invMassSum = invMass1 + invMass2;
        if (invMassSum == 0f) {
            return;
        }

        // Store point of contact relative to center of mass
        Vector3f r1 = new Vector3f(m.contacts().get(c)).sub(a.gameObject.transform.position);
        Vector3f r2 = new Vector3f(m.contacts().get(c)).sub(b.gameObject.transform.position);

        // Store the inverse inertia tensor for both objects
        Matrix3f i1 = a.getInverseInertiaTensor();
        Matrix3f i2 = b.getInverseInertiaTensor();

        // Relative velocity
        Vector3f relativeVel = new Vector3f(b.velocity()).add(new Vector3f(b.angularVelocity()).cross(r2)).sub(
                new Vector3f(a.velocity()).add(new Vector3f(a.angularVelocity()).cross(r1))
        );

        // Relative collision normal
        Vector3f relativeNorm = new Vector3f(m.normal());
        relativeNorm.normalize();

        // Moving away from each other? Do nothing
        if (relativeVel.dot(relativeNorm) > 0f) {
            return;
        }

        float e = Math.min(a.cor(), b.cor());
        float numerator = (-(1f + e) * relativeVel.dot(relativeNorm));
        float d1 = invMassSum;
        Vector3f d2 = new Vector3f(r1).cross(relativeNorm).mul(i1).cross(r1);
        Vector3f d3 = new Vector3f(r2).cross(relativeNorm).mul(i2).cross(r2);
        float denominator = d1 + relativeNorm.dot(new Vector3f(d2).add(d3));

        float j = (denominator == 0f) ? 0f : numerator / denominator;
        if (m.contacts().size() > 0f && j != 0f) {
            j /= (float)m.contacts().size();
        }

        Vector3f impulse = new Vector3f(relativeNorm).mul(j);
        System.out.println(impulse);
        a.addLinearImpulse(new Vector3f(impulse).mul(invMass1).mul(-1f));
        b.addLinearImpulse(new Vector3f(impulse).mul(invMass2).mul(1f));
        a.angularVelocity().sub(new Vector3f(r1).cross(impulse).mul(i1));
        b.angularVelocity().add(new Vector3f(r2).cross(impulse).mul(i2));

        // Apply Friction
        Vector3f t = new Vector3f(relativeVel).sub(new Vector3f(relativeNorm).mul(relativeVel.dot(relativeNorm)));
        if (JMath.compare(t.lengthSquared(), 0f)) {
            return;
        }
        t.normalize();

        numerator = -relativeVel.dot(t);
        d1 = invMassSum;
        d2 = i1.transform(new Vector3f(r1).cross(t)).cross(r1);
        d3 = i2.transform(new Vector3f(r2).cross(t)).cross(r2);
        denominator = d1 + t.dot(new Vector3f(d2).add(d3));
        if (denominator == 0f) {
            return;
        }

        float jt = numerator / invMassSum;
        if (m.contacts().size() > 0f && jt != 0f) {
            jt /= (float)m.contacts().size();
        }
        if (JMath.compare(jt, 0f)) {
            return;
        }

        float friction = (float)Math.sqrt(a.friction() * b.friction());
        if (jt > j * friction) {
            jt = j * friction;
        } else if (jt < -j * friction) {
            jt = -j * friction;
        }

        Vector3f tangentialImpulse = new Vector3f(t).mul(jt);
        a.addLinearImpulse(new Vector3f(tangentialImpulse).mul(invMass1).mul(-1f));
        b.addLinearImpulse(new Vector3f(tangentialImpulse).mul(invMass2).mul(1f));
        a.angularVelocity().sub(new Vector3f(r1).cross(tangentialImpulse).mul(i1));
        b.angularVelocity().add(new Vector3f(r2).cross(tangentialImpulse).mul(i2));
    }

    public void addRigidbody(Rigidbody body) {
        this.bodies.add(body);
    }

    public void addConstraint(Collider constraint) {
        this.constraints.add(constraint);
    }

    public void clearRigidbodies() {
        this.bodies.clear();
    }

    public void clearConstraints() {
        this.constraints.clear();
    }
}
