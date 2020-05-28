package com.jade.physics.rigidbody;

import com.jade.physics.primitives.*;
import com.jade.renderer.Line;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider a, Collider b) {
        if (a instanceof Sphere) {
            if (b instanceof Sphere) {
                return findCollisionFeatures((Sphere)a, (Sphere)b);
            } else if (b instanceof  Box) {
                return findCollisionFeatures((Box)b, (Sphere)a);
            } else if (b instanceof Plane) {
                return findCollisionFeatures((Sphere)a, (Plane)b);
            }
        } else if (a instanceof Box) {
            if (b instanceof Sphere) {
                return findCollisionFeatures((Box)a, (Sphere)b);
            } else if (b instanceof Box) {
                return findCollisionFeatures((Box)a, (Box)b);
            } else if (b instanceof Plane) {
                return findCollisionFeatures((Box)a, (Plane)b);
            }
        } else if (a instanceof Plane) {
            if (b instanceof Sphere) {
                return findCollisionFeatures((Sphere)b, (Plane)a);
            } else if (b instanceof Box) {
                return findCollisionFeatures((Box)b, (Plane)a);
            }
        }

        assert false : "Uh oh. Undefined collision behavior. Between '" + a.getClass().getSimpleName() + "' and '" + b.getClass().getSimpleName() + "'";
        return null;
    }

    // ======================================================================================
    // Sphere vs primitive
    // ======================================================================================
    public static CollisionManifold findCollisionFeatures(Sphere a, Sphere b) {
        // TODO: WRITE TESTS FOR THIS
        CollisionManifold result = new CollisionManifold();
        result.reset();
        float sumRadii = a.radius() + b.radius();
        Vector3f distance = new Vector3f(b.gameObject.transform.position).sub(a.gameObject.transform.position);
        if (distance.lengthSquared() - sumRadii * sumRadii > 0 || distance.lengthSquared() == 0f) {
            return result;
        }

        float depth = Math.abs(distance.length() - sumRadii) * 0.5f;
        Vector3f normal = new Vector3f(distance);
        normal.normalize();
        // dtp - Distance To intersection Point
        float dtp = a.radius() - depth;
        Vector3f contact = new Vector3f(a.gameObject.transform.position).add(new Vector3f(normal).mul(dtp));

        result.init(true, normal, depth);
        result.addContactPoint(contact);
        return result;
    }

    public static CollisionManifold findCollisionFeatures(Sphere sphere, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        CollisionManifold result = new CollisionManifold();
        result.reset();

        Vector3f closestPoint = IntersectionTester.closestPoint(sphere.gameObject.transform.position, plane);
        float distanceSquared = new Vector3f(closestPoint).sub(sphere.gameObject.transform.position).lengthSquared();
        if (distanceSquared > sphere.radius() * sphere.radius()) {
            return result;
        }

        Vector3f normal = new Vector3f(plane.normal());
        Vector3f outsidePoint = new Vector3f(sphere.gameObject.transform.position).sub(new Vector3f(normal).mul(sphere.radius()));
        float distance = new Vector3f(closestPoint).sub(outsidePoint).length();
        Vector3f contactPoint = new Vector3f(closestPoint).add(new Vector3f(outsidePoint).sub(closestPoint).mul(0.5f));

        result.init(true, normal, distance * 0.5f);
        result.addContactPoint(contactPoint);

        return result;
    }

    public static CollisionManifold findCollisionFeatures(Box box, Sphere sphere) {
        // TODO: WRITE TESTS FOR THIS
        CollisionManifold result = new CollisionManifold();
        result.reset();

        // Closest point to the sphere that is on the box
        Vector3f closestPoint = IntersectionTester.closestPoint(sphere.gameObject.transform.position, box);
        float distanceSquare = new Vector3f(closestPoint).sub(sphere.gameObject.transform.position).lengthSquared();
        if (distanceSquare > sphere.radius() * sphere.radius()) {
            return result;
        }

        Vector3f normal;
        if (JMath.compare(distanceSquare, 0f)) {
            // If the closest point to the sphere is the center of the sphere, we try to find a better point
            float mSq = new Vector3f(closestPoint).sub(box.gameObject.transform.position).lengthSquared();
            if (JMath.compare(mSq, 0f)) {
                // I guess we give up at this point... :(
                return result;
            }
            // Closest point is at the center of the sphere
            normal = new Vector3f(closestPoint).sub(box.gameObject.transform.position);
            normal.normalize();
        } else {
            normal = new Vector3f(sphere.gameObject.transform.position).sub(closestPoint);
            normal.normalize();
        }

        Vector3f outsidePoint = new Vector3f(sphere.gameObject.transform.position).sub(new Vector3f(normal).mul(sphere.radius()));
        float distance = new Vector3f(closestPoint).sub(outsidePoint).length();
        Vector3f contactPoint = new Vector3f(closestPoint).add(new Vector3f(outsidePoint).sub(closestPoint).mul(0.5f));

        result.init(true, normal, distance * 0.5f);
        result.addContactPoint(contactPoint);

        return result;
    }



    // ======================================================================================
    // Box vs primitive
    // ======================================================================================
    public static CollisionManifold findCollisionFeatures(Box a, Box b) {
        // TODO: WRITE TESTS FOR THIS
        CollisionManifold result = new CollisionManifold();
        result.reset();

        Vector3f[] axes = {
            a.getAxis(0), a.getAxis(1), a.getAxis(2),
            b.getAxis(0), b.getAxis(1), b.getAxis(2),

            a.getAxis(0).cross(b.getAxis(0)),
            a.getAxis(0).cross(b.getAxis(1)),
            a.getAxis(0).cross(b.getAxis(2)),
            a.getAxis(1).cross(b.getAxis(0)),
            a.getAxis(1).cross(b.getAxis(1)),
            a.getAxis(1).cross(b.getAxis(2)),
            a.getAxis(2).cross(b.getAxis(0)),
            a.getAxis(2).cross(b.getAxis(1)),
            a.getAxis(2).cross(b.getAxis(2)),
        };

        Vector3f hitNormal = new Vector3f();
        Vector2f shouldFlip = new Vector2f();
        result.setDepth(Float.MAX_VALUE);
        for (int i=0; i < axes.length; i++) {
            if (axes[i].lengthSquared() < 0.001f) {
                continue;
            }

            float depth = penetrationDepth(a, b, axes[i], shouldFlip);
            if (depth <= 0f) {
                return result;
            } else if (depth < result.depth()) {
                if (shouldFlip.x == 1) {
                    axes[i].mul(-1f);
                }
                result.setDepth(depth);
                hitNormal.set(axes[i]);
            }
        }

        if (result.depth() == 0) {
            return result;
        }

        Vector3f axis = hitNormal;
        List<Vector3f> c1 = clipEdgesToBox(getEdges(b), a);
        List<Vector3f> c2 = clipEdgesToBox(getEdges(a), b);
        for (int i=0; i < c1.size(); i++) {
            result.addContactPoint(c1.get(i));
        }
        for (int i=0; i < c2.size(); i++) {
            result.addContactPoint(c2.get(i));
        }

        Vector2f interval = IntersectionTester.getInterval(a, axis);
        float distance = (interval.y - interval.x) * 0.5f - result.depth() * 0.5f;
        Vector3f pointOnPlane = new Vector3f(a.gameObject.transform.position).add(new Vector3f(axis).mul(distance));

        for (int i=result.contacts().size() - 1; i >= 0; i--) {
            Vector3f contact = result.contacts().get(i);
            contact.add(new Vector3f(axis).mul(axis.dot(new Vector3f(pointOnPlane).sub(contact))));

            for (int j=result.contacts().size() - 1; j > i; j--) {
                if (new Vector3f(result.contacts().get(j)).sub(result.contacts().get(i)).lengthSquared() < 0.0001f) {
                    result.contacts().remove(j);
                    break;
                }
            }
        }

        result.init(true, axis);

        return result;
    }

    public static CollisionManifold findCollisionFeatures(Box a, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        CollisionManifold result = new CollisionManifold();
        result.reset();

        return result;
    }



    // ======================================================================================
    // Helper functions
    // ======================================================================================
    private static List<Line> getEdges(Box box) {
        List<Line> result = new ArrayList<>();
        Vector3f[] vertices = box.getVertices();
        int[][] index = {
                // Indices for each edge in the box
                {0, 1}, {0, 2}, {2, 3}, {1, 3}, {0, 4}, {2, 6},
                {4, 5}, {4, 6}, {6, 7}, {5, 7}, {1, 5}, {3, 7}
        };

        for (int j=0; j < index.length; j++) {
            result.add(new Line(
                    new Vector3f(vertices[index[j][0]]),
                    new Vector3f(vertices[index[j][1]])
            ));
        }

        return result;
    }

    private static List<Plane> getPlanes(Box box) {
        Vector3f center = box.gameObject.transform.position;
        Vector3f extents = box.getHalfSize();
        Vector3f[] axes = {
                box.getAxis(0),
                box.getAxis(1),
                box.getAxis(2)
        };

        List<Plane> result = new ArrayList<>();
        result.add(new Plane(
                new Vector3f(axes[0]),
                axes[0].dot(new Vector3f(center).add(new Vector3f(axes[0]).mul(extents.x)))
        ));
        result.add(new Plane(
                new Vector3f(axes[0]).mul(-1f),
                -axes[0].dot(new Vector3f(center).sub(new Vector3f(axes[0]).mul(extents.x)))
        ));
        result.add(new Plane(
                new Vector3f(axes[1]),
                axes[1].dot(new Vector3f(center).add(new Vector3f(axes[1]).mul(extents.y)))
        ));
        result.add(new Plane(
                new Vector3f(axes[1]).mul(-1f),
                -axes[1].dot(new Vector3f(center).sub(new Vector3f(axes[1]).mul(extents.y)))
        ));
        result.add(new Plane(
                new Vector3f(axes[2]),
                axes[2].dot(new Vector3f(center).add(new Vector3f(axes[2]).mul(extents.z)))
        ));
        result.add(new Plane(
                new Vector3f(axes[2]).mul(-1f),
                -axes[2].dot(new Vector3f(center).sub(new Vector3f(axes[2]).mul(extents.z)))
        ));

        return result;
    }

    private static boolean clipToPlane(Plane plane, Line line, Vector3f outPoint) {
        Vector3f ab = new Vector3f(line.end()).sub(line.start());
        float nAB = plane.normal().dot(ab);
        if (JMath.compare(nAB, 0)) {
            return false;
        }

        float nA = plane.normal().dot(line.start());
        float t = (plane.distanceFromOrigin() - nA) / nAB;
        if (t >= 0f && t <= 1f) {
            if (outPoint != null) {
                outPoint.set(new Vector3f(line.start()).add(new Vector3f(ab).mul(t)));
            }
            return true;
        }
        return false;
    }

    private static List<Vector3f> clipEdgesToBox(List<Line> edges, Box box) {
        List<Vector3f> result = new ArrayList<>();
        Vector3f intersection = new Vector3f();
        List<Plane> planes = getPlanes(box);

        for (int i=0; i < planes.size(); i++) {
            for (int j=0; j < edges.size(); j++) {
                if (clipToPlane(planes.get(i), edges.get(j), intersection)) {
                    if (IntersectionTester.pointInBox(intersection, box)) {
                        result.add(new Vector3f(intersection));
                    }
                }
            }
        }
        assert result.size() <= edges.size() : "We should not get any more points then we have edges.";

        return result;
    }

    private static float penetrationDepth(Box b1, Box b2, Vector3f axis, Vector2f outShouldFlip) {
        axis.normalize();
        Vector2f interval1 = IntersectionTester.getInterval(b1, axis);
        Vector2f interval2 = IntersectionTester.getInterval(b2, axis);

        if (!((interval2.x <= interval1.y) && (interval1.x <= interval2.y))) {
            return 0f; // No penetration
        }

        float len1 = interval1.y - interval1.x;
        float len2 = interval2.y - interval2.x;

        float min = Math.min(interval1.x, interval2.x);
        float max = Math.max(interval2.y, interval1.y);
        float length = max - min;

        if (outShouldFlip != null) {
            outShouldFlip.x = (interval2.x < interval1.x) ? 1f : 0f;
        }

        return (len1 + len2) - length;
    }
}
