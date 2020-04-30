package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.*;
import com.jade.events.KeyListener;
import com.jade.physics.colliders.BoxCollider;
import com.jade.physics.particles.*;
import com.jade.physics.rigidbody.*;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

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

    private FontRenderer fpsLabel;
    private FontRenderer msLabel;
    private UIObject springVisual;
    private boolean doPhysics = false;

    Particle particle;
    Particle particle2;
    Particle particle4;
    Particle floater;
    ParticleForceRegistry particleRegistry = new ParticleForceRegistry();
    ForceRegistry forceRegistry = new ForceRegistry();

    GameObject testCube;
    GameObject testWall;
    Vector3f springStart, springEnd;

    @Override
    public void init() {
        UIObject particle1Obj = new UIObject("Particle 1", new Vector3f(585, 780, 0), new Vector3f(10, 10, 0));
        SpriteRenderer renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.COLOR4_BLUE);
        particle1Obj.addComponent(renderer);
        UIObject particle2Obj = new UIObject("Particle 2", new Vector3f(585, 800, 0), new Vector3f(10, 10, 0));
        renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.COLOR4_RED);
        particle2Obj.addComponent(renderer);
        UIObject particle3Obj = new UIObject("Particle 3", new Vector3f(800, 230, 0), new Vector3f(10, 10, 0));
        renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.COLOR4_GREEN);
        particle3Obj.addComponent(renderer);
        UIObject particle4Obj = new UIObject("Particle 4", new Vector3f(1000, 750, 0), new Vector3f(100, 200, 0));
        renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.COLOR4_YELLOW);
        particle4Obj.addComponent(renderer);

        this.springVisual = new UIObject("Spring visual", new Vector3f(750, 1000, 0), new Vector3f(1, 100, 0));
        this.springVisual.addComponent(new SpriteRenderer(new Sprite("images/defaultSprite.png")));

//        this.addUIObject(this.springVisual);
//        this.addUIObject(particle1Obj);
//        this.addUIObject(particle2Obj);
//        this.addUIObject(particle3Obj);
//        this.addUIObject(particle4Obj);

        ParticleGravity gravity = new ParticleGravity(new Vector3f(0.0f, -9.8f, 0.0f));

        particle = new Particle(new Vector3f(0), 0.1f, true);
        particle1Obj.addComponent(particle);

        particle2 = new Particle(new Vector3f(0), 10, true);
        particle2Obj.addComponent(particle2);

        floater = new Particle(new Vector3f(0), 10, true);
        particle3Obj.addComponent(floater);

        particle4 = new Particle(new Vector3f(0), 3f, true);
        particle4Obj.addComponent(particle4);

//        particleRegistry.add(floater, new ParticleBuoyancy(10.0f, 100.0f, 200.0f, 10.0f));
//        particleRegistry.add(floater, gravity);
//        particleRegistry.add(floater, new ParticleDrag(0.01f, 0.1f));

        //particleRegistry.add(particle, new ParticleBungee(particle2, 10.0f, 100.0f));
        particleRegistry.add(particle, new ParticleAnchoredSpring(particle2Obj.transform.position, 50.0f, 20f));
        //particleRegistry.add(particle2, new ParticleSpring(particle, 10.0f, 50.0f));

        particleRegistry.add(particle, gravity);
        //particleRegistry.add(particle2, gravity);
        particleRegistry.add(particle, new ParticleDrag(.47f, 1.2f));
        //particleRegistry.add(particle2, new ParticleDrag(0.1f, 0.1f));

        particleRegistry.add(particle4, new ParticleDrag(.47f, 1.2f));
        particleRegistry.add(particle4, new ParticleGroundBounce());
        particleRegistry.add(particle4, new ParticleTorqueSpring(new Vector2f(750, 1000), 0.2f));
        particleRegistry.add(particle4, gravity);

        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

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

        GameObject testLight = new GameObject("Test Light", new Transform(new Vector3f(24, 0, 19)));
        PointLight testLightComp = new PointLight(new Vector3f(1.0f, 0.95f, 0.71f), 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

        testWall = new GameObject("Test wall", new Transform(new Vector3f(18, -4, 30), new Vector3f(1), new Vector3f(0, -90, 0)));
        Model test = new Model("mesh-ext/brickWall.obj", "images/BrickPaint.png");
        test.addPointLight(testLightComp);
        testWall.addComponent(test);
        testWall.addComponent(new BoxCollider(new Vector3f(23f, 7f, 2f), new Vector3f()));
        testWall.addComponent(new Rigidbody(15.0f, 0.3f, 0.3f));
        this.addGameObject(testWall);

//        DebugDraw.addLine(new Vector3f(25, 0, 0), new Vector3f(25, 0, 10), 0.1f, Constants.COLOR3_RED, 60 * 5);

//        GameObject debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(0, 0.0f, 5)));
//        Model debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
//        debugModel.setTintColor(new Vector3f(0, 1, 0));
//        debugGizmoArrow.addComponent(debugModel);
//        debugGizmoArrow.setNonserializable();
//        this.addGameObject(debugGizmoArrow);
//
//        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(0, -1f, 4)));
//        debugGizmoArrow.transform.rotation.x = -90;
//        debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
//        debugModel.setTintColor(new Vector3f(1, 0, 0));
//        debugGizmoArrow.addComponent(debugModel);
//        debugGizmoArrow.setNonserializable();
//        this.addGameObject(debugGizmoArrow);
//
//        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(1, -1f, 5)));
//        debugGizmoArrow.transform.rotation.z = -90;
//        debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
//        debugModel.setTintColor(new Vector3f(0, 0, 1));
//        debugGizmoArrow.addComponent(debugModel);
//        debugGizmoArrow.setNonserializable();
//        this.addGameObject(debugGizmoArrow);

        testCube = new GameObject("Test Cube", new Transform(new Vector3f(32, -5, 17), new Vector3f(1), new Vector3f(0, 0, 0)));
        Model cubeModel = new Model("mesh-ext/cube.obj");
        cubeModel.addPointLight(testLightComp);
        testCube.addComponent(cubeModel);
        testCube.addComponent(new BoxCollider(new Vector3f(2), new Vector3f()));
        Rigidbody cubeRb = new Rigidbody(10.0f, 0.9f, 0.5f);
        testCube.addComponent(cubeRb);
        this.addGameObject(testCube);
        this.springEnd = new Vector3f(testCube.transform.position).add(-0.75f, 1f, 0.75f);

        GameObject otherCube = new GameObject("Other Cube", new Transform(new Vector3f(32, 0, 17), new Vector3f(0.5f)));
        otherCube.addComponent(new Model("mesh-ext/cube.obj"));
        otherCube.getComponent(Model.class).addPointLight(testLightComp);
        Rigidbody otherCubeRb = new Rigidbody(10f, 0.1f, 0.3f);
        otherCube.addComponent(otherCubeRb);
        otherCube.addComponent(new BoxCollider(new Vector3f(1), new Vector3f()));
        this.addGameObject(otherCube);
        this.springStart = new Vector3f(otherCube.transform.position).add(new Vector3f(0f, -0.5f, 0f));

        forceRegistry.add(cubeRb, new Gravity(new Vector3f(0f, -10f, 0f)));
        forceRegistry.add(cubeRb, new Spring(new Vector3f(-0.75f, 1f, 0.75f), otherCubeRb, new Vector3f(0f, -0.5f, 0f), 25f, 1f));
        forceRegistry.add(cubeRb, new Drag(-100f));

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

    private float keyDebounce = 0.2f;
    private float debounceTime = 0.2f;
    @Override
    public void update(float dt) {
        keyDebounce -= dt;
        fpsLabel.setText(String.format("FPS: %.3f", (1.0f / dt)));
        msLabel.setText(String.format("MS Last Frame: %.3f", dt * 1000.0f));

        if (doPhysics) {
            float physicsDt = 1 / 60.0f;
            particleRegistry.updateForces(physicsDt);
            forceRegistry.updateForces(physicsDt);
        }
        this.springEnd.set(testCube.transform.position).add(testCube.transform.orientation.transform(new Vector3f(-0.75f, 1f, 0.75f)));
        DebugDraw.addLine(springStart, springEnd, 0.1f, Constants.COLOR3_WHITE);

        if (!doPhysics && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && keyDebounce < 0) {
            doPhysics = true;
            keyDebounce = debounceTime;
            this.turnPhysicsOn();
        } else if (doPhysics && KeyListener.isKeyPressed(GLFW_KEY_SPACE) && keyDebounce < 0) {
            doPhysics = false;
            keyDebounce = debounceTime;
            particleRegistry.zeroForces();
            forceRegistry.zeroForces();
            this.turnPhysicsOff();
        }

        // Width 100, height 200
        Vector2f topLeftBox = new Vector2f(particle4.uiObject.transform.position.x - (particle4.uiObject.transform.scale.x / 2.0f),
                                            particle4.uiObject.transform.position.y + (particle4.uiObject.transform.scale.y / 2.0f));
        JMath.rotate(topLeftBox, particle4.uiObject.transform.rotation.z, particle4.uiObject.transform.position);
        Vector3f v2 = new Vector3f(topLeftBox.x, topLeftBox.y, 0);
        Vector3f v1 = new Vector3f(750, 1000, 0);
        Vector3f v3 = new Vector3f(v1).sub(v2);
        float length = v3.length();
        float theta = (float)Math.toDegrees(Math.atan(Math.abs(v3.y) / Math.abs(v3.x))) + 90;
        if (v3.x < 0) theta *= -1;
        if (v3.y < 0) theta *= -1;

        Vector3f center = new Vector3f(v3.x / 2.0f, v3.y / 2.0f, 0);
        springVisual.transform.scale.y = length;
        springVisual.transform.position = v1.sub(center);
        springVisual.transform.rotation.z = theta;
        //forceRegistry.updateForces(physicsDt);

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
