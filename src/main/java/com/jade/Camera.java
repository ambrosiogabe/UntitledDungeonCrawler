package com.jade;

import com.jade.util.Constants;
import com.sun.javafx.sg.prism.NGAmbientLight;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, orthoProjection, orthoView, orthoInverseProjection, modelMatrix;
    private float fov = 45;
    private float aspect = 0.0f;

    private Vector3f cameraForward = new Vector3f();
    private Vector3f cameraUp = new Vector3f();
    private Vector3f cameraRight = new Vector3f();

    public Transform transform;

    public Camera(Vector3f position) {
        this.transform = new Transform(position);

        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.orthoProjection = new Matrix4f();
        this.orthoView = new Matrix4f();
        this.orthoInverseProjection = new Matrix4f();

        this.caluclateAspect();
        this.adjustPerspective();
    }

    private void caluclateAspect() {
        this.aspect = (float)Window.getWindow().getWidth() / (float)Window.getWindow().getHeight();
    }

    public void adjustPerspective() {
        this.caluclateAspect();
        projectionMatrix.identity();
        projectionMatrix = projectionMatrix.perspective(fov, (float) Window.getWindow().getWidth() / (float) Window.getWindow().getHeight(),
                0.1f, 100.0f);

        orthoProjection.identity();
        orthoProjection.ortho(0, 1080.0f * aspect, 0, 1080.0f, 0.0f, 100.0f);
        orthoInverseProjection.identity();
        orthoProjection.invert(orthoInverseProjection);
    }

    public Matrix4f getViewMatrix() {
        cameraForward.x = (float)(Math.cos(Math.toRadians(transform.rotation.y)) * Math.cos(Math.toRadians(transform.rotation.x)));
        cameraForward.y = (float)Math.sin(Math.toRadians(transform.rotation.x));
        cameraForward.z = (float)(Math.sin(Math.toRadians(transform.rotation.y)) * Math.cos(Math.toRadians(transform.rotation.x)));
        cameraForward.normalize();

        cameraForward.cross(Constants.UP, cameraRight);
        cameraRight.cross(cameraForward, cameraUp);

        Vector3f tmp = new Vector3f(transform.position.x, transform.position.y, transform.position.z);
        tmp.add(cameraForward);

        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(transform.position, tmp, cameraUp);

        return this.viewMatrix;
    }

    public Vector3f cameraForward() {
        return this.cameraForward;
    }

    public Vector3f cameraUp() {
        return this.cameraUp;
    }

    public Vector3f cameraRight() {
        return this.cameraRight;
    }

    public Matrix4f getOrthoView() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(0.0f, 0.0f, 20.0f), cameraFront, cameraUp);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getOrthoProjection() {
        return orthoProjection;
    }

    public Matrix4f getInverseProjection() {
        return this.inverseProjection;
    }

    public Matrix4f getOrthoInverseProjection() {
        return this.orthoInverseProjection;
    }
}
