package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.Window;
import com.jade.components.*;
import com.jade.events.KeyListener;
import com.jade.physics.PhysicsSystem;
import com.jade.physics.forces.AnchoredRod;
import com.jade.physics.forces.Spring;
import com.jade.physics.rigidbody.Rigidbody;
import com.jade.physics.primitives.Box;
import com.jade.physics.primitives.Sphere;
import com.jade.physics2d.rigidbody.Rigidbody2D;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class TestScene3D extends Scene {
    private FontRenderer fpsLabel;
    private FontRenderer msLabel;

    GameObject sphere, cubeOne, cubeTwo, plane, groundCube;
    PhysicsSystem testPhysics = new PhysicsSystem();

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 0.0f;
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

        GameObject testLight = new GameObject("Test Light", new Transform(new Vector3f(12.9f, 7, -10)));
        PointLight testLightComp = new PointLight(new Vector3f(1.0f, 0.95f, 0.71f), 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

//        cubeOne = new GameObject("Cube 1", new Transform(new Vector3f(35.1f, 0f, 17), new Vector3f(1f)));
//        Model cubeModel = new Model("mesh-ext/cube.obj");
//        cubeModel.addPointLight(testLightComp);
//        cubeOne.addComponent(cubeModel);
//        cubeOne.addComponent(new Box(new Vector3f(2f)));
//        cubeOne.addComponent(new Rigidbody(5f));
//        this.addGameObject(cubeOne);
//
//        testPhysics.addGameObject(cubeOne);
//        testPhysics.addForceRegistration(cubeOne.getComponent(Rigidbody.class),
//                new Spring(new Vector3f(0, 1, 0), new Vector3f(42, 10, 17), 30f, 10f));

//        cubeTwo = new GameObject("Other Cube", new Transform(new Vector3f(40, -5, 17), new Vector3f(1.25f)));
//        cubeTwo.addComponent(new Model("mesh-ext/cube.obj"));
//        cubeTwo.getComponent(Model.class).addPointLight(testLightComp);
//        cubeTwo.addComponent(new Box(new Vector3f(2.5f)));
//        cubeTwo.addComponent(new Rigidbody(0.1f));
//        this.addGameObject(cubeTwo);
//
//        testPhysics.addGameObject(cubeTwo);

//        plane = new GameObject("Plane", new Transform(new Vector3f(30, -6, 4), new Vector3f(50, 50, 1), new Vector3f(90, 0, 0)));
//        plane.addComponent(new Model("mesh-ext/plane.obj"));
//        plane.getComponent(Model.class).addPointLight(testLightComp);
//        plane.addComponent(new Plane(new Vector3f(0, 1, 0), -6));
//        plane.addComponent(new Rigidbody(0f));
//        this.addGameObject(plane);
//
//        testPhysics.addRigidbody(plane.getComponent(Rigidbody.class));
        groundCube = new GameObject("Ground Cube", new Transform(new Vector3f(30, -7.25f, 4), new Vector3f(50, 1, 50)));
        groundCube.addComponent(new Model("mesh-ext/cube.obj"));
        groundCube.getComponent(Model.class).addPointLight(testLightComp);
        groundCube.addComponent(new Box(new Vector3f(100f, 2f, 100f)));
        groundCube.addComponent(new Rigidbody(0f));
        this.addGameObject(groundCube);

        testPhysics.addGameObject(groundCube);

//        sphere = new GameObject("Sphere", new Transform(new Vector3f(30, -2, 15), new Vector3f(1f)));
//        sphere.addComponent(new Model("mesh-ext/sphere.obj"));
//        sphere.getComponent(Model.class).addPointLight(testLightComp);
//        sphere.addComponent(new Sphere(1f));
//        sphere.addComponent(new Rigidbody(1f));
//        this.addGameObject(sphere);
//
//        for (int i=0; i < 10; i++) {
//            GameObject particle = new GameObject("Particle" + i, new Transform(new Vector3f(31.9f, 0 + i * (0.2f), 16.9f + i * (0.1f)), new Vector3f(0.1f)));
//            particle.addComponent(new Model("mesh-ext/sphere.obj"));
//            particle.getComponent(Model.class).addPointLight(testLightComp);
//            particle.addComponent(new Sphere(0.1f));
//            particle.addComponent(new Rigidbody(1f));
//            this.addGameObject(particle);
//            testPhysics.addGameObject(particle);
//        }

//        sphere = new GameObject("Sphere", new Transform(new Vector3f(5, 8f, -7f)));
//        sphere.addComponent(new Model("mesh-ext/sphere.obj"));
//        sphere.getComponent(Model.class).addPointLight(testLightComp);
//        sphere.addComponent(new Rigidbody(15f));
//        sphere.addComponent(new Sphere(1f));
//        this.addGameObject(sphere);
//        testPhysics.addGameObject(sphere);
//        testPhysics.addForceRegistration(sphere.getComponent(Rigidbody.class), new AnchoredRod(
//                new Vector3f(0, 1, 0),
//                new Vector3f(20f, 10f, -7f)));
//
//
//        for (int i=0; i < 7; i++) {
//            for (int j=0; j < i - 1; j++) {
//                float x = 20;
//                float y = (j * 2.1f) - (5);
//                float z = (i * 2.1f) - (j * 1.05f) - (15);
//                GameObject cube = new GameObject("Cube" + i + "" + j, new Transform(new Vector3f(x, y, z)));
//                cube.addComponent(new Model("mesh-ext/cube.obj"));
//                cube.getComponent(Model.class).addPointLight(testLightComp);
//                cube.addComponent(new Rigidbody(0.1f));
//                cube.addComponent(new Box(new Vector3f(2f, 2f, 2f)));
//                this.addGameObject(cube);
//                testPhysics.addGameObject(cube);
//            }
//        }

        for (int i=0; i < 15; i++) {
            float x = 20;
            float y = -4f;
            float z = (i * 2.1f) - (15f);
            float thetaX = 0f;
            float thetaY = 0f;
            float thetaZ = 0f;
            if (i == 0) {
                thetaX = 15f;
            }
            GameObject cube = new GameObject("Cube" + i + "", new Transform(new Vector3f(x, y, z), new Vector3f(1f, 2, 0.3f),
            new Vector3f(thetaX, thetaY, thetaZ)));
            cube.addComponent(new Model("mesh-ext/cube.obj"));
            cube.getComponent(Model.class).addPointLight(testLightComp);
            cube.addComponent(new Rigidbody(1f));
            cube.addComponent(new Box(new Vector3f(2f, 4f, 0.6f)));
            this.addGameObject(cube);
            testPhysics.addGameObject(cube);
        }

        GameObject cameraController = new GameObject("Camera Controller", new Transform());
        cameraController.addComponent(new FlyingCameraController());
        cameraController.setNonserializable();
        this.addGameObject(cameraController);
    }

    private int labelFrame = 0;
    private boolean runPhysics = false;
    @Override
    public void update(float dt) {
        labelFrame--;
        if (labelFrame < 0) {
            fpsLabel.setText(String.format("FPS: %.3f", (1.0f / dt)));
            msLabel.setText(String.format("MS Last Frame: %.3f", dt * 1000.0f));
            labelFrame = 5;
        }

//        DebugDraw.addLine(new Vector3f(cubeOne.transform.position).add(new Vector3f(cubeOne.transform.up).mul(1)),
//                new Vector3f(42, 10, 17), 0.05f, new Vector3f(0.5f, 0.5f, 0.5f));
//        DebugDraw.addLine(new Vector3f(sphere.transform.position).add(new Vector3f(sphere.transform.up).mul(1)),
//                new Vector3f(20f, 10f, -7f), 0.05f, new Vector3f(0.5f, 0.5f, 0.5f));

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            runPhysics = true;
        }
        if (runPhysics) {
            testPhysics.update(1f / 60f);
        }
        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        if (activeGameObject >= 0) {
            gameObjects.get(activeGameObject).drawGizmo();
        }
    }
}
