package com.jade.physics.primitives;

import com.jade.Component;
import com.jade.util.DebugDraw;
import imgui.ImFloat;
import imgui.ImGui;

public class Sphere extends Collider {
    private float radius;

    public Sphere(float radius) {
        this.radius = radius;
    }

    @Override
    public void drawGizmo() {
        DebugDraw.addSphere(this.gameObject.transform.position, this.radius);
    }

    @Override
    public void imgui() {
        float[] imRadius = {this.radius};
        if (ImGui.dragFloat("Radius: ", imRadius)) {
            this.radius = imRadius[0];
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

    public float radius() {
        return this.radius;
    }
}
