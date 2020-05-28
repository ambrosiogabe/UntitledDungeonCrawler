package com.jade.physics.primitives;

import com.jade.Component;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Collider extends Component {
    public Vector3f getAxis(int index) {
        Matrix4f data = this.gameObject.transform.modelMatrix;
        Vector3f axis = new Vector3f(data.get(index, 0), data.get(index, 1), data.get(index, 2));
        axis.normalize();
        return axis;
    }

    public abstract Matrix4f getInertiaTensor(float mass);
}
