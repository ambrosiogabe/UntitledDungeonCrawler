package com.jade.physics.primitives;

import com.jade.renderer.Line;
import com.jade.util.JMath;
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
        float dot = point.dot(plane.normal());
        return JMath.compare(dot - plane.distanceFromOrigin(), 0.0f);
    }

    public static boolean pointOnLine(Vector3f point, Line line) {
        Vector3f closestPoint = closestPoint(point, line);
        float distanceSq = closestPoint.sub(point).lengthSquared();
        return JMath.compare(distanceSq, 0.0f);
    }

    public static boolean pointOnRay(Vector3f point, Ray ray) {
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
        Vector3f result = box.gameObject.transform.position;
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
        float dot = plane.normal().dot(point);
        float distance = dot - plane.distanceFromOrigin();
        return point.sub(new Vector3f(plane.normal()).mul(distance));
    }

    public static Vector3f closestPoint(Vector3f point, Line line) {
        Vector3f lineVec = new Vector3f(line.end()).sub(line.start());
        float t = new Vector3f(point).sub(line.start()).dot(lineVec) / lineVec.dot(lineVec);
        t = Math.max(t, 0.0f);
        t = Math.min(t, 1.0f);

        return new Vector3f(line.start()).add(lineVec.mul(t));
    }

    public static Vector3f closestPoint(Vector3f point, Ray ray) {
        float t = new Vector3f(point).sub(ray.origin()).dot(ray.normalDirection());
        t = Math.max(t, 0.0f);

        return new Vector3f(ray.origin()).add(new Vector3f(ray.normalDirection()).mul(t));
    }
}
