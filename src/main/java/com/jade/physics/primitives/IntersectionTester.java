package com.jade.physics.primitives;

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

    public static boolean pointInTriangle(Vector3f point, Triangle triangle) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f a = new Vector3f(triangle.a()).sub(point);
        Vector3f b = new Vector3f(triangle.b()).sub(point);
        Vector3f c = new Vector3f(triangle.c()).sub(point);

        Vector3f normPBC = b.cross(c);
        Vector3f normPCA = c.cross(a);
        Vector3f normPAB = a.cross(b);

        // If the faces of the pyramid do not have the same normal, the point is not contained
        if (normPBC.dot(normPCA) < 0f) {
            return false;
        } else if (normPBC.dot(normPAB) < 0f) {
            return false;
        }

        // If all the faces of the pyramid have the same normal, the pyrmaid is flat,
        // this means the point is contained
        return true;
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

    public static Vector3f closestPoint(Vector3f point, Triangle triangle) {
        // TODO: WRITE TESTS FOR THIS
        Plane plane = fromTriangle(triangle);
        Vector3f closest = closestPoint(point, plane);
        if (pointInTriangle(closest, triangle)) {
            return closest;
        }

        Vector3f c1 = closestPoint(point, new Line(new Vector3f(triangle.a()), new Vector3f(triangle.b())));
        Vector3f c2 = closestPoint(point, new Line(new Vector3f(triangle.b()), new Vector3f(triangle.c())));
        Vector3f c3 = closestPoint(point, new Line(new Vector3f(triangle.c()), new Vector3f(triangle.a())));

        float lengthSq1 = new Vector3f(point).sub(c1).lengthSquared();
        float lengthSq2 = new Vector3f(point).sub(c2).lengthSquared();
        float lengthSq3 = new Vector3f(point).sub(c3).lengthSquared();

        if (lengthSq1 < lengthSq2 && lengthSq1 < lengthSq3) {
            return c1;
        } else if (lengthSq2 < lengthSq1 && lengthSq2 < lengthSq3) {
            return c2;
        }

        return c3;
    }


    // ====================================================================
    // Line vs. primitive tests
    // ====================================================================
    public static boolean lineTest(Collider collider, Line line) {
        if (collider instanceof Sphere) {
            return lineTest((Sphere)collider, line);
        } else if (collider instanceof Box) {
            return lineTest((Box)collider, line);
        } else if (collider instanceof Plane) {
            return lineTest((Plane)collider, line);
        } else if (collider instanceof Triangle) {
            return lineTest((Triangle)collider, line);
        }

        assert false : "Uh oh. Should not have reached here";
        return false;
    }

    public static boolean lineTest(Sphere sphere, Line line) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f closest = closestPoint(sphere.gameObject.transform.position, line);
        float distSquared = new Vector3f(sphere.gameObject.transform.position).sub(closest).lengthSquared();
        return distSquared <= sphere.radius() * sphere.radius();
    }

    public static boolean lineTest(Box box, Line line) {
        // TODO: WRITE TESTS FOR THIS
        Ray ray = new Ray(new Vector3f(line.start()), new Vector3f(line.end()).sub(line.start()));
        RaycastResult res = new RaycastResult();
        raycast(box, ray, res);
        float t = res.t();

        return t >= 0 && t * t <= line.lengthSquared();
    }

    public static boolean lineTest(Plane plane, Line line) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f ab = new Vector3f(line.end()).sub(line.start());

        float nA = plane.normal().dot(line.start());
        float nAB = plane.normal().dot(ab);

        // If the line and plane are parallel, nAB will be 0
        if (JMath.compare(nAB, 0)) {
            return false;
        }

        float t = (plane.distanceFromOrigin() - nA) / nAB;
        return t >= 0.0f && t <= 1.0f;
    }

    public static boolean lineTest(Triangle triangle, Line line) {
        // TODO: WRITE TESTS FOR THIS
        Ray ray = new Ray(new Vector3f(line.start()), new Vector3f(line.end()).sub(line.start()));
        RaycastResult result = new RaycastResult();
        raycast(triangle, ray, result);
        float t = result.t();
        return t >= 0f && t * t <= line.lengthSquared();
    }



    // ====================================================================
    // Raycast vs. primitive tests
    // ====================================================================
    public static boolean raycast(Collider collider, Ray ray, RaycastResult result) {
        if (collider instanceof Sphere) {
            return raycast((Sphere)collider, ray, result);
        } else if (collider instanceof Box) {
            return raycast((Box)collider, ray, result);
        } else if (collider instanceof Plane) {
            return raycast((Plane)collider, ray, result);
        } else if (collider instanceof Triangle) {
            return raycast((Triangle)collider, ray, result);
        }

        assert false : "Uh oh. Should not have reached here";
        return false;
    }

    public static boolean raycast(Sphere sphere, Ray ray, RaycastResult result) {
        // TODO: WRITE TESTS FOR THIS
        RaycastResult.reset(result);

        Vector3f originToSphere = new Vector3f(sphere.gameObject.transform.position).sub(ray.origin());
        float radiusSquared = sphere.radius() * sphere.radius();
        float originToSphereLengthSquared = originToSphere.lengthSquared();

        // Project the vector from ray origin onto the direction of the ray
        float a = originToSphere.dot(ray.normalDirection());
        float bSq = originToSphereLengthSquared - (a * a);
        if (radiusSquared - bSq < 0.0f) {
            return false; // -1 indicates it did not intersect
        }

        float f = (float)Math.sqrt(radiusSquared - bSq);
        float t = 0;
        if (originToSphereLengthSquared < radiusSquared) {
            // Ray starts inside the sphere
            t = a + f; // Just reverse direction
        }

        t = a - f; // Otherwise it's a normal intersection

        if (result != null) {
            Vector3f point = new Vector3f(ray.origin()).add(new Vector3f(ray.normalDirection()).mul(t));
            Vector3f normal = new Vector3f(point).sub(sphere.gameObject.transform.position);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    public static boolean raycast(Box box, Ray ray, RaycastResult result) {
        // TODO: WRITE TESTS FOR THIS
        RaycastResult.reset(result);

        Vector3f origin = ray.origin();
        Vector3f direction = ray.normalDirection();

        Vector3f size = box.getSize();
        Vector3f xAxis = box.getAxis(0);
        Vector3f yAxis = box.getAxis(1);
        Vector3f zAxis = box.getAxis(2);

        // Get the vector from the origin to the center of the Box
        Vector3f p = new Vector3f(box.gameObject.transform.position).sub(origin);
        // Project the direction of the ray onto each axis of the box
        Vector3f f = new Vector3f(xAxis.dot(direction), yAxis.dot(direction), zAxis.dot(direction));
        // Next, project p onto every axis of the box
        Vector3f e = new Vector3f(xAxis.dot(p), yAxis.dot(p), zAxis.dot(p));

        float[] t = { 0, 0, 0, 0, 0, 0 };
        for (int i=0; i < 3; i++) {
            if (JMath.compare(f.get(i), 0)) {
                // If the ray is parallel to the current slab, and the origin of the ray is not inside the slab, we
                // have no hit
                if (-e.get(i) - size.get(i) > 0 || -e.get(i) + size.get(i) < 0) {
                    return false;
                }
                f.setComponent(i, 0.00001f); // Set it to small value to avoid division by zero
            }
            t[i * 2 + 0] = (e.get(i) + size.get(i)) / f.get(i); // tmin for this axis
            t[i * 2 + 1] = (e.get(i) - size.get(i)) / f.get(i); // tmax this axis
        }

        float tmin = Math.max(
                Math.max(Math.min(t[0], t[1]), Math.min(t[2], t[3])),
                Math.min(t[4], t[5])
        );

        float tmax = Math.min(
                Math.min(Math.max(t[0], t[1]), Math.max(t[2], t[3])),
                Math.max(t[4], t[5])
        );

        if (tmax < 0) {
            // Box is behind the ray
            return false;
        }

        if (tmin > tmax) {
            // Ray does not intersect box
            return false;
        }

        float tResult = tmin;
        if (tmin < 0.0f) {
            // Ray's origin is inside the box
            tResult = tmax;
        }

        if (result != null) {
            Vector3f point = new Vector3f(ray.origin()).add(new Vector3f(ray.normalDirection()).mul(tResult));
            Vector3f[] normals = {
                    xAxis, new Vector3f(xAxis).mul(-1),
                    yAxis, new Vector3f(yAxis).mul(-1),
                    zAxis, new Vector3f(zAxis).mul(-1)
            };
            Vector3f normal = normals[0];

            for (int i=0; i < normals.length; i++) {
                if (JMath.compare(tResult, t[i])) {
                    normal = normals[i];
                    break;
                }
            }

            result.init(point, normal, tResult, true);
        }
        return true;
    }

    public static boolean raycast(Plane plane, Ray ray, RaycastResult result) {
        // TODO: WRITE TESTS FOR THIS
        RaycastResult.reset(result);
        float nd = ray.normalDirection().dot(plane.normal());
        float pn = ray.origin().dot(plane.normal());

        if (nd >= 0f) {
            return false;
        }

        float t = (plane.distanceFromOrigin() - pn) / nd;
        if (t >= 0f) {
            if (result != null) {
                Vector3f point = new Vector3f(ray.origin()).add(new Vector3f(ray.normalDirection()).mul(t));
                Vector3f normal = new Vector3f(plane.normal());
                result.init(point, normal, t, true);
            }
            return true;
        }

        return false;
    }

    public static boolean raycast(Triangle triangle, Ray ray, RaycastResult result) {
        // TODO: WRITE SUPER TESTS FOR THIS!!!
        // TODO: ALSO MAKE SURE TO TEST AND FIGURE OUT HOW BARYCENTRIC WORKS!!
        RaycastResult.reset(result);
        Plane plane = fromTriangle(triangle);
        if (!raycast(plane, ray, result)) {
            return false;
        }

        float t = result.t();
        Vector3f resultingPoint = new Vector3f(ray.origin()).add(new Vector3f(ray.normalDirection()).mul(t));
        Vector3f barycentric = barycentric(resultingPoint, triangle);
        if (barycentric.x >= 0f && barycentric.x <= 1f &&
            barycentric.y >= 0f && barycentric.y <= 1f &&
            barycentric.z >= 0f && barycentric.z <= 1f) {
            if (result != null) {
                result.init(resultingPoint, result.normal(), t, true);
            }
            return true;
        }

        return false;
    }



    // ====================================================================
    // Triangle vs. primitive tests
    // ====================================================================
    public static boolean triangleAndSphere(Triangle triangle, Sphere sphere) {
        Vector3f closest = closestPoint(sphere.gameObject.transform.position, triangle);
        float lengthSq = new Vector3f(sphere.gameObject.transform.position).sub(closest).lengthSquared();
        return lengthSq <= sphere.radius() * sphere.radius();
    }

    public static boolean triangleAndBox(Triangle triangle, Box box) {
        // TODO: WRITE TESTS FOR THIS
        // Find edge vectors of triangle
        Vector3f f0 = new Vector3f(triangle.b()).sub(triangle.a());
        Vector3f f1 = new Vector3f(triangle.c()).sub(triangle.b());
        Vector3f f2 = new Vector3f(triangle.a()).sub(triangle.c());

        // Find face normals of box
        Vector3f u0 = box.getAxis(0);
        Vector3f u1 = box.getAxis(1);
        Vector3f u2 = box.getAxis(2);

        Vector3f[] axes = {
                u0, u1, u2, // Box normals
                new Vector3f(f0).cross(f1), // Triangle normal

                // All the cross products
                new Vector3f(u0).cross(f0), new Vector3f(u0).cross(f1), new Vector3f(u0).cross(f2),
                new Vector3f(u1).cross(f0), new Vector3f(u1).cross(f1), new Vector3f(u1).cross(f2),
                new Vector3f(u2).cross(f0), new Vector3f(u2).cross(f1), new Vector3f(u2).cross(f2)
        };

        for (int i=0; i < axes.length; i++) {
            if (!overlapOnAxis(box, triangle, axes[i])) {
                return false; // Separating axis was found
            }
        }

        // No separating axis found
        return true;
    }

    public static boolean triangleAndPlane(Triangle triangle, Plane plane) {
        // TODO: WRITE TESTS FOR THIS
        // Find which side of the plane every point of the triangle is on
        float side1 = planeEquation(triangle.a(), plane);
        float side2 = planeEquation(triangle.b(), plane);
        float side3 = planeEquation(triangle.c(), plane);

        // If all points are ont he plane, they intersect
        if (JMath.compare(side1, 0) && JMath.compare(side2, 0) && JMath.compare(side3, 0)) {
            return true;
        }

        // If all 3 points are in front of the plane, it does not intersect
        if (side1 > 0 && side2 > 0 && side3 > 0) {
            return false;
        }

        // If all 3 points of the triangle are behind the plane, it does not intersect
        if (side1 < 0 && side2 < 0 && side3 < 0) {
            return false;
        }

        // At least 2 points are on opposite sides of the plane
        return true;
    }

    public static boolean triangleAndTriangle(Triangle t1, Triangle t2) {
        // TODO: WRITE TESTS FOR THIS
        Vector3f[] axes = {
            satCrossEdge(t1.a(), t1.b(), t1.b(), t1.c()), // Triangle 1 normal
            satCrossEdge(t2.a(), t2.b(), t2.b(), t2.c()), // Triangle 2 normal

            // All cross products
            satCrossEdge(t2.a(), t2.b(), t1.a(), t1.b()),
            satCrossEdge(t2.a(), t2.b(), t1.b(), t1.c()),
            satCrossEdge(t2.a(), t2.b(), t1.c(), t1.a()),

            satCrossEdge(t2.b(), t2.c(), t1.a(), t1.b()),
            satCrossEdge(t2.b(), t2.c(), t1.b(), t1.c()),
            satCrossEdge(t2.b(), t2.c(), t1.c(), t1.a()),

            satCrossEdge(t2.c(), t2.a(), t1.a(), t1.b()),
            satCrossEdge(t2.c(), t2.a(), t1.b(), t1.c()),
            satCrossEdge(t2.c(), t2.a(), t1.c(), t1.a())
        };

        for (int i=0; i < 11; i++) {
            if (!overlapOnAxis(t1, t2, axes[i])) {
                return false; // Separating axis found
            }
        }

        // No separating axis found
        return true;
    }


    // ====================================================================
    // Sphere vs Primitive tests
    // ====================================================================
    public static boolean sphereAndTriangle(Sphere sphere, Triangle triangle) {
        return triangleAndSphere(triangle, sphere);
    }

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
    public static float planeEquation(Vector3f point, Plane plane) {
        return point.dot(plane.normal()) - plane.distanceFromOrigin();
    }

    private static Vector3f barycentric(Vector3f point, Triangle triangle) {
        Vector3f ap = new Vector3f(point).sub(triangle.a());
        Vector3f bp = new Vector3f(point).sub(triangle.b());
        Vector3f cp = new Vector3f(point).sub(triangle.c());

        Vector3f ab = new Vector3f(triangle.b()).sub(triangle.a());
        Vector3f ac = new Vector3f(triangle.c()).sub(triangle.a());
        Vector3f bc = new Vector3f(triangle.c()).sub(triangle.b());
        Vector3f cb = new Vector3f(triangle.b()).sub(triangle.c());
        Vector3f ca = new Vector3f(triangle.a()).sub(triangle.c());

        Vector3f v = new Vector3f(ab).sub(JMath.project(ca, ab));
        float a = 1f - (v.dot(ap) / v.dot(ab));

        v = new Vector3f(bc).sub(JMath.project(bc, ac));
        float b = 1f - (v.dot(bp) / v.dot(bc));

        v = new Vector3f(ca).sub(JMath.project(ca, ab));
        float c = 1f - (v.dot(cp) / v.dot(ca));

        return new Vector3f(a, b, c);
    }

    private static Vector3f satCrossEdge(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        Vector3f ab = new Vector3f(a).sub(b);
        Vector3f cd = new Vector3f(c).sub(d);
        Vector3f result = new Vector3f(ab).cross(cd);

        if (!JMath.compare(result.lengthSquared(), 0)) {
            return result; // The cross edges are not parallel
        } else {
            // ab and cd are parallel
            Vector3f axis = new Vector3f(ab).cross(new Vector3f(c).sub(a));
            result = new Vector3f(ab).cross(axis);
            if (!JMath.compare(result.lengthSquared(), 0)) {
                return result; // Not parallel
            }
        }

        return new Vector3f(); // Triangles are coplanar, no way to get a good cross product
    }

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

    private static boolean overlapOnAxis(Box box, Triangle triangle, Vector3f axis) {
        Vector2f aInterval = getInterval(box, axis);
        Vector2f bInterval = getInterval(triangle, axis);
        return ((bInterval.x <= aInterval.y) && (aInterval.x <= bInterval.y));
    }

    private static boolean overlapOnAxis(Triangle t1, Triangle t2, Vector3f axis) {
        Vector2f t1Interval = getInterval(t1, axis);
        Vector2f t2Interval = getInterval(t2, axis);
        return ((t2Interval.x <= t1Interval.y) && (t1Interval.x <= t2Interval.y));
    }

    private static Vector2f getInterval(Box box, Vector3f axis) {
        Vector3f[] vertices = box.getVertices();
        Vector2f result = new Vector2f();
        result.x = result.y = axis.dot(vertices[0]);

        for (int i=1; i < 8; i++) {
            float projection = axis.dot(vertices[i]);
            result.x = Math.min(projection, result.x);
            result.y = Math.max(projection, result.y);
        }

        return result;
    }

    private static Vector2f getInterval(Triangle triangle, Vector3f axis) {
        Vector2f result = new Vector2f();

        result.x = axis.dot(triangle.points()[0]);
        result.y = result.x;
        for (int i=1; i < 3; i++) {
            float value = axis.dot(triangle.points()[i]);
            result.x = Math.min(result.x, value);
            result.y = Math.max(result.y, value);
        }

        return result;
    }

    private static Plane fromTriangle(Triangle t) {
        Vector3f normal = new Vector3f(t.b()).sub(t.a()).cross(new Vector3f(t.c()).sub(t.a()));
        float distance = normal.dot(t.a());
        return new Plane(normal, distance);
    }
}
