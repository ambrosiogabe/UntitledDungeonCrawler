package com.jade.physics.colliders;

import com.jade.Component;
import org.joml.Matrix3f;

public abstract class Collider extends Component {

    public abstract Matrix3f getInertiaTensor(float mass);
}
