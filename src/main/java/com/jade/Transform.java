package com.jade;

import com.jade.file.Parser;
import com.jade.file.Serialize;
import com.jade.util.JMath;
import org.joml.Vector3f;

public class Transform extends Serialize {
    public Vector3f position;
    public Vector3f scale;
    public Vector3f rotation;

    public Transform() {
        init(new Vector3f(0.0f), new Vector3f(1.0f), new Vector3f(0.0f, 0.0f, 0.0f));
    }

    public Transform(Vector3f position) {
        init(position, new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 0.0f));
    }

    public Transform(Vector3f position, Vector3f scale) {
        init(position, scale, new Vector3f(0.0f, 0.0f, 0.0f));
    }

    public Transform(Vector3f position, Vector3f scale, Vector3f rotation) {
        init(position, scale, rotation);
    }

    private void init(Vector3f position, Vector3f scale, Vector3f rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Transform copy() {
        Transform transform = new Transform(JMath.copy(this.position));
        transform.scale = JMath.copy(this.scale);
        transform.rotation = this.rotation;
        return transform;
    }

    public static void copyValues(Transform from, Transform to) {
        to.position.x = from.position.x;
        to.position.y = from.position.y;
        to.position.z = from.position.z;

        to.scale.x = from.scale.x;
        to.scale.y = from.scale.y;
        to.scale.z = from.scale.z;

        to.rotation.x = from.rotation.x;
        to.rotation.y = from.rotation.y;
        to.rotation.z = from.rotation.z;
    }

    @Override
    public String toString() {
        return "Position (" + position.x + ", " + position.y + ", " + position.z  + ")\n" +
                "Scale (" + scale.x + ", " + scale.y + ", " + scale.z + ")\n" +
                "Rotation(" + rotation.x + ", " + rotation.y + ", " + rotation.z + ")";
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("Transform", tabSize));

        builder.append(beginObjectProperty("Position", tabSize + 1));
        builder.append(JMath.serialize(position, tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        builder.append(beginObjectProperty("Scale", tabSize + 1));
        builder.append(JMath.serialize(scale, tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, true));

        builder.append(beginObjectProperty("Rotation", tabSize + 1));
        builder.append(JMath.serialize(rotation, tabSize + 2));
        builder.append(closeObjectProperty(tabSize + 1));
        builder.append(addEnding(true, false));

        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static Transform deserialize() {
        Parser.consumeBeginObjectProperty("Transform");
        Parser.consumeBeginObjectProperty("Position");
        Vector3f position = JMath.deserializeVector3f();
        Parser.consumeEndObjectProperty();

        Parser.consume(',');
        Parser.consumeBeginObjectProperty("Scale");
        Vector3f scale = JMath.deserializeVector3f();
        Parser.consumeEndObjectProperty();

        Parser.consume(',');
        Parser.consumeBeginObjectProperty("Rotation");
        Vector3f rotation = JMath.deserializeVector3f();
        Parser.consumeEndObjectProperty();

        return new Transform(position, rotation, scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform other = (Transform)o;
        return other.position.x == this.position.x && other.position.y == this.position.y && other.position.z == this.position.z &&
                other.scale.x == this.scale.x && other.scale.y == this.scale.y && other.scale.z == this.scale.z &&
                other.rotation.x == this.rotation.x && other.rotation.y == this.rotation.y && other.rotation.z == this.rotation.z;
    }
}
