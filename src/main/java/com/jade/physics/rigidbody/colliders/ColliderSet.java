package com.jade.physics.rigidbody.colliders;

import com.jade.Component;
import com.jade.physics.rigidbody.collisions.CollisionData;
import org.joml.Matrix3f;

import java.util.List;

public class ColliderSet extends Collider {
    List<ColliderInSet> primitives;

    @Override
    public Matrix3f getInertiaTensor(float mass) {
        return null;
    }

    @Override
    public void generateContacts(Collider firstCollider, Collider secondCollider, CollisionData data) {

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
