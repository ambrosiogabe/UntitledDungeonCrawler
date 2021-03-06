package com.jade.physics.rigidbody;

import com.jade.Component;
import com.jade.physics.primitives.Collider;
import com.jade.util.JMath;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class Rigidbody extends Component {
    // Linear
    private Vector3f position, oldPosition;
    private Vector3f forces, velocity;
    private float inverseMass, mass;

    // Angular
    private Vector3f angularVelocity, torques;
    private Matrix4f inverseInertiaTensor;

    private float cor; // Coefficient of Restitution

    private Vector3f gravity;
    private float friction;

    public Rigidbody(float mass) {
        this.position = new Vector3f();
        this.oldPosition = new Vector3f();
        this.forces = new Vector3f();
        this.velocity = new Vector3f();

        this.angularVelocity = new Vector3f();
        this.torques = new Vector3f();

        this.friction = 0.6f;
        this.gravity = new Vector3f(0f, -9.82f, 0f);
        this.mass = mass;
        this.inverseMass = this.mass == 0f ? 0f : 1 / this.mass;
        this.cor = 0.45f;
    }

    @Override
    public void start() {
        this.position.set(this.gameObject.transform.position);
        this.oldPosition.set(this.gameObject.transform.position);
        Matrix4f inertiaTensor = this.gameObject.getComponent(Collider.class).getInertiaTensor(this.mass);
        this.inverseInertiaTensor = new Matrix4f();
        if (this.mass != 0f) {
            inertiaTensor.invert(this.inverseInertiaTensor);
        } else {
            inverseInertiaTensor.set(0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f);
        }
    }

    public void physicsUpdate(float deltaTime) {
        if (this.mass == 0f) return;

        float damping = 0.98f;

        // Calculate linear velocity
        Vector3f acceleration = new Vector3f(forces).mul(inverseMass);
        velocity.add(acceleration.mul(deltaTime));
        velocity.mul(damping);

        if (Math.abs(velocity.x) < 0.0001f) {
            velocity.x = 0f;
        }
        if (Math.abs(velocity.y) < 0.0001f) {
            velocity.y = 0f;
        }
        if (Math.abs(velocity.z) < 0.0001f) {
            velocity.z = 0f;
        }

        // Calculate angular velocity
        Vector3f angularAcceleration = JMath.mul(torques, inverseInertiaTensor);
        angularVelocity.add(new Vector3f(angularAcceleration).mul(deltaTime));
        angularVelocity.mul(damping);

        if (Math.abs(angularVelocity.x) < 0.0001f) {
            angularVelocity.x = 0f;
        }
        if (Math.abs(angularVelocity.y) < 0.0001f) {
            angularVelocity.y = 0f;
        }
        if (Math.abs(angularVelocity.z) < 0.0001f) {
            angularVelocity.z = 0f;
        }

        // Update linear position
        position.add(new Vector3f(velocity).mul(deltaTime));

        // Update angular data
        // Quaternion differentiation: https://fgiesen.wordpress.com/2012/08/24/quaternion-differentiation/
        Quaternionf q = new Quaternionf(angularVelocity.x * deltaTime * 0.5f,
                                        angularVelocity.y * deltaTime * 0.5f,
                                        angularVelocity.z * deltaTime * 0.5f, 0);
        q.mul(this.gameObject.transform.orientation);
        this.gameObject.transform.orientation.add(q);
        this.gameObject.transform.orientation.normalize();
        //this.gameObject.transform.orientation.integrate(deltaTime, angularVelocity.x, angularVelocity.y, angularVelocity.z);

        synchCollisionVolumes();

        clearAccumulators();
    }

    public void applyForces() {
        this.forces.set(new Vector3f(gravity).mul(mass));
    }

    private void clearAccumulators() {
        this.forces.zero();
        this.torques.zero();
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

    public Vector3f getPointInWorldSpace(Vector3f localPoint) {
        Matrix4f scaleFree = new Matrix4f(this.gameObject.transform.modelMatrix);
        scaleFree.mul(1f / this.gameObject.transform.scale.x, 0f, 0f, 0f,
                0f, 1f / this.gameObject.transform.scale.y, 0f, 0f,
                0f, 0f, 1f / this.gameObject.transform.scale.z, 0f,
                0f, 0f, 0f, 1f);
        return JMath.mul(localPoint, scaleFree);
    }

    public void synchCollisionVolumes() {
        this.gameObject.transform.position.set(this.position);
    }

    public void addForce(Vector3f force) {
        this.forces.add(force);
    }

    public void addForceAtPoint(Vector3f force, Vector3f worldPoint) {
        Vector3f centerOfMass = this.position;
        Vector3f torque = new Vector3f(worldPoint).sub(centerOfMass).cross(force);

        forces.add(force);
        torques.add(torque);
    }

    public void addLinearImpulse(Vector3f impulse) {
        this.velocity.add(impulse);
    }

    public void addRotationalImpulse(Vector3f point, Vector3f impulse) {
        Vector3f centerOfMass = this.position;
        Vector3f torque = new Vector3f(point).sub(centerOfMass).cross(impulse);

        Vector3f angularAcceleration = JMath.mul(torque, inverseInertiaTensor);
        angularVelocity.add(angularAcceleration);
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

    public float mass() {
        return this.mass;
    }

    public Vector3f velocity() {
        return this.velocity;
    }

    public float friction() {
        return this.friction;
    }

    public Matrix4f getInverseInertiaTensor() {
        return this.inverseInertiaTensor;
    }

    public Vector3f angularVelocity() {
        return this.angularVelocity;
    }

    public boolean hasInfiniteMass() {
        return this.mass == 0f;
    }

    public void zeroForces() {
        this.forces.zero();
        this.torques.zero();
        this.velocity.zero();
        this.angularVelocity.zero();
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
