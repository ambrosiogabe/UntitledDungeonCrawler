package com.jade.physics2d;

import com.jade.GameObject;
import com.jade.Window;
import com.jade.physics2d.forces.ForceRegistry2D;
import com.jade.physics2d.forces.Gravity2D;
import com.jade.physics2d.primitives.Box2D;
import com.jade.physics2d.primitives.Collider2D;
import com.jade.physics2d.primitives.Raycast2D;
import com.jade.physics2d.rigidbody.Rigidbody2D;
import com.jade.util.JMath;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Physics2D {
    private QuadTreeNode quadTree;
    private ForceRegistry2D registry;
    private ContactResolver2D contactResolver;

    // IMPORTANT: These two lists must remain in sync at all times
    private List<Rigidbody2D> dynamicRigidbodies;
    private List<Collider2D> colliders;

    private Gravity2D gravity;
    private float physicsTimestep = 1f / 60f;

    public Physics2D() {
        this.quadTree = new QuadTreeNode(new Box2D(new Vector2f(0, 0), new Vector2f(1920, 1080)));
        this.registry = new ForceRegistry2D();
        this.contactResolver = new ContactResolver2D();

        this.dynamicRigidbodies = new ArrayList<>();
        this.colliders = new ArrayList<>();

        this.gravity = new Gravity2D(new Vector2f(0, -50));
    }

    public void addGameObject(GameObject go) {
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        if (rb != null) {
            if (!rb.hasInfiniteMass() && !rb.isStatic()) {
                // Only add gravity, and to our list of rigidbodies if it is dynamic
                registry.add(rb, gravity);
                this.dynamicRigidbodies.add(rb);
                this.colliders.add(go.getComponent(Collider2D.class));
            }

            QuadTreeData data = new QuadTreeData(go);
            quadTree.insert(data);
        }
    }

    public void update(float dt) {
        // Apply all forces for this frame
        registry.updateForces(physicsTimestep);
        for (Rigidbody2D rb : dynamicRigidbodies) {
            rb.applyForces(physicsTimestep);
            rb.integrate(physicsTimestep);
        }

        // Update dynamic objects in quadtree
        int size = dynamicRigidbodies.size();
        for (int i=0; i < size; i++) {
            QuadTreeData data = new QuadTreeData(dynamicRigidbodies.get(i).gameObject);
            quadTree.update(data);
        }
        //quadTree.draw();

        // Resolve all collisions in quadtree
        for (int i=0; i < size; i++) {
            Collider2D coll = colliders.get(i);
            List<QuadTreeData> collisions = quadTree.query(coll);

            if (collisions.size() > 0)
                dynamicRigidbodies.get(i).setColliding(true);
            else
                dynamicRigidbodies.get(i).setColliding(false);

            for (int j=0; j < collisions.size(); j++) {
                //coll.gameObject.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
                QuadTreeData data = collisions.get(j);
                contactResolver.resolve(data.collider(), coll);
            }
        }

//        // Update position and velocity of all dynamic objects
//        for (Rigidbody2D rb : dynamicRigidbodies) {
//            rb.integrate(physicsTimestep);
//        }
    }

    public static GameObject raycast(Vector2f origin, Vector2f direction, float maxDistance, GameObject ignore) {
        Raycast2D ray = new Raycast2D(origin, direction, maxDistance, ignore);
        return Window.getScene().getPhysics2D().raycast(ray);
    }

    public GameObject raycast(Raycast2D ray) {
        List<QuadTreeData> result = quadTree.query(ray);
        if (result.size() > 0) {
            int minDistance = Integer.MAX_VALUE;
            GameObject go = null;
            for (int i=0; i < result.size(); i++) {
                QuadTreeData current = result.get(i);
                float distance = JMath.vector2fFrom3f(current.gameObject().transform.position).sub(ray.origin()).lengthSquared();
                if (distance < minDistance) {
                    distance = minDistance;
                    go = current.gameObject();
                }
            }

            return go;
        }

        return null;
    }
}
