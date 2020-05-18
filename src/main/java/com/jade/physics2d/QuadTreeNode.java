package com.jade.physics2d;

import com.jade.physics2d.primitives.Box2D;
import com.jade.physics2d.primitives.Collider2D;
import com.jade.physics2d.rigidbody.CollisionDetector2D;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QuadTreeNode {
    private static final int MAX_DEPTH = 5;
    private static final int MAX_OBEJCTS_PER_NODE = 15;

    protected List<QuadTreeNode> children;
    protected List<QuadTreeData> contents;
    protected int currentDepth;
    protected Box2D nodeBounds;

    public QuadTreeNode(Box2D bounds) {
        this.nodeBounds = bounds;
        this.currentDepth = 0;
        this.children = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    public QuadTreeNode(Box2D bounds, int depth) {
        this.nodeBounds = bounds;
        this.currentDepth = depth;
        this.children = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    public List<QuadTreeData> query(Collider2D area) {
        List<QuadTreeData> result = new ArrayList<>();
        if (!CollisionDetector2D.colliderAndCollider(area, nodeBounds)) {
            // If we're not intersecting with area requested, return the empty list
            return result;
        }

        if (isLeaf()) {
            // If we are a leaf, check if the area is intersecting with any of the contents
            int size = contents.size();
            for (int i=0; i < size; i++) {
                if (contents.get(i).collider() != area && CollisionDetector2D.colliderAndCollider(contents.get(i).collider(), area)) {
                    result.add(contents.get(i));
                }
            }
        } else {
            // Finally, if we're not dealing with a leaf node, recursively check all of our children
            // for collisions
            int size = children.size();
            for (int i=0; i < size; i++) {
                List<QuadTreeData> recurse = children.get(i).query(area);
                if (recurse.size() > 0) {
                    result.addAll(recurse);
                }
            }
        }

        return result;
    }

    public void insert(QuadTreeData data) {
        if (!CollisionDetector2D.colliderAndCollider(data.collider(), nodeBounds)) {
            // The object does not fit into this node
            return;
        }

        if (isLeaf() && contents.size() + 1 > MAX_OBEJCTS_PER_NODE) {
            // Try to split and add object to this node
            split();
        }

        if (isLeaf()) {
            contents.add(data);
        } else {
            // Recursively try to insert into all children
            int size = children.size();
            for (int i=0; i < size; i++) {
                children.get(i).insert(data);
            }
        }
    }

    public void remove(QuadTreeData data) {
        if (isLeaf()) {
            // If we are dealing with a leaf, look for the data to remove
            int removeIndex = -1;
            int size = contents.size();
            for (int i=0; i < size; i++) {
                if (contents.get(i).equals(data)) {
                    removeIndex = i;
                    break;
                }
            }

            // If we found the object to remove, then remove it
            if (removeIndex != -1) {
                contents.remove(removeIndex);
            }
        } else {
            // If the node is not a leaf, call remove recursively
            int size = children.size();
            for (int i=0; i < size; i++) {
                children.get(i).remove(data);
            }
        }

        shake();
    }

    public void update(QuadTreeData data) {
        remove(data);
        insert(data);
    }

    public void shake() {
        if (!isLeaf()) {
            int numObjects = numObjects();
            if (numObjects == 0) {
                children.clear();
            } else if (numObjects < MAX_OBEJCTS_PER_NODE) {
                // If this node has enough room for all the children, add the children
                // to this node and remove unnecessary nodes
                Queue<QuadTreeNode> process = new LinkedList<>();
                process.add(this);
                while (process.size() > 0) {
                    QuadTreeNode processing = process.remove();
                    if (!processing.isLeaf()) {
                        int size = processing.children.size();
                        for (int i=0; i < size; i++) {
                            process.add(processing.children.get(i));
                        }
                    } else {
                        contents.addAll(processing.contents);
                    }
                }
                children.clear();
            }
        }
    }

    public void split() {
        if (currentDepth + 1 > MAX_DEPTH) {
            // We cannot split any further
            return;
        }

        Vector2f min = nodeBounds.getMin();
        Vector2f max = nodeBounds.getMax();
        Vector2f center = new Vector2f(min).add(nodeBounds.getHalfSize());

        // Split into four equal sized areas for the children
        Box2D[] childAreas = {
            new Box2D(new Vector2f(min.x, min.y), new Vector2f(center.x, center.y)),
            new Box2D(new Vector2f(center.x, min.y), new Vector2f(max.x, center.y)),
            new Box2D(new Vector2f(center.x, center.y), new Vector2f(max.x, max.y)),
            new Box2D(new Vector2f(min.x, center.y), new Vector2f(center.x, max.y))
        };

        // Add the new areas as this node's children
        for (int i=0; i < 4; i++) {
            children.add(new QuadTreeNode(childAreas[i], currentDepth + 1));
        }

        // Distribute the children into the appropriate areas
        int size = contents.size();
        for (int i=0; i < size; i++) {
            children.get(i).insert(contents.get(i));
        }
        contents.clear();
    }

    public void reset() {
        if (isLeaf()) {
            int size = contents.size();
            for (int i=0; i < size; i++) {
                contents.get(i).setFlag(false);
            }
        } else {
            int size = children.size();
            for (int i=0; i < size; i++) {
                children.get(i).reset();
            }
        }
    }

    public int numObjects() {
        reset();
        int objectCount = contents.size();
        int size = contents.size();
        for (int i=0; i < size; i++) {
            contents.get(i).setFlag(true);
        }

        // Iterate through all children non-recursively and count the number
        // of children
        Queue<QuadTreeNode> process = new LinkedList<>();
        process.add(this);
        while (process.size() > 0) {
            QuadTreeNode processing = process.remove();

            if (!processing.isLeaf()) {
                size = processing.children.size();
                for (int i=0; i < size; i++) {
                    process.add(processing.children.get(i));
                }
            } else {
                size = processing.contents.size();
                for (int i=0; i < size; i++) {
                    if (!processing.contents.get(i).flag()) {
                        objectCount++;
                        processing.contents.get(i).setFlag(true);
                    }
                }
            }
        }

        reset();
        return objectCount;
    }

    public void draw() {
        DebugDraw.addBox2D(JMath.vector2fFrom3f(this.nodeBounds.getCenter()), new Vector2f(this.nodeBounds.getHalfSize()).mul(2f), 0f, 1f);
        for (QuadTreeNode child : children) {
            child.draw();
        }
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }
}
