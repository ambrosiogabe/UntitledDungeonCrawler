package com.jade.physics.primitives;

import com.jade.Component;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import imgui.ImGui;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Box extends Collider {
    private Vector3f size;
    private Vector3f halfSize;

    private Vector3f[] baseVertices = new Vector3f[8];
    private Vector3f[] transformedVertices = new Vector3f[8];

    public Box(Vector3f size) {
        this.size = size;
        this.halfSize = new Vector3f(size).mul(0.5f);

        this.baseVertices[0] = new Vector3f(-halfSize.x, -halfSize.y, -halfSize.z);
        this.baseVertices[1] = new Vector3f(-halfSize.x, -halfSize.y,  halfSize.z);
        this.baseVertices[2] = new Vector3f(-halfSize.x,  halfSize.y, -halfSize.z);
        this.baseVertices[3] = new Vector3f(-halfSize.x,  halfSize.y,  halfSize.z);
        this.baseVertices[4] = new Vector3f( halfSize.x, -halfSize.y, -halfSize.z);
        this.baseVertices[5] = new Vector3f( halfSize.x, -halfSize.y,  halfSize.z);
        this.baseVertices[6] = new Vector3f( halfSize.x,  halfSize.y, -halfSize.z);
        this.baseVertices[7] = new Vector3f( halfSize.x,  halfSize.y,  halfSize.z);

        for (int i=0; i < transformedVertices.length; i++) {
            this.transformedVertices[i] = new Vector3f();
        }
    }

    private void calculateVertices() {
        for (int i=0; i < this.baseVertices.length; i++) {
            Vector4f tmp = new Vector4f(this.baseVertices[i], 1);
            Matrix4f scaleFree = new Matrix4f(this.gameObject.transform.modelMatrix);
            scaleFree.mul(1f / this.gameObject.transform.scale.x, 0f, 0f, 0f,
                            0f, 1f / this.gameObject.transform.scale.y, 0f, 0f,
                            0f, 0f, 1f / this.gameObject.transform.scale.z, 0f,
                            0f, 0f, 0f, 1f);
            tmp.mul(scaleFree);
            this.transformedVertices[i].set(tmp.x, tmp.y, tmp.z);
        }
    }

    @Override
    public void update(float dt) {
        this.calculateVertices();
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public void imgui() {
        float[] size = {this.size.x, this.size.y, this.size.z};
        ImGui.dragFloat3("Size: ", size);
        if (!this.size.equals(size[0], size[1], size[2])) {
            this.size.set(size[0], size[1], size[2]);
            this.halfSize.set(size[0] / 2f, size[1] / 2f, size[2] / 2f);
        }
    }

    @Override
    public void drawGizmo() {
        DebugDraw.addBox(this.gameObject.transform.position, this.size, this.gameObject.transform);
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public float getHalfSize(int i) {
        return this.halfSize.get(i);
    }

    public Vector3f getHalfSize() {
        return this.halfSize;
    }

    public Vector3f getSize() {
        return this.size;
    }

    public Vector3f halfSize() {
        return this.halfSize;
    }

    public Vector3f getMaximum() {
        return new Vector3f(this.gameObject.transform.position).add(this.halfSize);
    }

    public Vector3f getMinimum() {
        return new Vector3f(this.gameObject.transform.position).sub(this.halfSize);
    }

    public Vector3f[] getVertices() {
        return this.transformedVertices;
    }

    @Override
    public Matrix4f getInertiaTensor(float mass) {
        return JMath.createRectanglularPrismInertiaTensor(mass, this.size);
    }
}
