package com.jade.physics.coRigidbody;

import com.jade.Component;
import com.jade.physics.primitives.Collider;
import com.jade.physics.primitives.IntersectionTester;
import com.jade.physics.primitives.Ray;
import com.jade.physics.primitives.RaycastResult;
import com.jade.renderer.Line;
import com.jade.util.DebugDraw;
import org.joml.Vector3f;

import java.util.List;

public class Rigidbody extends Component {
    private Vector3f position, oldPosition;
    private Vector3f forces, velocity;
    private float inverseMass, mass;
    private float cor; // Coefficient of Restitution

    private Vector3f gravity;
    private float friction;

    public Rigidbody(float mass) {
        this.position = new Vector3f();
        this.oldPosition = new Vector3f();
        this.forces = new Vector3f();
        this.velocity = new Vector3f();

        this.friction = 0.6f;
        this.gravity = new Vector3f(0f, -9.82f, 0f);
        this.mass = mass;
        this.inverseMass = this.mass == 0f ? 0f : 1 / this.mass;
        this.cor = 0.5f;
    }

    @Override
    public void start() {
        this.position.set(this.gameObject.transform.position);
        this.oldPosition.set(this.gameObject.transform.position);
    }

    public void physicsUpdate(float deltaTime) {
        float damping = 0.98f;
        Vector3f acceleration = new Vector3f(forces).mul(inverseMass);
        velocity.add(acceleration.mul(deltaTime));
        velocity.mul(damping);

        position.add(new Vector3f(velocity).mul(deltaTime));
        synchCollisionVolumes();
    }

    public void applyForces() {
        this.forces.set(new Vector3f(gravity).mul(mass));
    }

    public void solveConstraints(List<Collider> constraints) {
//        int size = constraints.size();
//        for (int i=0; i < size; i++) {
//            Line lineTraveled = new Line(oldPosition, position);
//            if (IntersectionTester.lineTest(constraints.get(i), lineTraveled)) {
//                Vector3f velocity = new Vector3f(position).sub(oldPosition);
//                Vector3f direction = new Vector3f(velocity);
//                Ray ray = new Ray(oldPosition, direction);
//                RaycastResult result = new RaycastResult();
//                if (IntersectionTester.raycast(constraints.get(i), ray, result)) {
//                    position.set(result.point()).add(new Vector3f(result.normal()).mul(0.02f));
//                    Vector3f vn = new Vector3f(result.normal()).mul(result.normal().dot(velocity));
//                    Vector3f vt = new Vector3f(velocity).sub(vn);
//
//                    oldPosition.set(new Vector3f(position).sub(new Vector3f(vt).sub(new Vector3f(vn).mul(bounce))));
//
//                    gameObject.transform.position.set(position);
//                    break;
//                }
//            }
//        }
    }

    public void synchCollisionVolumes() {
        this.gameObject.transform.position.set(this.position);
    }

    public void addLinearImpulse(Vector3f impulse) {
        this.velocity.add(impulse);
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public void setPosition(Vector3f pos) {
        this.position.set(pos);
        this.oldPosition.set(pos);
    }

    public Vector3f position() {
        return this.position;
    }

    public void setCor(float cor) {
        this.cor = cor;
    }

    public float cor() {
        return this.cor;
    }

    public float inverseMass() {
        return this.inverseMass;
    }

    public Vector3f velocity() {
        return this.velocity;
    }

    public float friction() {
        return this.friction;
    }
}
