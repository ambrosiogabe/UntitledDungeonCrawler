package com.jade.physics.rigidbody.colliders;

import com.jade.Component;
import com.jade.physics.rigidbody.Rigidbody;
import com.jade.physics.rigidbody.collisions.CollisionData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Collider extends Component {
    protected Rigidbody body;
    protected Matrix4f offset;

    public abstract Matrix3f getInertiaTensor(float mass);

    public void generateContacts(Collider firstCollider, Collider secondCollider, CollisionData data) {

    }

    public Rigidbody getRigidbody() {
        return this.gameObject.getComponent(Rigidbody.class);
    }

    public Vector3f getAxis(int index) {
        Matrix4f data = this.gameObject.transform.modelMatrix;
        return new Vector3f(data.get(index, 0), data.get(index, 1), data.get(index, 2));
    }
}
