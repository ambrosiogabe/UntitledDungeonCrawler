package com.jade.components;

import com.jade.Component;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.prefabs.Prefabs;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointLight extends Component {
    private Model model;

    private Vector4f color;
    private float strength;

    public PointLight(Vector4f color, float strength) {
        this.color = color;
        this.strength = strength;
    }

    @Override
    public void start() {
        this.model = new Model("mesh-ext/plane.fbx", "images/lightBulb.png");
        this.gameObject.addComponent(this.model);
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }
}
