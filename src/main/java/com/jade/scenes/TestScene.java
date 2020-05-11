package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.*;
import com.jade.events.KeyListener;
import com.jade.physics.rigidbody.boundingVolumes.BoundingSphere;
import com.jade.physics.rigidbody.colliders.BoxCollider;
import com.jade.physics.particles.*;
import com.jade.physics.rigidbody.*;
import com.jade.physics.rigidbody.colliders.Plane;
import com.jade.physics.rigidbody.colliders.SphereCollider;
import com.jade.physics.rigidbody.collisions.BVHNode;
import com.jade.physics.rigidbody.collisions.CollisionData;
import com.jade.physics.rigidbody.collisions.ContactResolver;
import com.jade.physics.rigidbody.collisions.PotentialContact;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class TestScene extends Scene {
    private FontRenderer fpsLabel;
    private FontRenderer msLabel;
    private boolean doPhysics = false;

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 0.0f;

        UIObject fps = new UIObject("FPS Label", new Vector3f(10, 1010, 0));
        fpsLabel = new FontRenderer(Constants.DEBUG_FONT, "FPS: ");
        fps.addComponent(fpsLabel);
        fps.setNonSerializable();
        this.addUIObject(fps);

        UIObject ms = new UIObject("MS Label", new Vector3f(10, 1040, 0));
        msLabel = new FontRenderer(Constants.DEBUG_FONT, "MS Last Frame: ");
        ms.addComponent(msLabel);
        ms.setNonSerializable();
        this.addUIObject(ms);

        GameObject testLight = new GameObject("Test Light", new Transform(new Vector3f(24, 6, 19)));
        PointLight testLightComp = new PointLight(new Vector3f(1.0f, 0.95f, 0.71f), 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

//        testWall = new GameObject("Test wall", new Transform(new Vector3f(18, -4, 30), new Vector3f(1), new Vector3f(0, -90, 0)));
//        Model test = new Model("mesh-ext/brickWall.obj", "images/BrickPaint.png");
//        test.addPointLight(testLightComp);
//        testWall.addComponent(test);
//        testWall.addComponent(new BoxCollider(new Vector3f(23f, 7f, 2f), new Vector3f()));
//        testWall.addComponent(new Rigidbody(15.0f, 0.3f, 0.3f));
//        this.addGameObject(testWall);

        GameObject testCube = new GameObject("Test Cube", new Transform(new Vector3f(32, -5, 17), new Vector3f(1), new Vector3f(0, 0, 0)));
        Model cubeModel = new Model("mesh-ext/cube.obj");
        cubeModel.addPointLight(testLightComp);
        testCube.addComponent(cubeModel);
        testCube.addComponent(new BoxCollider(new Vector3f(2), new Vector3f()));
        Rigidbody cubeRb = new Rigidbody(10.0f, 0.9f, 0.5f);
        testCube.addComponent(cubeRb);
        this.addGameObject(testCube);
        //this.springEnd = new Vector3f(testCube.transform.position).add(-0.75f, 1f, 0.75f);

        GameObject otherCube = new GameObject("Other Cube", new Transform(new Vector3f(32, 0, 17), new Vector3f(0.5f)));
        otherCube.addComponent(new Model("mesh-ext/cube.obj"));
        otherCube.getComponent(Model.class).addPointLight(testLightComp);
        Rigidbody otherCubeRb = new Rigidbody(1f, 0.1f, 0.1f);
        otherCube.addComponent(otherCubeRb);
        otherCube.addComponent(new BoxCollider(new Vector3f(1), new Vector3f()));
        this.addGameObject(otherCube);
        //this.springStart = new Vector3f(otherCube.transform.position).add(new Vector3f(0f, -0.5f, 0f));


        // =========================================================================================================
        // BVH test
        // =========================================================================================================
        for (int i=0; i < 5; i++) {
            for (int j=i; j < 5 - i; j++) {
                GameObject cube0 = new GameObject("Cube" + i + "" + j, new Transform(new Vector3f(j * 2.5f, i * 2.5f, 20)));
                cube0.addComponent(new Model("mesh-ext/cube.obj"));
                cube0.getComponent(Model.class).addPointLight(testLightComp);
                cube0.addComponent(new BoundingSphere(cube0.transform.position, 1.5f));
                cube0.addComponent(new Rigidbody(10, 0.1f, 0.1f));
                BoxCollider boxCollider0 = new BoxCollider(new Vector3f(2, 2, 2), new Vector3f());
                cube0.addComponent(boxCollider0);
                this.addGameObject(cube0);
            }
        }

        GameObject plane = new GameObject("Plane", new Transform(new Vector3f(30, -6, 4), new Vector3f(50, 50, 1), new Vector3f(90, 0, 0)));
        plane.addComponent(new Model("mesh-ext/plane.obj"));
        plane.getComponent(Model.class).addPointLight(testLightComp);
        plane.addComponent(new BoundingSphere(plane.transform.position, plane.transform.scale.x));
        plane.addComponent(new Rigidbody(1, 0.1f, 0.1f, true));
        Plane planeCollider = new Plane(new Vector3f(Constants.UP), 0);
        plane.addComponent(planeCollider);
        this.addGameObject(plane);

        GameObject sphere = new GameObject("Sphere", new Transform(new Vector3f(5, -2, 8)));
        sphere.addComponent(new Model("mesh-ext/sphere.obj"));
        sphere.getComponent(Model.class).addPointLight(testLightComp);
        sphere.addComponent(new BoundingSphere(sphere.transform.position, 1));
        sphere.addComponent(new Rigidbody(100, 0.1f, 0.1f));
        SphereCollider sphereCollider = new SphereCollider(1);
        sphere.addComponent(sphereCollider);

        this.addGameObject(sphere);

        // =========================================================================================================
        // =========================================================================================================

//        forceRegistry.add(cubeRb, new Gravity(new Vector3f(0f, -10f, 0f)));
//        forceRegistry.add(cubeRb, new Spring(new Vector3f(-0.75f, 1f, 0.75f), otherCubeRb, new Vector3f(0f, -0.5f, 0f), 25f, 1f));
//        forceRegistry.add(cubeRb, new Drag(-100f));
//        forceRegistry.add(cube0.getComponent(Rigidbody.class), new Gravity(new Vector3f(0f, -15f, 0f)));

        GameObject cameraController = new GameObject("Camera Controller", new Transform());
        cameraController.addComponent(new FlyingCameraController());
        cameraController.setNonserializable();
        this.addGameObject(cameraController);

        GameObject debugKeyController = new GameObject("Debug Key Controller", new Transform());
        debugKeyController.addComponent(new DebugKeyController());
        debugKeyController.setNonserializable();
        this.addGameObject(debugKeyController);
    }

    private float keyDebounce = 0.2f;
    private float debounceTime = 0.2f;
    private int labelFrame = 0;
    @Override
    public void update(float dt) {
        keyDebounce -= dt;
        labelFrame--;
        if (labelFrame < 0) {
            fpsLabel.setText(String.format("FPS: %.3f", (1.0f / dt)));
            msLabel.setText(String.format("MS Last Frame: %.3f", dt * 1000.0f));
            labelFrame = 5;
        }

        if (doPhysics) {
            physics.update(dt);
        }
//        this.springEnd.set(testCube.transform.position).add(testCube.transform.orientation.transform(new Vector3f(-0.75f, 1f, 0.75f)));
//        DebugDraw.addLine(springStart, springEnd, 0.1f, Constants.COLOR3_WHITE);

        if (!doPhysics && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && keyDebounce < 0) {
            doPhysics = true;
            keyDebounce = debounceTime;
            this.turnPhysicsOn();
        } else if (doPhysics && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && keyDebounce < 0) {
            doPhysics = false;
            keyDebounce = debounceTime;
            this.turnPhysicsOff();
            physics.zeroForces();
        }

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }

        if (activeGameObject >= 0) {
            gameObjects.get(activeGameObject).drawGizmo();
        }
    }
}
