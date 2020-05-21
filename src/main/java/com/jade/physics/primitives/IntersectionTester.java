package com.jade.physics.primitives;

import com.jade.physics.rigidbody.colliders.BoxCollider;
import com.jade.renderer.Line;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class IntersectionTester {
    // ====================================================================
    // Point in primitive
    // ====================================================================
    public static boolean pointInSphere(Vector3f point, Sphere sphere) {
        float lengthSq = new Vector3f(point).sub(sphere.gameObject.transform.position).lengthSquared();
        float radiusSq = sphere.radius() * sphere.radius();

        return lengthSq < radiusSq;
    }

    public static boolean pointInBox(Vector3f point, Box box) {
        Vector3f relPoint = new Vector3f(point).sub(box.gameObject.transform.position);
        for (int i=0; i < 3; i++) {
            Vector3f axis = box.getAxis(i);
            float distance = relPoint.dot(axis);

            if (distance > box.getHalfSize(i)) {
                return false;
            }
            if (distance < -box.getHalfSize(i)) {
                return false;
            }
        }

        return true;
    }

    public static boolean pointOnPlane(Vector3f point, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        float dot = point.dot(plane.normal());
        return JMath.compare(dot - plane.distanceFromOrigin(), 0.0f);
    }

    public static boolean pointOnLine(Vector3f point, Line line) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f closestPoint = closestPoint(point, line);
        float distanceSq = closestPoint.sub(point).lengthSquared();
        return JMath.compare(distanceSq, 0.0f);
    }

    public static boolean pointOnRay(Vector3f point, Ray ray) {
        // TODO: WRITE TESTS FOR THIS
        if (point.equals(ray.origin())) {
            return true;
        }

        Vector3f normal = new Vector3f(point).sub(ray.origin());
        normal.normalize();
        float diff = normal.dot(ray.normalDirection());
        return JMath.compare(diff, 1.0f);
    }

    // ====================================================================
    // Closest point located on primitive in world coordinates
    // ====================================================================
    public static Vector3f closestPoint(Vector3f point, Sphere sphere) {
        Vector3f sphereToPoint = new Vector3f(point).sub(sphere.gameObject.transform.position);
        sphereToPoint.normalize();
        sphereToPoint.mul(sphere.radius());

        return sphereToPoint.add(sphere.gameObject.transform.position);
    }

    public static Vector3f closestPoint(Vector3f point, Box box) {
        Vector3f result = new Vector3f(box.gameObject.transform.position);
        Vector3f relPoint = new Vector3f(point).sub(box.gameObject.transform.position);

        for (int i=0; i < 3; i++) {
            Vector3f axis = box.getAxis(i);
            float distance = relPoint.dot(axis);

            if (distance > box.getHalfSize(i)) {
                distance = box.getHalfSize(i);
            }
            if (distance < -box.getHalfSize(i)) {
                distance = -box.getHalfSize(i);
            }

            result.add(axis.mul(distance));
        }

        return result;
    }

    public static Vector3f closestPoint(Vector3f point, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        float dot = plane.normal().dot(point);
        float distance = dot - plane.distanceFromOrigin();
        return point.sub(new Vector3f(plane.normal()).mul(distance));
    }

    public static Vector3f closestPoint(Vector3f point, Line line) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f lineVec = new Vector3f(line.end()).sub(line.start());
        float t = new Vector3f(point).sub(line.start()).dot(lineVec) / lineVec.dot(lineVec);
        t = Math.max(t, 0.0f);
        t = Math.min(t, 1.0f);

        return new Vector3f(line.start()).add(lineVec.mul(t));
    }

    public static Vector3f closestPoint(Vector3f point, Ray ray) {
        // TODO: WRITE TESTS FOR THIS
        float t = new Vector3f(point).sub(ray.origin()).dot(ray.normalDirection());
        t = Math.max(t, 0.0f);

        return new Vector3f(ray.origin()).add(new Vector3f(ray.normalDirection()).mul(t));
    }

    // ====================================================================
    // Sphere vs Primitive tests
    // ====================================================================
    public static boolean sphereAndSphere(Sphere s1, Sphere s2) {
        // TODO: WRITE TESTS FOR THIS
        float radiiSum = s1.radius() + s2.radius();
        float squareDistance = new Vector3f(s1.gameObject.transform.position).sub(s2.gameObject.transform.position).lengthSquared();

        return squareDistance < radiiSum * radiiSum;
    }

    public static boolean sphereAndBox(Sphere sphere, Box box) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f closestPoint = closestPoint(sphere.gameObject.transform.position, box);
        float distanceSquared = new Vector3f(sphere.gameObject.transform.position).sub(closestPoint).lengthSquared();
        return distanceSquared < sphere.radius() * sphere.radius();
    }

    public static boolean sphereAndPlane(Sphere sphere, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f closestPoint = closestPoint(sphere.gameObject.transform.position, plane);
        float distanceSquared = new Vector3f(sphere.gameObject.transform.position).sub(closestPoint).lengthSquared();
        return distanceSquared < sphere.radius() * sphere.radius();
    }

    // ====================================================================
    // Box vs primitive tests
    // ====================================================================
    public static boolean boxAndSphere(Box box, Sphere sphere) {
        return sphereAndBox(sphere, box);
    }

    public static boolean boxAndBox(Box b1, Box b2) {
        // TODO: WRITE TESTS FOR THIS
        // Find the vector between the two centers
        Vector3f toCenter = new Vector3f(b2.gameObject.transform.position).sub(b1.gameObject.transform.position);

        return (
                // Check on box one's axes first
                overlapOnAxis(b1, b2, b1.getAxis(0), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(1), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(2), toCenter) &&

                // And on two's
                overlapOnAxis(b1, b2, b2.getAxis(0), toCenter) &&
                overlapOnAxis(b1, b2, b2.getAxis(1), toCenter) &&
                overlapOnAxis(b1, b2, b2.getAxis(2), toCenter) &&

                // Now on the cross products
                overlapOnAxis(b1, b2, b1.getAxis(0).cross(b2.getAxis(0)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(0).cross(b2.getAxis(1)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(0).cross(b2.getAxis(2)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(1).cross(b2.getAxis(0)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(1).cross(b2.getAxis(1)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(1).cross(b2.getAxis(2)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(2).cross(b2.getAxis(0)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(2).cross(b2.getAxis(1)), toCenter) &&
                overlapOnAxis(b1, b2, b1.getAxis(2).cross(b2.getAxis(2)), toCenter)
        );
    }

    public static boolean boxAndPlane(Box box, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        float projectedRadius = box.getHalfSize(0) * Math.abs(plane.normal().dot(box.getAxis(0))) +
                box.getHalfSize(1) * Math.abs(plane.normal().dot(box.getAxis(1))) +
                box.getHalfSize(2) * Math.abs(plane.normal().dot(box.getAxis(2)));

        float boxDistance = plane.normal().dot(new Vector3f(box.gameObject.transform.position).sub(plane.gameObject.transform.position)) - projectedRadius;

        return boxDistance <= plane.distanceFromOrigin();
    }




    // ====================================================================
    // Helper functions
    // ====================================================================
    private static boolean overlapOnAxis(Box one, Box two, Vector3f axis, Vector3f toCenter) {
        // Project the half-size of one onto axis
        float oneProject = transformToAxis(one, axis);
        float twoProject = transformToAxis(two, axis);

        // Project this onto the axis
        float distance = Math.abs(toCenter.dot(axis));

        // Check for overlap
        return (distance < oneProject + twoProject);
    }

    private static float transformToAxis(Box box, Vector3f axis) {
        return  box.getHalfSize(0) * Math.abs(axis.dot(box.getAxis(0))) +
                box.getHalfSize(1) * Math.abs(axis.dot(box.getAxis(1))) +
                box.getHalfSize(2) * Math.abs(axis.dot(box.getAxis(2)));
    }
}
