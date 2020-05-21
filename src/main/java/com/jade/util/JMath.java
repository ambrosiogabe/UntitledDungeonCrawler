package com.jade.util;

import com.jade.file.Parser;
import com.jade.file.Serialize;
import com.jade.util.enums.DataType;
import org.joml.*;

import java.lang.Math;

public class JMath {
    private static final int FLOAT_SIZE = 4;
    private static final int INT_SIZE = 4;
    private static final int BOOL_SIZE = 4;

    public static int sizeof(DataType type) {
        switch (type) {
            case FLOAT:
                return FLOAT_SIZE;
            case INT:
                return INT_SIZE;
            case BOOL:
                return BOOL_SIZE;
            default:
                System.out.println("Size of this data type unknown in JMath");
                return 0;
        }
    }

    public static void copyValues(Vector4f from, Vector4f to) {
        to.x = from.x;
        to.y = from.y;
        to.z = from.z;
        to.w = from.w;
    }

    public static Vector2f copy(Vector2f vec) {
        return new Vector2f(vec.x, vec.y);
    }

    public static Vector3f copy(Vector3f vec) {
        return new Vector3f(vec.x, vec.y, vec.z);
    }

    public static Vector4f copy(Vector4f vec) {
        return new Vector4f(vec.x, vec.y, vec.z, vec.w);
    }

    public static String serialize(Vector2f vec, int tabSize) {
        return Serialize.addFloatProperty("X", vec.x, tabSize, true, true) +
                Serialize.addFloatProperty("Y", vec.y, tabSize, true, false);
    }

    public static String serialize(Vector3f vec, int tabSize) {
        return Serialize.addFloatProperty("X", vec.x, tabSize, true, true) +
                Serialize.addFloatProperty("Y", vec.y, tabSize, true, true) +
                Serialize.addFloatProperty("Z", vec.z, tabSize, true, false);
    }

    public static String serialize(Vector4f vec, int tabSize) {
        return Serialize.addFloatProperty("X", vec.x, tabSize, true, true) +
                Serialize.addFloatProperty("Y", vec.y, tabSize, true, true) +
                Serialize.addFloatProperty("Z", vec.z, tabSize, true, true) +
                Serialize.addFloatProperty("W", vec.w, tabSize, true, false);
    }

    public static Vector4f deserializeVector4f() {
        float x = Parser.consumeFloatProperty("X");
        Parser.consume(',');
        float y = Parser.consumeFloatProperty("Y");
        Parser.consume(',');
        float z = Parser.consumeFloatProperty("Z");
        Parser.consume(',');
        float w = Parser.consumeFloatProperty("W");

        return new Vector4f(x, y, z, w);
    }

    public static Vector3f deserializeVector3f() {
        float x = Parser.consumeFloatProperty("X");
        Parser.consume(',');
        float y = Parser.consumeFloatProperty("Y");
        Parser.consume(',');
        float z = Parser.consumeFloatProperty("Z");

        return new Vector3f(x, y, z);
    }

    public static Vector2f deserializeVector2f() {
        float x = Parser.consumeFloatProperty("X");
        Parser.consume(',');
        float y = Parser.consumeFloatProperty("Y");

        return new Vector2f(x, y);
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float xPrime = origin.x + ((x * (float)Math.cos(Math.toRadians(angleDeg))) - (y * (float)Math.sin(Math.toRadians(angleDeg))));
        float yPrime = origin.y + ((x * (float)Math.sin(Math.toRadians(angleDeg))) + (y * (float)Math.cos(Math.toRadians(angleDeg))));

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector3f origin) {
        float s = (float)(Math.sin(Math.toRadians(angleDeg)));
        float c = (float)(Math.cos(Math.toRadians(angleDeg)));

        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float xPrime = origin.x + (x * c) - (y * s);
        float yPrime = origin.y + (x * s) + (y * c);

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static float project(Vector3f a, Vector3f b) {
        return a.dot(new Vector3f(b).normalize());
    }

    public static Matrix3f createRectanglularPrismInertiaTensor(float mass, Vector3f dimensions) {
        float xy00 = (1.0f / 12.0f) * mass * (dimensions.y * dimensions.y + dimensions.z * dimensions.z);
        float xy11 = (1.0f / 12.0f) * mass * (dimensions.x * dimensions.x + dimensions.z * dimensions.z);
        float xy22 = (1.0f / 12.0f) * mass * (dimensions.x * dimensions.x + dimensions.y * dimensions.y);

        return new Matrix3f(xy00, 0, 0,
                      0, xy11, 0,
                      0, 0, xy22);
    }

    public static Matrix3f createSphereInertiaTensor(float mass, float radius) {
        float xy00 = (2f / 5f) * mass * (radius * radius);
        float xy11 = xy00;
        float xy22 = xy00;

        return new Matrix3f(xy00, 0, 0,
                0, xy11, 0,
                0, 0, xy22);
    }

    public static Matrix3f createPlaneInertiaTensor(float mass, Vector2f dimensions) {
        float xy00 = (1.0f / 12.0f) * mass * (dimensions.y * dimensions.y);
        float xy11 = (1.0f / 12.0f) * mass * (dimensions.x * dimensions.x);
        float xy22 = (1.0f / 12.0f) * mass * (dimensions.x * dimensions.x + dimensions.y * dimensions.y);

        return new Matrix3f(xy00, 0, 0,
                0, xy11, 0,
                0, 0, xy22);
    }

    public static float createSquareInertiaTensor(float mass, Vector2f dimensions) {
        return (1f / 12f) * mass * (dimensions.x * dimensions.x + dimensions.y * dimensions.y);
    }

    public static float createCircleInertiaTensor(float mass, float radius) {
        return 0.5f * mass * radius * radius;
    }

    // Compares two floating point numbers and returns whether they are almost equal with custom threshold
    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(Vector3f vec1, Vector3f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon) && compare(vec1.z, vec2.z, epsilon);
    }

    public static boolean compare(Vector4f vec1, Vector4f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon) && compare(vec1.z, vec2.z, epsilon) && compare(vec1.w, vec2.w, epsilon);
    }

    // Compares two floating point numbers and returns whether they are almost equal
    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }

    public static boolean compare(Vector3f vec1, Vector3f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y) && compare(vec1.z, vec2.z);
    }

    public static boolean compare(Vector4f vec1, Vector4f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y) && compare(vec1.z, vec2.z) && compare(vec1.w, vec2.w);
    }

    public static Vector2f vector2fFrom3f(Vector3f vec) {
        return new Vector2f(vec.x, vec.y);
    }

    public static Vector2f vector2fFrom4f(Vector4f vec) {
        return new Vector2f(vec.x, vec.y);
    }

    public static Vector3f vector3fFrom2f(Vector2f vec) {
        return new Vector3f(vec.x, vec.y, 0);
    }

    public static Vector2f transformFromModelMatrix(Vector2f vec, Matrix4f modelMatrix) {
        Vector4f tmp = new Vector4f(vec.x, vec.y, 0, 1);
        modelMatrix.transform(tmp);
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector3f transformFromModelMatrix(Vector3f vec, Matrix4f modelMatrix) {
        Vector4f tmp = new Vector4f(vec.x, vec.y, vec.z, 1);
        modelMatrix.transform(tmp);
        return new Vector3f(tmp.x, tmp.y, tmp.z);
    }
}
