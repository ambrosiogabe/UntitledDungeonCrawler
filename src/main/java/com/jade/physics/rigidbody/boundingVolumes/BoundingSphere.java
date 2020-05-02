package com.jade.physics.rigidbody.boundingVolumes;

import com.jade.Component;
import com.jade.util.DebugDraw;
import org.joml.Vector3f;

public class BoundingSphere extends BoundingVolume {
    private Vector3f center;
    private float radius;

    /* *******************************************************************
     * Creates a new bounding sphere at the given center with the radius
     * ******************************************************************* */
    public BoundingSphere(Vector3f center, float radius) {
        this.center = new Vector3f(center);
        this.radius = radius;
    }

    /* *******************************************************************
     * Creates a bounding sphere that overlaps the two bounding spheres
     * ******************************************************************* */
    public BoundingSphere(BoundingSphere one, BoundingSphere two) {
        Vector3f centerOffset = new Vector3f(two.getCenter()).sub(one.getCenter());
        float distance = centerOffset.lengthSquared();
        float radiusDiff = two.getRadius() - one.getRadius();

        // Check whether the larger sphere encloses the small one
        if (radiusDiff * radiusDiff >= distance) {
            if (one.getRadius() > two.getRadius()) {
                center = new Vector3f(one.getCenter());
                radius = one.getRadius();
            } else {
                center = new Vector3f(two.getCenter());
                radius = two.getRadius();
            }
        } else {
            // Otherwise we need to work with partially overlapping spheres
            distance = (float)Math.sqrt(distance);
            radius = (distance + one.getRadius() + two.getRadius()) * 0.5f;

            // The new center is based on one's center, moved toward two's center
            // by an amount proportional to the spheres' radii
            center = new Vector3f(one.getCenter());
            if (distance > 0) {
                center.fma(((radius - one.getRadius()) / distance), centerOffset);
            }
        }

    }

    @Override
    public boolean overlaps(BoundingVolume other) {
        if (other instanceof BoundingSphere) {
            BoundingSphere sphere = (BoundingSphere)other;
            float distanceSquared = new Vector3f(center).sub(sphere.getCenter()).lengthSquared();
            return distanceSquared < (radius + sphere.getRadius()) * (radius + sphere.getRadius());
        }

        return false;
    }

    @Override
    public float getSize() {
        return 0;
    }

    @Override
    public float getGrowth(BoundingVolume other) {
        if (other instanceof BoundingSphere) {
            BoundingSphere otherSphere = (BoundingSphere)other;
            BoundingSphere newSphere = new BoundingSphere(this, otherSphere);

            return newSphere.getRadius() * newSphere.getRadius() - this.radius * this.radius;
        }

        return -1;
    }

    public Vector3f getCenter() {
        return this.center;
    }

    public float getRadius() {
        return this.radius;
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
