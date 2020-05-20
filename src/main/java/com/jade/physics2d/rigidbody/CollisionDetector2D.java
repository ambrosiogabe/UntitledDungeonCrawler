package com.jade.physics2d.rigidbody;

import com.jade.physics2d.primitives.Box2D;
import com.jade.physics2d.primitives.Circle;
import com.jade.physics2d.primitives.Collider2D;
import com.jade.physics2d.primitives.Raycast2D;
import com.jade.renderer.Line2D;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CollisionDetector2D {

    // =============================================================================
    // Generic primitive vs primitive tests
    // =============================================================================
    public static boolean colliderAndCollider(Collider2D coll1, Collider2D coll2) {
        if (coll1 instanceof Circle) {
            if (coll2 instanceof Circle) {
                return circleAndCircle((Circle)coll1, (Circle)coll2);
            } else if (coll2 instanceof Box2D) {
                return circleAndBox2D((Circle)coll1, (Box2D)coll2);
            } else if (coll2 instanceof Raycast2D) {
                return circleAndRay((Circle)coll1, (Raycast2D)coll2);
            }
        } else if (coll1 instanceof Box2D) {
            if (coll2 instanceof Box2D) {
                return box2DAndBox2D((Box2D)coll1, (Box2D)coll2);
            } else if (coll2 instanceof Circle) {
                return box2DAndCircle((Box2D)coll1, (Circle)coll2);
            } else if (coll2 instanceof Raycast2D) {
                return box2DAndRay((Box2D)coll1, (Raycast2D)coll2);
            }
        } else if (coll1 instanceof Raycast2D) {
            if (coll2 instanceof Box2D) {
                return rayAndBox2D((Raycast2D) coll1, (Box2D)coll2);
            } else if (coll2 instanceof Circle) {
                return rayAndCircle((Raycast2D) coll1, (Circle)coll2);
            } else if (coll2 instanceof Raycast2D) {
                return false;
            }
        }

        assert false : "Error: Could not resolve colliders '" + coll1.getClass() + "' and '" + coll2.getClass() + "'";
        return false;
    }

    // =============================================================================
    // Point vs. Primitive tests
    // =============================================================================
    public static boolean pointOnLine(Vector2f point, Line2D line) {
        // Find the slope
        float dy = (line.end().y - line.start().y);
        float dx = (line.end().x - line.start().x);
        float slope = dy / dx;

        // Find the y-intercept
        float yIntercept = line.start().y - (slope * line.start().x);

        // Check the line equation
        return JMath.compare(point.y, slope * point.x + yIntercept);
    }

    public static boolean pointInCircle(Vector2f point, Circle circle) {
        // Calculate line between point and center of circle
        Vector2f line = JMath.vector2fFrom3f(circle.gameObject.transform.position).sub(point);

        return line.lengthSquared() <= circle.radius() * circle.radius();
    }

    public static boolean pointInBox2D(Vector2f point, Box2D box) {
        // Translate point into local space, then try to clip the point to the rectangle
        Vector2f pointLocalSpace = new Vector2f(point);
        JMath.rotate(pointLocalSpace, box.getRotation(), box.getCenter());

        Vector3f min = new Vector3f(box.getCenter()).sub(JMath.vector3fFrom2f(box.getHalfSize()));
        Vector3f max = new Vector3f(box.getCenter()).add(JMath.vector3fFrom2f(box.getHalfSize()));

        return min.x <= pointLocalSpace.x && min.y <= pointLocalSpace.y &&
                pointLocalSpace.x <= max.x && pointLocalSpace.y <= max.y;
    }

    // =============================================================================
    // Line vs. Primitive tests
    // =============================================================================
    public static boolean lineAndCircle(Line2D line, Circle circle) {
        if (pointInCircle(line.start(), circle) || pointInCircle(line.end(), circle)) {
            return true;
        }

        Vector2f ab = new Vector2f(line.end()).sub(line.start());

        // Project point (circle position) onto ab (line segment), computing the parameterized
        // position d(t) = a + t * (b - a)
        Vector2f circleCenter = JMath.vector2fFrom3f(circle.gameObject.transform.position);
        float t = new Vector2f(circleCenter).sub(line.start()).dot(ab) / ab.dot(ab);

        // Clamp T to a 0-1 range. If t was < 0 or > 1
        // then the closest point was outside the segment!
        if (t < 0.0f || t > 1.0f) {
            return false;
        }

        // Find the closest point on the line segment
        Vector2f closestPoint = new Vector2f(line.start()).add(new Vector2f(ab).mul(t));

        Vector2f circleToClosest = new Vector2f(circleCenter).sub(closestPoint);
        return circleToClosest.lengthSquared() < circle.radius() * circle.radius();
    }

    public static boolean lineAndBox2D(Line2D line, Box2D box) {
        float theta = -box.getRotation();
        Vector2f localStart = new Vector2f(line.start());
        JMath.rotate(localStart, theta, box.getCenter());
        Vector2f localEnd = new Vector2f(line.end());
        JMath.rotate(localEnd, theta, box.getCenter());
        Line2D localLine = new Line2D(localStart, localEnd);

        if (pointInBox2D(localLine.start(), box) || pointInBox2D(localLine.end(), box)) {
            return true;
        }

        Vector2f normal = new Vector2f(localLine.end()).sub(localLine.start());
        normal.normalize();
        normal.x = (normal.x != 0) ? 1.0f / normal.x : 0f;
        normal.y = (normal.y != 0) ? 1.0f / normal.y : 0f;

        Vector2f min = box.getMin();
        min.sub(localLine.start()).mul(normal);
        Vector2f max = box.getMax();
        max.sub(localLine.start()).mul(normal);

        float tmin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tmax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if (tmax < 0 || tmin > tmax) {
            return false;
        }
        float t = (tmin < 0f) ? tmax : tmin;
        return t > 0f && t * t < localLine.lengthSquared();
    }

    // =============================================================================
    // Ray vs. Primitive tests
    // =============================================================================
    public static boolean rayAndCircle(Raycast2D ray, Circle circle) {
        return false;
    }

    public static boolean rayAndBox2D(Raycast2D ray, Box2D box) {
        if (box.gameObject == ray.ignore()) {
            return false;
        }

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        Vector2f origin = ray.origin();
        Vector2f dir = ray.direction();
        float maxDistance = ray.maxDistance();

        float tMinX = (min.x - origin.x) / (JMath.compare(dir.x, 0) ? 0.00001f : dir.x);
        float tMaxX = (max.x - origin.x) / (JMath.compare(dir.x, 0) ? 0.00001f : dir.x);
        float tMinY = (min.y - origin.y) / (JMath.compare(dir.y, 0) ? 0.00001f : dir.y);
        float tMaxY = (max.y - origin.y) / (JMath.compare(dir.y, 0) ? 0.00001f : dir.y);

        float tMin = Math.max(Math.min(tMinX, tMaxX), Math.min(tMinY, tMaxY));
        float tMax = Math.min(Math.max(tMinX, tMaxX), Math.max(tMinY, tMaxY));

        if (tMax < 0) {
            return false;
        } else if (tMin > tMax) {
            return false;
        } else if (tMin > maxDistance) {
            return false;
        }

        if (box.gameObject != null) {
            DebugDraw.addLine2D(ray.origin(), new Vector2f(ray.origin()).add(new Vector2f(ray.direction()).mul(tMin)));
        }

        return true;
    }

    // =============================================================================
    // Circle vs. Primitive tests
    // =============================================================================
    public static boolean circleAndLine(Circle circle, Line2D line) {
        return lineAndCircle(line, circle);
    }

    public static boolean circleAndRay(Circle circle, Raycast2D ray) {
        return rayAndCircle(ray, circle);
    }

    public static boolean circleAndCircle(Circle c1, Circle c2) {
        Vector2f lineBetweenCenters = JMath.vector2fFrom3f(c1.gameObject.transform.position).sub(JMath.vector2fFrom3f(c2.gameObject.transform.position));
        float radiiSum = c1.radius() + c2.radius();
        return lineBetweenCenters.lengthSquared() <= radiiSum * radiiSum;
    }

    public static boolean circleAndBox2D(Circle circle, Box2D box) {
        // Treat the box as if it was located at halfSize.x, halfSize.y
        Vector2f min = new Vector2f();
        Vector2f max = box.getHalfSize().mul(2);

        // Create a circle in box's local space
        Vector2f r = JMath.vector2fFrom3f(circle.gameObject.transform.position).sub(JMath.vector2fFrom3f(box.getCenter()));
        JMath.rotate(r, -box.getRotation(), new Vector2f());
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
        return line.lengthSquared() <= circle.radius() * circle.radius();
    }

    // =============================================================================
    // Box2D vs. primitive tests
    // =============================================================================
    public static boolean box2DAndCircle(Box2D box, Circle circle) {
        return circleAndBox2D(circle, box);
    }

    public static boolean box2DAndRay(Box2D box, Raycast2D ray) {
        return rayAndBox2D(ray, box);
    }

    public static boolean box2DAndLine(Box2D box, Line2D line) {
        return lineAndBox2D(line, box);
    }

    public static boolean box2DAndBox2D(Box2D b1, Box2D b2) {
        // Generate all the axes to test from box one and box two
        Vector2f boxOneUp = new Vector2f(0, b1.getHalfSize().y);
        Vector2f boxOneRight = new Vector2f(b1.getHalfSize().x, 0);
        JMath.rotate(boxOneUp, b1.getRotation(), new Vector2f());
        JMath.rotate(boxOneRight, b1.getRotation(), new Vector2f());

        Vector2f boxTwoUp = new Vector2f(0, b2.getHalfSize().y);
        Vector2f boxTwoRight = new Vector2f(b2.getHalfSize().x, 0);
        JMath.rotate(boxTwoUp, b2.getRotation(), new Vector2f());
        JMath.rotate(boxTwoRight, b2.getRotation(), new Vector2f());

        // Test whether the boxes are intersecting on all axes (including global up and right axes)
        Vector2f[] axisToTest = {boxOneUp, boxOneRight, boxTwoUp, boxTwoRight};
        Vector2f toCenter = JMath.vector2fFrom3f(b2.getCenter()).sub(JMath.vector2fFrom3f(b1.getCenter()));
        int axis = -1;
        float overlap = Integer.MAX_VALUE;
        for (int i=0; i < axisToTest.length; i++) {
            // Intervals don't overlap, separating axis found
            float thisOverlap;
            if ((thisOverlap = overlapOnAxis(b1, b2, axisToTest[i], toCenter)) < 0) {
                return false;
            } else {
                if (thisOverlap < overlap) {
                    overlap = thisOverlap;
                    axis = i;
                }
            }
        }

        //DebugDraw.addLine2D(new Vector2f(200, 200), new Vector2f(200, 200).add(new Vector2f(axisToTest[axis]).mul(overlap)), 1f);
        //b2.gameObject.transform.position.add(new Vector3f(axisToTest[axis].mul(overlap).x, axisToTest[axis].mul(overlap).y, 0));

        // All intervals overlapped, no separating axis found
        return true;
    }


    // =============================================================================
    // Helper functions
    // =============================================================================
    private static float overlapOnAxis(Box2D b1, Box2D b2, Vector2f axis, Vector2f toCenter) {
        Vector2f interval1 = getInterval(b1, axis);
        Vector2f interval2 = getInterval(b2, axis);

        float proj1 = (interval1.y - interval1.x) / 2.0f;
        float proj2 = (interval2.y -interval2.x) / 2.0f;

        float distance = Math.abs(toCenter.dot(axis));

        //return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
        return proj1 + proj2 - distance;
    }

    private static Vector2f getInterval(Box2D box, Vector2f axis) {
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
