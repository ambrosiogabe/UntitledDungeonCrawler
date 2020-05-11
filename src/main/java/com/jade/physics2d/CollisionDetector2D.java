package com.jade.physics2d;

import com.jade.physics2d.primitives.Box2D;
import com.jade.physics2d.primitives.Circle;
import com.jade.physics2d.primitives.Line2D;
import com.jade.util.JMath;
import org.joml.Vector2f;

public class CollisionDetector2D {

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
        return false;
    }
}
