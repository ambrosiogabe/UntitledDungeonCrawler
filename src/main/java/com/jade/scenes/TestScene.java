package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.*;
import com.jade.events.KeyListener;
import com.jade.events.MouseListener;
import com.jade.ui.Button;
import com.jade.ui.buttons.ExitGameButton;
import com.jade.ui.buttons.PlayGameButton;
import com.jade.util.Constants;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;

public class TestScene extends Scene {
    float cubeVertices[] = {
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,  0.0f,  0.0f, -1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f,  0.0f,  0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,  0.0f,  0.0f, -1.0f,

            -0.5f, -0.5f,  0.5f, 0.0f, 0.0f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 0.0f,  0.0f,  1.0f,
            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f, 0.0f, 0.0f,  0.0f,  0.0f,  1.0f,

            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f, -1.0f,  0.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, 0.0f, 0.0f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,  0.0f, -1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, 0.0f, 0.0f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f,  0.0f,  1.0f,  0.0f
    };

    GameObject testLight;
    GameObject testWall;
    float yaw = 0.0f;
    float pitch = 0.0f;

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

        testLight = new GameObject("Test Light", new Transform(new Vector3f(10.0f, 8.0f, -10.0f)));
        PointLight testLightComp = new PointLight(Constants.WHITE, 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

        testWall = new GameObject("Test wall", new Transform(new Vector3f(0.0f, -15.0f, 0.0f)));
        Model test = new Model("mesh-ext/brickWall.fbx", "images/BrickPaint.png");
        test.addPointLight(testLightComp);
        testWall.addComponent(test);
        this.addGameObject(testWall);

        GameObject cube = new GameObject("Test Cube", new Transform(new Vector3f(10.0f, 0.0f, -12.0f)));
        Model cubeModel = new Model("mesh-ext/cube.fbx");
        cubeModel.addPointLight(testLightComp);
        cube.addComponent(cubeModel);
        this.addGameObject(cube);

        for (int i=0; i < this.gameObjects.size(); i++) {
            GameObject g = this.gameObjects.get(i);
            g.start();
        }

        for (int i=0; i < this.uiObjects.size(); i++) {
            UIObject u = this.uiObjects.get(i);
            u.start();
        }
    }

    @Override
    public void update(float dt) {
        float velocity = 50.0f * dt;
        Vector3f speedVec = new Vector3f(0.0f, 0.0f, 0.0f);

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            Window.lockCursor();
            if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                speedVec.add(camera().cameraForward());
                speedVec.mul(velocity);
                camera().transform.position.add(speedVec);
//            testLight.transform.position.y += speed * dt * 0.1f;
            } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                speedVec.add(camera().cameraForward());
                speedVec.mul(velocity);
                camera().transform.position.sub(speedVec);
//            testLight.transform.position.y -= speed * dt * 0.1f;
            } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                speedVec.add(camera().cameraRight());
                speedVec.mul(velocity);
                camera().transform.position.sub(speedVec);
//            testLight.transform.position.z += speed * dt * 0.1f;
            } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                speedVec.add(camera().cameraRight());
                speedVec.mul(velocity);
                camera().transform.position.add(speedVec);
//            testLight.transform.position.z -= speed * dt * 0.1f;
            } else if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
                testLight.transform.position.x -= velocity * 0.1f;
            } else if (KeyListener.isKeyPressed(GLFW_KEY_Q)) {
                testLight.transform.position.x += velocity * 0.1f;
            }

            float sensitivity = 0.05f;
            yaw -= MouseListener.getDx() * sensitivity;
            pitch += MouseListener.getDy() * sensitivity;

            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;

            camera().transform.rotation.x = pitch;
            camera().transform.rotation.y = yaw;
        } else {
            Window.unlockCursor();
        }

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
