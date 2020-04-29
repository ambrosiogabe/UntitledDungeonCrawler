package com.jade.physics.colliders;

import com.jade.Component;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import imgui.ImGui;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class BoxCollider extends Collider {

    private Vector3f dimensions;
    private Vector3f offset;
    private Vector3f tmp;

    public BoxCollider(Vector3f dimensions, Vector3f offset) {
        this.dimensions = dimensions;
        this.offset = offset;
        this.tmp = new Vector3f();
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    @Override
    public void drawGizmo() {
        DebugDraw.addBox(tmp.set(this.gameObject.transform.position).add(this.offset),
                this.dimensions, this.gameObject.transform, 0.1f, Constants.COLOR3_GREEN, 1);
    }

    @Override
    public void imgui() {
        float[] dimXYZ = {dimensions.x, dimensions.y, dimensions.z};
        float[] offXYZ = {offset.x, offset.y, offset.z};

        ImGui.dragFloat3("Dimensions", dimXYZ);
        ImGui.dragFloat3("Offset", offXYZ);

        if (!dimensions.equals(dimXYZ[0], dimXYZ[1], dimXYZ[2])) {
            dimensions.set(dimXYZ[0], dimXYZ[1], dimXYZ[2]);
        }

        if (!offset.equals(offXYZ[0], offXYZ[1], offXYZ[2])) {
            offset.set(offXYZ[0], offXYZ[1], offXYZ[2]);
        }
    }

    @Override
    public Matrix3f getInertiaTensor(float mass) {
        return JMath.createRectanglularPrismInertiaTensor(mass, dimensions);
    }
}
