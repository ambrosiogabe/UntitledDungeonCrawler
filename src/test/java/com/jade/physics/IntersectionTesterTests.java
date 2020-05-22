package com.jade.physics;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.physics.primitives.Box;
import com.jade.physics.primitives.IntersectionTester;
import com.jade.physics.primitives.Ray;
import com.jade.physics.primitives.Sphere;
import com.jade.physics.rigidbody.colliders.IntersectionTests;
import com.jade.renderer.Line;
import com.jade.util.JMath;
import org.joml.Vector3f;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class IntersectionTesterTests {

    private final float EPSILON = 0.000001f;

    // =========================================================================================================
    // Line IntersectionTester tests
    // =========================================================================================================
    @Test
    public void pointOnLineShouldReturnTrueTestOne() {
        Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(12, 4, 0));
        Vector3f point = new Vector3f(0, 0, 0);

        assertTrue(IntersectionTester.pointOnLine(point, line));
    }

    @Test
    public void pointOnLineShouldReturnTrueTestTwo() {
        Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(12, 4, 0));
        Vector3f point = new Vector3f(6, 2, 0);

        assertTrue(IntersectionTester.pointOnLine(point, line));
    }

    @Test
    public void pointOnLineShouldReturnFalseTestOne() {
        Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(12, 4, 0));
        Vector3f point = new Vector3f(4, 2, 0);

        assertFalse(IntersectionTester.pointOnLine(point, line));
    }

    @Test
    public void pointOnLineShouldReturnTrueTestThree() {
        Line line = new Line(new Vector3f(10, 10, 10), new Vector3f(22, 14, 10));
        Vector3f point = new Vector3f(10, 10, 10);

        assertTrue(IntersectionTester.pointOnLine(point, line));
    }

    @Test
    public void pointOnLineShouldReturnTrueTestFour() {
        Line line = new Line(new Vector3f(10, 10, 10), new Vector3f(22, 14, 10));
        Vector3f point = new Vector3f(16, 12, 10);

        assertTrue(IntersectionTester.pointOnLine(point, line));
    }

    @Test
    public void pointOnLineShouldReturnFalseTestTwo() {
        Line line = new Line(new Vector3f(10, 10, 10), new Vector3f(22, 14, 10));
        Vector3f point = new Vector3f(14, 12, 10);

        assertFalse(IntersectionTester.pointOnLine(point, line));
    }

    @Test
    public void closestPointToLineTestOne() {
        Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(12, 4, 0));
        Vector3f point = new Vector3f(6, 2, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, line);
        Vector3f actualClosestPoint = new Vector3f(6, 2, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToLineTestTwo() {
        Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(12, 4, 0));
        Vector3f point = new Vector3f(13, 3, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, line);
        Vector3f actualClosestPoint = new Vector3f(12, 4, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToLineTestThree() {
        Line line = new Line(new Vector3f(0, 0, 0), new Vector3f(12, 4, 0));
        Vector3f point = new Vector3f(7, 4, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, line);
        Vector3f actualClosestPoint = new Vector3f(7.5f, 2.5f, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToLineTestFour() {
        Line line = new Line(new Vector3f(10, 10, 10), new Vector3f(22, 14, 10));
        Vector3f point = new Vector3f(16, 12, 10);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, line);
        Vector3f actualClosestPoint = new Vector3f(16, 12, 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToLineTestFive() {
        Line line = new Line(new Vector3f(10, 10, 10), new Vector3f(22, 14, 10));
        Vector3f point = new Vector3f(23, 13, 10);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, line);
        Vector3f actualClosestPoint = new Vector3f(22, 14, 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToLineTestSix() {
        Line line = new Line(new Vector3f(10, 10, 10), new Vector3f(22, 14, 10));
        Vector3f point = new Vector3f(17, 14, 10);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, line);
        Vector3f actualClosestPoint = new Vector3f(17.5f, 12.5f, 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }




    // =========================================================================================================
    // Raycast IntersectionTester tests
    // =========================================================================================================
    @Test
    public void pointOnRayShouldReturnTrueTestOne() {
        Ray ray = new Ray(new Vector3f(0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(0, 0, 0);

        assertTrue(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnTrueTestTwo() {
        Ray ray = new Ray(new Vector3f(0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(6, 2, 0);

        assertTrue(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnFalseTestOne() {
        Ray ray = new Ray(new Vector3f(0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(-6, -2, 0);

        assertFalse(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnFalseTestTwo() {
        Ray ray = new Ray(new Vector3f(0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(4, 2, 0);

        assertFalse(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnTrueTestThree() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(10, 10, 0);

        assertTrue(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnTrueTestFour() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(16, 12, 0);

        assertTrue(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnFalseTestThree() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(-6 + 10, -2 + 10, 0);

        assertFalse(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void pointOnRayShouldReturnFalseTestFour() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(14, 12, 0);

        assertFalse(IntersectionTester.pointOnRay(point, ray));
    }

    @Test
    public void closestPointToRayTestOne() {
        Ray ray = new Ray(new Vector3f(0, 0, 0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(-1, -1, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, ray);
        Vector3f actualClosestPoint = new Vector3f(0, 0, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToRayTestTwo() {
        Ray ray = new Ray(new Vector3f(0, 0, 0), new Vector3f((float)(3.0 / Math.sqrt(10f)), (float)(1.0 / Math.sqrt(10f)), 0));
        Vector3f point = new Vector3f(6, 2, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, ray);
        Vector3f actualClosestPoint = new Vector3f(6, 2, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRayTestThree() {
        Ray ray = new Ray(new Vector3f(0, 0, 0), new Vector3f((float)(3.0 / Math.sqrt(10f)), (float)(1.0 / Math.sqrt(10f)), 0));
        Vector3f point = new Vector3f(7, 4, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, ray);
        Vector3f actualClosestPoint = new Vector3f(7.5f, 2.5f, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRayTestFour() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f(0.948683f, 0.316228f, 0));
        Vector3f point = new Vector3f(9, 9, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, ray);
        Vector3f actualClosestPoint = new Vector3f(10, 10, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToRayTestFive() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f((float)(3.0 / Math.sqrt(10f)), (float)(1.0 / Math.sqrt(10f)), 0));
        Vector3f point = new Vector3f(16, 12, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, ray);
        Vector3f actualClosestPoint = new Vector3f(16, 12, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRayTestSix() {
        Ray ray = new Ray(new Vector3f(10, 10, 0), new Vector3f((float)(3.0 / Math.sqrt(10f)), (float)(1.0 / Math.sqrt(10f)), 0));
        Vector3f point = new Vector3f(17, 14, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, ray);
        Vector3f actualClosestPoint = new Vector3f(17.5f, 12.5f, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }




    // =========================================================================================================
    // Sphere intersection tester tests
    // =========================================================================================================
    @Test
    public void pointInSphereShouldReturnTrueTestOne() {
        Sphere sphere = new Sphere(5f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f()));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(3, -2, -2);

        boolean result = IntersectionTester.pointInSphere(point, sphere);
        assertTrue(result);
    }

    @Test
    public void pointInSphereShouldReturnTrueTestTwo() {
        Sphere sphere = new Sphere(5f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f()));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(-4.9f, 0, 0);

        boolean result = IntersectionTester.pointInSphere(point, sphere);
        assertTrue(result);
    }

    @Test
    public void pointInSphereShouldReturnFalseTestOne() {
        Sphere sphere = new Sphere(5f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f()));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(-6, -6, 1);

        boolean result = IntersectionTester.pointInSphere(point, sphere);
        assertFalse(result);
    }

    @Test
    public void pointInSphereShouldReturnTrueTestFour() {
        Sphere sphere = new Sphere(5f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(3 + 10, -2 + 10, -2 + 10);

        boolean result = IntersectionTester.pointInSphere(point, sphere);
        assertTrue(result);
    }

    @Test
    public void pointInSphereShouldReturnTrueTestFive() {
        Sphere sphere = new Sphere(5f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(-4.9f + 10, 0 + 10, 0 + 10);

        boolean result = IntersectionTester.pointInSphere(point, sphere);
        assertTrue(result);
    }

    @Test
    public void pointInSphereShouldReturnFalseTestTwo() {
        Sphere sphere = new Sphere(5f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(-6 + 10, -6 + 10, 1 + 10);

        boolean result = IntersectionTester.pointInSphere(point, sphere);
        assertFalse(result);
    }

    @Test
    public void closestPointToSphereTestOne() {
        Sphere sphere = new Sphere(1f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(5, 0, 0);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, sphere);
        Vector3f actualClosestPoint = new Vector3f(1, 0, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToSphereTestTwo() {
        Sphere sphere = new Sphere(1f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(2.5f, -2.5f, 2.5f);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, sphere);
        Vector3f actualClosestPoint = new Vector3f(0.5773502f, -0.5773502f, 0.5773502f);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToSphereTestThree() {
        Sphere sphere = new Sphere(1f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(5 + 10, 0 + 10, 0 + 10);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, sphere);
        Vector3f actualClosestPoint = new Vector3f(1 + 10, 0 + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToSphereTestFour() {
        Sphere sphere = new Sphere(1f);
        GameObject gameObject = new GameObject("Sphere Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(sphere);

        Vector3f point = new Vector3f(2.5f + 10, -2.5f + 10, 2.5f + 10);

        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, sphere);
        Vector3f actualClosestPoint = new Vector3f(0.5773502f + 10, -0.5773502f + 10, 0.5773502f + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }



    // =========================================================================================================
    // Box intersection tester tests
    // =========================================================================================================
    @Test
    public void pointInBoxShouldReturnTrueTestOne() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(4, 0, 4.3f);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInBoxShouldReturnTrueTestTwo() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-4.9f, -4.9f, -4.9f);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInBoxShouldReturnFalseTestOne() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(0, 5.1f, 0);

        assertFalse(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInBoxShouldReturnTrueTestThree() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(4 + 10, 0 + 10, 4.3f + 10);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInBoxShouldReturnTrueTestFour() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-4.9f + 10, -4.9f + 10, -4.9f + 10);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInBoxShouldReturnFalseTestTwo() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(0 + 10, 5.1f + 10, 0 + 10);

        assertFalse(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInRotatedBoxShouldReturnTrueTestOne() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0), new Vector3f(1), new Vector3f(45, 45, 0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-1, -1, -1);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInRotatedShouldReturnTrueTestTwo() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-3.43553390593f, 3.43553390593f, 0);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInRotatedShouldReturnFalseTestOne() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-3.63553390593f, 3.63553390593f, 0);

        assertFalse(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInRotatedBoxShouldReturnTrueTestThree() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10), new Vector3f(1), new Vector3f(45, 45, 0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-1 + 10, -1 + 10, -1 + 10);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInRotatedShouldReturnTrueTestFour() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-3.43553390593f + 10, 3.43553390593f + 10, 0 + 10);

        assertTrue(IntersectionTester.pointInBox(point, box));
    }

    @Test
    public void pointInRotatedShouldReturnFalseTestTwo() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-3.63553390593f + 10, 3.63553390593f + 10, 0 + 10);

        assertFalse(IntersectionTester.pointInBox(point, box));
    }


    @Test
    public void closestPointToBoxTestOne() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(0, 10, 0);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(0, 5, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToBoxTestTwo() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-6, -4, 0);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(-5, -4, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToBoxTestThree() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(3, -4, 0);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(3, -4, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToBoxTestFour() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(0 + 10, 10 + 10, 0 + 10);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(0 + 10, 5 + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToBoxTestFive() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-6 + 10, -4 + 10, 0 + 10);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(-5 + 10, -4 + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToBoxTestSix() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(3 + 10, -4 + 10, 0 + 10);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(3 + 10, -4 + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint));
    }

    @Test
    public void closestPointToRotatedBoxTestOne() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(10, 0, 0);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(7.07106781187f, 0, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRotatedBoxTestTwo() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-5.5355339f, -5.5355339f, 0);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(-3.5355339f, -3.5355339f, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRotatedBoxTestThree() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(0), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(0, 7.07106781187f, 0);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(0, 7.07106781187f, 0);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRotatedBoxTestFour() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(10 + 10, 0 + 10, 0 + 10);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(7.07106781187f + 10, 0 + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRotatedBoxTestFive() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(-5.5355339f + 10, -5.5355339f + 10, 0 + 10);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(-3.5355339f + 10, -3.5355339f + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }

    @Test
    public void closestPointToRotatedBoxTestSix() {
        Box box = new Box(new Vector3f(10));
        GameObject gameObject = new GameObject("Box Test", new Transform(new Vector3f(10), new Vector3f(1), new Vector3f(0, 0, 45)));
        gameObject.addComponent(box);

        Vector3f point = new Vector3f(0 + 10, 7.07106781187f + 10, 0 + 10);
        Vector3f calculatedClosestPoint = IntersectionTester.closestPoint(point, box);
        Vector3f actualClosestPoint = new Vector3f(0 + 10, 7.07106781187f + 10, 0 + 10);

        assertTrue(JMath.compare(calculatedClosestPoint, actualClosestPoint, EPSILON));
    }
}
