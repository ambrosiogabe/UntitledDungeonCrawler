package com.jade.components;

import com.jade.Component;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.prefabs.Prefabs;
import com.jade.util.AssetPool;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointLight extends Component {
    private Billboard billboard;

    private Vector3f color;
    private float strength;

    public PointLight(Vector3f color, float strength) {
        this.color = color;
        this.strength = strength;
    }

    @Override
    public void start() {
        this.billboard = new Billboard("images/lightBulb.png");
        this.gameObject.addComponent(this.billboard);
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public Vector3f getColor() {
        return this.color;
    }
}
