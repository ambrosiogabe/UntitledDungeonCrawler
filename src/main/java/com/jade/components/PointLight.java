package com.jade.components;

import com.jade.Component;
import imgui.ImGui;
import org.joml.Vector3f;

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
    public void imgui() {
        float[] imColor = {this.color.x, this.color.y, this.color.z};
        if(ImGui.colorPicker3("Light Color", imColor)) {
            this.color.x = imColor[0];
            this.color.y = imColor[1];
            this.color.z = imColor[2];
        }
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
