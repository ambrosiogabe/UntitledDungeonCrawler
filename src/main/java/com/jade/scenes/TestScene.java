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
    GameObject debugGizmoArrow;
    GameObject cube;
    FontRenderer fpsLabel;
    FontRenderer msLabel;
    float yaw = 0.0f;
    float pitch = 0.0f;

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

        UIObject fps = new UIObject(new Vector3f(10, 1010, 0));
        fpsLabel = new FontRenderer(Constants.DEBUG_FONT, "FPS: ");
        fps.addComponent(fpsLabel);
        this.addUIObject(fps);

        UIObject ms = new UIObject(new Vector3f(10, 1040, 0));
        msLabel = new FontRenderer(Constants.DEBUG_FONT, "MS Last Frame: ");
        ms.addComponent(msLabel);
        this.addUIObject(ms);

        testLight = new GameObject("Test Light", new Transform(new Vector3f(12.0f, 8.0f, -5.0f)));
        PointLight testLightComp = new PointLight(new Vector3f(1.0f, 0.95f, 0.71f), 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

        testWall = new GameObject("Test wall", new Transform(new Vector3f(0.0f, 0.0f, 0.0f)));
        Model test = new Model("mesh-ext/brickWall.obj", "images/BrickPaint.png");
        test.addPointLight(testLightComp);
        testWall.addComponent(test);
        this.addGameObject(testWall);

        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(0, 0.0f, 5)));
        Model debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
        debugModel.setTintColor(new Vector3f(0, 1, 0));
        debugGizmoArrow.addComponent(debugModel);
        this.addGameObject(debugGizmoArrow);

        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(0, -1f, 4)));
        debugGizmoArrow.transform.rotation.x = -90;
        debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
        debugModel.setTintColor(new Vector3f(1, 0, 0));
        debugGizmoArrow.addComponent(debugModel);
        this.addGameObject(debugGizmoArrow);

        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(1, -1f, 5)));
        debugGizmoArrow.transform.rotation.z = -90;
        debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
        debugModel.setTintColor(new Vector3f(0, 0, 1));
        debugGizmoArrow.addComponent(debugModel);
        this.addGameObject(debugGizmoArrow);

        cube = new GameObject("Test Cube", new Transform(new Vector3f(10.0f, 0.0f, -12.0f)));
        Model cubeModel = new Model("mesh-ext/cube.obj");
        cubeModel.addPointLight(testLightComp);
        cube.addComponent(cubeModel);
        this.addGameObject(cube);

        GameObject cameraController = new GameObject("Camera Controller", new Transform());
        cameraController.addComponent(new FlyingCameraController());
        cameraController.setNonserializable();
        this.addGameObject(cameraController);

        GameObject debugKeyController = new GameObject("Debug Key Controller", new Transform());
        debugKeyController.addComponent(new DebugKeyController());
        debugKeyController.setNonserializable();
        this.addGameObject(debugKeyController);

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
        fpsLabel.setText(String.format("FPS: %.3f", (1.0f / dt)));
        msLabel.setText(String.format("MS Last Frame: %.3f", dt * 1000.0f));

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
