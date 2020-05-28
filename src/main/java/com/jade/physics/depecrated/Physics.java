package com.jade.physics.depecrated;

import com.jade.GameObject;
import com.jade.physics.forces.ForceRegistry;
import com.jade.physics.forces.Gravity;
import com.jade.physics.depecrated.boundingVolumes.BoundingVolume;
import com.jade.physics.depecrated.colliders.*;
import com.jade.physics.depecrated.collisions.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Physics {
    private ContactResolver resolver;
    private CollisionData data;
    private ForceRegistry registry;

    private Gravity gravity = new Gravity(new Vector3f(0, -100, 0));
    private BVHNode bvhTree = null;
    private PotentialContact[] potentialContacts;

    private List<Rigidbody> bodies;

    private float physicsTimestep = 1f / 60f;

    public Physics() {
        this.resolver = new ContactResolver();
        this.data = new CollisionData();
        this.registry = new ForceRegistry();
        this.potentialContacts = new PotentialContact[100];
        this.bodies = new ArrayList<>();
    }

    public void zeroForces() {
        this.registry.zeroForces();
    }

    public void addGameObject(GameObject go) {
        Rigidbody rb;
        Collider coll;
        BoundingVolume volume;

        // Ensure that game object has Rigidbody, Collider and BoundingVolume (All necessary for a physics object)
        if ((rb = go.getComponent(Rigidbody.class)) != null && (coll = go.getComponent(Collider.class)) != null && (volume = go.getComponent(BoundingVolume.class)) != null) {
            if (!rb.isStatic()) {
                //registry.add(rb, gravity);
            }
            if (coll instanceof SphereCollider) {
                //registry.add(rb, new TempForce());
            }

            this.bodies.add(rb);
            if (bvhTree == null) {
                bvhTree = new BVHNode(null, volume, rb);
            } else {
                bvhTree.insert(rb, volume);
            }
        }
    }

    public void update(float dt) {
        registry.updateForces(physicsTimestep);
        for (Rigidbody rb : bodies) {
            rb.applyForces(dt);
        }

        bvhTree.updatePositions();
        //bvhTree.draw(Constants.COLOR3_GREEN);
//        int numContacts = bvhTree.getPotentialContacts(potentialContacts, 0, potentialContacts.length);
        int numContacts = getAllPairs();
        narrowPhase(numContacts);

        Contact[] realContacts = data.getContacts();
        resolver.resolveContacts(realContacts, data.getNumContacts(), physicsTimestep);

        for (Rigidbody rb : bodies) {
            rb.integrate(dt);
        }
    }

    private void narrowPhase(int numContacts) {
        data.reset();

        for (int i=0; i < numContacts; i++) {
            PotentialContact c = potentialContacts[i];
            if (c == null) continue;

            Collider collider1 = c.body[0] != null ? c.body[0].gameObject.getComponent(Collider.class) : null;
            Collider collider2 = c.body[1] != null ? c.body[1].gameObject.getComponent(Collider.class) : null;
            if (collider1 == null && collider2 == null) continue;

            // Boxes
            if (collider1 instanceof BoxCollider && collider2 instanceof BoxCollider) {
                CollisionDetector.boxAndBox((BoxCollider)collider1, (BoxCollider)collider2, data);
            } else if (collider1 instanceof BoxCollider && collider2 instanceof Plane) {
                CollisionDetector.boxAndHalfSpace((BoxCollider)collider1, (Plane)collider2, data);
            } else if (collider1 instanceof BoxCollider && collider2 instanceof SphereCollider) {
                CollisionDetector.boxAndSphere((BoxCollider)collider1, (SphereCollider)collider2, data);
            }
            // Planes
            else if (collider1 instanceof Plane && collider2 instanceof BoxCollider) {
                CollisionDetector.boxAndHalfSpace((BoxCollider)collider2, (Plane)collider1, data);
            } else if (collider1 instanceof Plane && collider2 instanceof SphereCollider) {
                CollisionDetector.sphereAndHalfSpace((SphereCollider)collider2, (Plane)collider1, data);
            }
            // Spheres
            else if (collider1 instanceof SphereCollider && collider2 instanceof SphereCollider) {
                CollisionDetector.sphereAndSphere((SphereCollider)collider1, (SphereCollider)collider2, data);
            } else if (collider1 instanceof SphereCollider && collider2 instanceof Plane) {
                CollisionDetector.sphereAndHalfSpace((SphereCollider)collider1, (Plane)collider2, data);
            } else if (collider1 instanceof SphereCollider && collider2 instanceof BoxCollider) {
                CollisionDetector.boxAndSphere((BoxCollider)collider2, (SphereCollider)collider1, data);
            }
        }
    }

    private int getAllPairs() {
        int index = 0;
        for (int i=0; i < bodies.size(); i++) {
            for (int j=i; j < bodies.size(); j++) {
                Rigidbody body = bodies.get(i);
                Rigidbody otherBody = bodies.get(j);
                if (body != otherBody) {
                    potentialContacts[index] = new PotentialContact(body, otherBody);
                    index++;
                    if (index >= potentialContacts.length) {
                        return index - 1;
                    }
                }
            }
        }
        return index;
    }
}
