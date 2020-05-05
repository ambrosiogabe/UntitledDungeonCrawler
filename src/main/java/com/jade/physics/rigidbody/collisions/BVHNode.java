package com.jade.physics.rigidbody.collisions;

import com.jade.physics.rigidbody.Rigidbody;
import com.jade.physics.rigidbody.boundingVolumes.BoundingSphere;
import com.jade.physics.rigidbody.boundingVolumes.BoundingVolume;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import org.joml.Vector3f;

public class BVHNode {
    private BVHNode[] children = new BVHNode[2];
    private BVHNode parent = null;

    /* **************************************************************
     * Root volume of the tree, could be the root volume of a sub tree
     * ************************************************************** */
    BoundingVolume volume;

    /* **************************************************************
     * Holds the rigidbody at this node
     * Only leaf nodes contain rigidbodies, if this is not a leaf body = null
     * ************************************************************** */
    Rigidbody body = null;

    public BVHNode(BVHNode parent, BoundingVolume volume, Rigidbody body) {
        this.parent = parent;
        this.volume = volume;
        this.body = body;
    }

    public int getPotentialContacts(PotentialContact[] contacts, int index, int limit) {
        // Early out if we don't have room for contacts, or if we're a leaf node
        if (isLeaf() || limit == 0) return 0;

        // Get the potential contacts of one of our children with the other
        return children[0].getPotentialContactsWith(children[1], contacts, index, limit);
    }

    public int getPotentialContactsWith(BVHNode other, PotentialContact[] contacts, int index, int limit) {
        // Early out if we don't overlap or have room for more contacts
        if (!volume.overlaps(other.getVolume()) || limit == 0) return 0;

        // If we're both at leaf nodes, then we have a potential contact
        if (isLeaf() && other.isLeaf()) {
            contacts[index] = new PotentialContact(this.body, other.getBody());
            return 1;
        }

        // Determine which node to descend to. If either is a leaf, then we descend
        // into the other. If both are brances, then we use the one with the largest size
        if (other.isLeaf() || (!isLeaf() && volume.getSize() >= other.getVolume().getSize())) {
            // Recurse into self
            int count = children[0].getPotentialContactsWith(other, contacts, index, limit);

            // Check that we have enough slots to do the other side too
            if (limit > count) {
                return count + children[1].getPotentialContactsWith(other, contacts, index + count, limit - count);
            } else {
                return count;
            }
        } else {
            // Recurse into the other node
            int count = getPotentialContactsWith(other.children[0], contacts, index, limit);

            // Check that we have enough slots to do the other side too
            if (limit > count) {
                return count + getPotentialContactsWith(other.children[1], contacts, index + count, limit - count);
            } else {
                return count;
            }
        }
    }

    public void insert(Rigidbody newBody, BoundingVolume newVolume) {
        // If we are a leaf then the only option is to spawn two new children
        // and place the new body in one
        if (isLeaf()) {
            // Child one is a copy of us
            children[0] = new BVHNode(this, volume, body);

            // Child two holds the new body
            children[1] = new BVHNode(this, newVolume, newBody);

            this.body = null;

            recalculateBoundingVolume();
        } else {
            // Otherwise, we need to figure out which child to give the new body too.
            // We will give it to whoever would grow the least to incorporate it
            if (children[0].getVolume().getGrowth(newVolume) < children[1].getVolume().getGrowth(newVolume)) {
                children[0].insert(newBody, newVolume);
            } else {
                children[1].insert(newBody, newVolume);
            }
        }
    }

    /* **************************************************************
     * This method will delete this node and all it's children.
     * As a consequence, this means it may restructure the tree
     * to compensate and balance.
     * ************************************************************** */
    public void delete() {
        // If we don't have a parent ignore the sibling processing
        if (parent != null) {
            // Find our sibling
            BVHNode sibling;
            if (parent.getChildren()[0] == this) sibling = parent.getChildren()[1];
            else sibling = parent.getChildren()[0];

            // Write its data to our parent
            parent.setVolume(sibling.getVolume());
            parent.setBody(sibling.getBody());
            parent.getChildren()[0] = sibling.getChildren()[0];
            parent.getChildren()[1] = sibling.getChildren()[1];

            // Delete the sibling (we blank its parent and children
            // to avoid processing/deleting them).
            sibling.setParent(null);
            sibling.setBody(null);
            sibling.getChildren()[0] = null;
            sibling.getChildren()[1] = null;
            // If I was in C++ this is where I would delete the memory

            // Recalculate the parent's bounding volume
            parent.recalculateBoundingVolume();
        }

        // Delete our children
        if (children[0] != null) {
            children[0].setParent(null);
        }
        if (children[1] != null) {
            children[1].setParent(null);
        }
    }

    public void recalculateBoundingVolume() {
        if (isLeaf()) return;

        volume = new BoundingSphere((BoundingSphere)children[0].getVolume(), (BoundingSphere)children[1].getVolume());

        // Recurse up the tree
        if (parent != null) {
            parent.recalculateBoundingVolume();
        }
    }

    public void draw(Vector3f color) {
        if (volume instanceof BoundingSphere) {
            BoundingSphere sphere = (BoundingSphere)volume;
            DebugDraw.addSphere(sphere.getCenter(), sphere.getRadius(), 0.05f, color);
        }

        if (children[0] != null) {
            children[0].draw(Constants.COLOR3_CYAN);
        }
        if (children[1] != null) {
            children[1].draw(Constants.COLOR3_RED);
        }
    }

    public boolean isLeaf() {
        return body != null;
    }

    public Rigidbody getBody() {
        return this.body;
    }

    public void setBody(Rigidbody body) {
        this.body = body;
    }

    public BoundingVolume getVolume() {
        return this.volume;
    }

    public void setVolume(BoundingVolume volume) {
        this.volume = volume;
    }

    public BVHNode getParent() {
        return this.parent;
    }

    public void setParent(BVHNode parent) {
        this.parent = parent;
    }

    public BVHNode[] getChildren() {
        return this.children;
    }
}
