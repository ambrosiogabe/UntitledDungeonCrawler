package com.jade.physics.rigidbody.colliders;

import com.jade.Component;
import com.jade.physics.rigidbody.collisions.CollisionData;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import imgui.ImGui;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BoxCollider extends Collider {

    private Vector3f dimensions;
    private Vector3f halfSize;
    private Vector3f offset;
    private Vector3f tmp;

    private Vector3f[] baseVertices = new Vector3f[8];
    private Vector3f[] transformedVertices = new Vector3f[8];

    public BoxCollider(Vector3f dimensions, Vector3f offset) {
        this.dimensions = dimensions;
        this.halfSize = new Vector3f(this.dimensions).mul(0.5f);
        this.offset = offset;
        this.tmp = new Vector3f();

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

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    private void calculateVertices() {
        for (int i=0; i < this.baseVertices.length; i++) {
            Vector4f tmp = new Vector4f(this.baseVertices[i], 1);
            this.gameObject.transform.modelMatrix.transform(tmp);
            this.transformedVertices[i].set(tmp.x, tmp.y, tmp.z);
        }
    }

    @Override
    public void update(float dt) {
        this.calculateVertices();
    }

    public void debugTestPlaneCollision(Plane plane, CollisionData data) {
        if (CollisionDetector.boxAndHalfSpace(this, plane, data) > 0) {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_RED);
        } else {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_GREEN);
        }
    }

    public void debugTestSphereCollision(SphereCollider sphere, CollisionData data) {
        if (CollisionDetector.boxAndSphere(this, sphere, data) > 0) {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_RED);
        } else {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_GREEN);
        }
    }

    public void debugTestBoxDetection(BoxCollider box) {
        if (IntersectionTests.boxAndBox(this, box)) {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_RED);
        } else {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_GREEN);
        }
    }

    public void debugTestBoxCollision(BoxCollider box, CollisionData data) {
        if (CollisionDetector.boxAndBox(this, box, data) > 0) {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_RED);
        } else {
            DebugDraw.addBox(this.gameObject.transform.position, this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_GREEN);
        }
    }

    @Override
    public void drawGizmo() {
        DebugDraw.addBox(tmp.set(this.gameObject.transform.position).add(this.offset),
                this.dimensions, this.gameObject.transform, 0.05f, Constants.COLOR3_GREEN, 1);
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

    public Vector3f getHalfSize() {
        return this.halfSize;
    }

    public Vector3f[] getVertices() {
        return this.transformedVertices;
    }
}
