package com.jade.physics.primitives;

import com.jade.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Collider extends Component {
    public Vector3f getAxis(int index) {
        Matrix4f data = this.gameObject.transform.modelMatrix;
        return new Vector3f(data.get(index, 0), data.get(index, 1), data.get(index, 2));
    }
}
