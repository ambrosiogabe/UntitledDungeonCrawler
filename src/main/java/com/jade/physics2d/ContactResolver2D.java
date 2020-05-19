package com.jade.physics2d;

import com.jade.physics2d.primitives.Box2D;
import com.jade.physics2d.primitives.Circle;
import com.jade.physics2d.primitives.Collider2D;
import com.jade.physics2d.rigidbody.Rigidbody2D;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ContactResolver2D {

    public void resolve(Collider2D coll1, Collider2D coll2) {
        colliderAndCollider(coll1, coll2);
    }

    // TODO: ALL THIS STUFF IS TEMPORARY, REPLACE WITH CONTACTS THAT ARE RETURNED FROM COLLISION_DETECTOR
    // =============================================================================
    // Generic primitive vs primitive tests
    // =============================================================================
    private void colliderAndCollider(Collider2D coll1, Collider2D coll2) {
        if (coll1 instanceof Circle) {
            if (coll2 instanceof Circle) {
                circleAndCircle((Circle)coll1, (Circle)coll2);
            } else if (coll2 instanceof Box2D) {
                circleAndBox2D((Circle)coll1, (Box2D)coll2);
            }
        } else if (coll1 instanceof Box2D) {
            if (coll2 instanceof Box2D) {
                box2DAndBox2D((Box2D)coll1, (Box2D)coll2);
            } else if (coll2 instanceof Circle) {
                box2DAndCircle((Box2D)coll1, (Circle)coll2);
            }
        }
    }

    // =============================================================================
    // Circle vs. Primitive tests
    // =============================================================================
    private void circleAndCircle(Circle c1, Circle c2) {
        Vector2f lineBetweenCenters = JMath.vector2fFrom3f(c1.gameObject.transform.position).sub(JMath.vector2fFrom3f(c2.gameObject.transform.position));
        float length = lineBetweenCenters.length();
        float penetration = (c1.radius() + c2.radius()) - length;
        lineBetweenCenters.div(length);

        c1.gameObject.transform.position.add(JMath.vector3fFrom2f(lineBetweenCenters).mul(penetration));
    }

    private void circleAndBox2D(Circle circle, Box2D box) {
        // Treat the box as if it was located at halfSize.x, halfSize.y
        Vector2f min = new Vector2f();
        Vector2f max = box.getHalfSize().mul(2);

        // Create a circle in box's local space
        Vector2f r = JMath.vector2fFrom3f(circle.gameObject.transform.position).sub(JMath.vector2fFrom3f(box.gameObject.transform.position));
        JMath.rotate(r, -box.gameObject.transform.rotation.z, new Vector2f());
        Vector2f localCirclePos = new Vector2f(r).add(box.getHalfSize());

        Vector2f closestPointToCircle = new Vector2f(localCirclePos);
        if (closestPointToCircle.x < min.x) {
            closestPointToCircle.x = min.x;
        } else if (closestPointToCircle.x > max.x) {
            closestPointToCircle.x = max.x;
        }

        if (closestPointToCircle.y < min.y) {
            closestPointToCircle.y = min.y;
        } else if (closestPointToCircle.y > max.y) {
            closestPointToCircle.y = max.y;
        }

        Vector2f line = new Vector2f(localCirclePos).sub(closestPointToCircle);
        float lineLength = line.length();
        float desiredLength = circle.radius() - lineLength;
        line.div(lineLength);

        line.mul(desiredLength);
        JMath.rotate(line, box.gameObject.transform.rotation.z, new Vector2f());
        circle.gameObject.transform.position.add(JMath.vector3fFrom2f(line));
    }

    // =============================================================================
    // Box2D vs. primitive tests
    // =============================================================================
    private void box2DAndCircle(Box2D box, Circle circle) {
        circleAndBox2D(circle, box);
    }

    private void box2DAndBox2D(Box2D b1, Box2D b2) {
        // Generate all the axes to test from box one and box two
        Vector2f boxOneUp = new Vector2f(0, b1.getHalfSize().y);
        Vector2f boxOneRight = new Vector2f(b1.getHalfSize().x, 0);
        JMath.rotate(boxOneUp, b1.gameObject.transform.rotation.z, new Vector2f());
        JMath.rotate(boxOneRight, b1.gameObject.transform.rotation.z, new Vector2f());

        Vector2f boxTwoUp = new Vector2f(0, b2.getHalfSize().y);
        Vector2f boxTwoRight = new Vector2f(b2.getHalfSize().x, 0);
        JMath.rotate(boxTwoUp, b2.gameObject.transform.rotation.z, new Vector2f());
        JMath.rotate(boxTwoRight, b2.gameObject.transform.rotation.z, new Vector2f());

        // Test whether the boxes are intersecting on all axes (including global up and right axes)
        Vector2f[] axisToTest = {boxOneUp, boxOneRight, boxTwoUp, boxTwoRight};
        Vector2f toCenter = JMath.vector2fFrom3f(b2.gameObject.transform.position).sub(JMath.vector2fFrom3f(b1.gameObject.transform.position));
        int axis = -1;
        float overlap = Integer.MAX_VALUE;
        for (int i=0; i < axisToTest.length; i++) {
            // Intervals don't overlap, separating axis found
            float thisOverlap;
            if ((thisOverlap = overlapOnAxis(b1, b2, axisToTest[i], toCenter)) < 0) {
                return;
            } else {
                if (thisOverlap < overlap) {
                    overlap = thisOverlap;
                    axis = i;
                }
            }
        }

        if (axisToTest[axis].dot(toCenter) < 0) axisToTest[axis].mul(-1);
        b2.gameObject.transform.position.add(new Vector3f(new Vector2f(axisToTest[axis]).mul(overlap).x, axisToTest[axis].mul(overlap).y, 0));
    }


    // =============================================================================
    // Helper functions
    // =============================================================================
    private float overlapOnAxis(Box2D b1, Box2D b2, Vector2f axis, Vector2f toCenter) {
        Vector2f interval1 = getInterval(b1, axis);
        Vector2f interval2 = getInterval(b2, axis);

        float proj1 = (interval1.y - interval1.x) / 2.0f;
        float proj2 = (interval2.y -interval2.x) / 2.0f;

        float distance = Math.abs(toCenter.dot(axis));

        //return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
        return proj1 + proj2 - distance;
    }

    private Vector2f getInterval(Box2D box, Vector2f axis) {
        axis.normalize();

        // Get the interval of the box's min and max projected onto the axis provided and store
        // in result (result.x = min, result.y = max)
        Vector2f result = new Vector2f();
        Vector2f[] vertices = box.getVertices();

        // Project each vertex of the box onto the axis, and keep track of the smallest
        // and largest values
        result.x = result.y = axis.dot(vertices[0]);
        for (int i=1 ; i < 4; i++) {
            float projection = axis.dot(vertices[i]);
            if (projection < result.x) {
                result.x = projection;
            }
            if (projection > result.y) {
                result.y = projection;
            }
        }

        return result;
    }
}
