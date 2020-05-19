package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.Window;
import com.jade.components.*;
import com.jade.events.KeyListener;
import com.jade.physics.primitives.Box;
import com.jade.physics.primitives.IntersectionTester;
import com.jade.physics.primitives.Sphere;
import com.jade.physics.rigidbody.Rigidbody;
import com.jade.physics.rigidbody.boundingVolumes.BoundingSphere;
import com.jade.physics.rigidbody.colliders.BoxCollider;
import com.jade.physics.rigidbody.colliders.Plane;
import com.jade.physics.rigidbody.colliders.SphereCollider;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class TestScene3D extends Scene {
    private FontRenderer fpsLabel;
    private FontRenderer msLabel;

    GameObject sphere, cubeOne, cubeTwo, plane;

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 0.0f;

        GameObject fps = new GameObject("FPS Label", new Transform(new Vector3f(10, 1010, 0)));
        fpsLabel = new FontRenderer(Constants.DEBUG_FONT, "FPS: ");
        fps.addComponent(fpsLabel);
        fps.setNonserializable();
        this.addGameObject(fps);

        GameObject ms = new GameObject("MS Label", new Transform(new Vector3f(10, 1040, 0)));
        msLabel = new FontRenderer(Constants.DEBUG_FONT, "MS Last Frame: ");
        ms.addComponent(msLabel);
        ms.setNonserializable();
        this.addGameObject(ms);

        GameObject testLight = new GameObject("Test Light", new Transform(new Vector3f(21, 7, -10)));
        PointLight testLightComp = new PointLight(new Vector3f(1.0f, 0.95f, 0.71f), 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

        cubeOne = new GameObject("Cube 1", new Transform(new Vector3f(32, -5, 17), new Vector3f(1f)));
        Model cubeModel = new Model("mesh-ext/cube.obj");
        cubeModel.addPointLight(testLightComp);
        cubeOne.addComponent(cubeModel);
        cubeOne.addComponent(new Box(new Vector3f(1f)));
        this.addGameObject(cubeOne);

        cubeTwo = new GameObject("Other Cube", new Transform(new Vector3f(40, -5, 17), new Vector3f(1.25f)));
        cubeTwo.addComponent(new Model("mesh-ext/cube.obj"));
        cubeTwo.getComponent(Model.class).addPointLight(testLightComp);
        cubeTwo.addComponent(new Box(new Vector3f(1.25f)));
        this.addGameObject(cubeTwo);

        plane = new GameObject("Plane", new Transform(new Vector3f(30, -6, 4), new Vector3f(50, 50, 1), new Vector3f(90, 0, 0)));
        plane.addComponent(new Model("mesh-ext/plane.obj"));
        plane.getComponent(Model.class).addPointLight(testLightComp);
        this.addGameObject(plane);

        sphere = new GameObject("Sphere", new Transform(new Vector3f(30, -2, 15), new Vector3f(0.05f)));
        sphere.addComponent(new Model("mesh-ext/sphere.obj"));
        sphere.getComponent(Model.class).addPointLight(testLightComp);
        sphere.addComponent(new Sphere(0.05f));
        this.addGameObject(sphere);

        GameObject cameraController = new GameObject("Camera Controller", new Transform());
        cameraController.addComponent(new FlyingCameraController());
        cameraController.setNonserializable();
        this.addGameObject(cameraController);
    }

    private int labelFrame = 0;
    @Override
    public void update(float dt) {
        labelFrame--;
        if (labelFrame < 0) {
            fpsLabel.setText(String.format("FPS: %.3f", (1.0f / dt)));
            msLabel.setText(String.format("MS Last Frame: %.3f", dt * 1000.0f));
            labelFrame = 5;
        }

        if (IntersectionTester.pointInBox(sphere.transform.position, cubeOne.getComponent(Box.class))) {
            Vector3f dir = new Vector3f(sphere.transform.position).sub(cubeOne.transform.position);
            dir.normalize();
            DebugDraw.addLine(cubeOne.transform.position, new Vector3f(sphere.transform.position).add(dir.mul(2f)));
        }

        for (GameObject g : gameObjects) {
            g.update(dt);
        }
    }
}