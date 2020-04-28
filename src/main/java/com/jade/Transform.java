package com.jade;

import com.jade.file.Parser;
import com.jade.file.Serialize;
import com.jade.util.Constants;
import com.jade.util.JMath;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform extends Serialize {
    public Vector3f position;
    public Vector3f scale;
    public Vector3f rotation;

    public Vector3f forward;
    public Vector3f up;
    public Vector3f right;
    public Quaternionf orientation;

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
        this.forward = new Vector3f();
        this.right = new Vector3f();
        this.up = new Vector3f();

        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.orientation = new Quaternionf().fromAxisAngleDeg(Constants.RIGHT, this.rotation.x);
        this.orientation.mul(new Quaternionf().fromAxisAngleDeg(Constants.UP, this.rotation.y));
        this.orientation.mul(new Quaternionf().fromAxisAngleDeg(Constants.FORWARD, this.rotation.z));

        this.forward.set(Constants.FORWARD);
        this.orientation.transform(this.forward);
        this.up.set(Constants.UP);
        this.orientation.transform(this.up);
        this.right.set(Constants.RIGHT);
        this.orientation.transform(this.right);
    }

    public Transform copy() {
        return new Transform(JMath.copy(this.position), JMath.copy(this.scale), JMath.copy(this.rotation));
    }

    public static void copyValues(Transform from, Transform to) {
        to.position.set(from.position);
        to.scale.set(from.scale);
        to.rotation.set(from.rotation);
        to.orientation.set(from.orientation);
        to.forward.set(from.forward);
        to.right.set(from.right);
        to.up.set(from.up);
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
        return other.position.equals(this.position) &&
                other.scale.equals(this.scale) &&
                other.rotation.equals(this.rotation) &&
                this.orientation.equals(other.orientation);
    }
}
