package com.jade.components;

import com.jade.Camera;
import com.jade.Component;
import com.jade.Window;
import com.jade.events.KeyListener;
import com.jade.events.MouseListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class FlyingCameraController extends Component {

    private Camera mainCamera;
    private float yaw, pitch;

    @Override
    public void start() {
        this.yaw = 0;
        this.pitch = 0;
        mainCamera = Window.getScene().camera();
    }

    @Override
    public void update(float dt) {
        float velocity = 50.0f * dt;
        Vector3f speedVec = new Vector3f(0.0f, 0.0f, 0.0f);

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            Window.lockCursor();
            if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                speedVec.add(mainCamera.cameraForward());
                speedVec.mul(velocity);
                mainCamera.transform.position.add(speedVec);
            } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                speedVec.add(mainCamera.cameraForward());
                speedVec.mul(velocity);
                mainCamera.transform.position.sub(speedVec);
            } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                speedVec.add(mainCamera.cameraRight());
                speedVec.mul(velocity);
                mainCamera.transform.position.sub(speedVec);
            } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                speedVec.add(mainCamera.cameraRight());
                speedVec.mul(velocity);
                mainCamera.transform.position.add(speedVec);
            }

            float sensitivity = 0.05f;
            yaw -= MouseListener.getDx() * sensitivity;
            pitch += MouseListener.getDy() * sensitivity;

            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;

            mainCamera.transform.rotation.x = pitch;
            mainCamera.transform.rotation.y = yaw;
        } else {
            Window.unlockCursor();
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
}
