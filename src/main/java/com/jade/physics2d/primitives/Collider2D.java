package com.jade.physics2d.primitives;

import com.jade.Component;
import org.joml.Vector2f;

public abstract class Collider2D extends Component {
    protected Vector2f offset = new Vector2f();

    public abstract float getInertiaTensor(float mass);
}
