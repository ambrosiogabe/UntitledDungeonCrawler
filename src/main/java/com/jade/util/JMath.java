package com.jade.util;

import com.jade.file.Parser;
import com.jade.file.Serialize;
import com.jade.util.enums.DataType;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public static void rotate(Vector2f vec, float angle, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float xPrime = vec.x + ((x * (float)Math.cos(Math.toRadians(angle))) - (y * (float)Math.sin(Math.toRadians(angle))));
        float yPrime = vec.y + ((x * (float)Math.sin(Math.toRadians(angle))) + (y * (float)Math.cos(Math.toRadians(angle))));

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static void rotate(Vector2f vec, float angle, Vector3f origin) {
        float s = (float)(Math.sin(Math.toRadians(angle)));
        float c = (float)(Math.cos(Math.toRadians(angle)));

        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float xPrime = x * c - y * s;
        float yPrime = x * s + y * c;

        vec.x = xPrime + origin.x;
        vec.y = yPrime + origin.y;
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
}
