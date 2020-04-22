package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.UIObject;
import com.jade.Window;
import com.jade.components.*;
import com.jade.physics.particles.*;
import com.jade.physics.rigidbody.ForceRegistry;
import com.jade.physics.rigidbody.Rigidbody;
import com.jade.util.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;

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

    Particle particle;
    Particle particle2;
    Particle particle4;
    Particle floater;
    Rigidbody body;
    ParticleForceRegistry particleRegistry = new ParticleForceRegistry();
    ForceRegistry forceRegistry = new ForceRegistry();

    @Override
    public void init() {
        UIObject particle1Obj = new UIObject("Particle 1", new Vector3f(585, 780, 0), new Vector3f(10, 10, 0));
        SpriteRenderer renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.BLUE);
        particle1Obj.addComponent(renderer);
        UIObject particle2Obj = new UIObject("Particle 2", new Vector3f(585, 800, 0), new Vector3f(10, 10, 0));
        renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.RED);
        particle2Obj.addComponent(renderer);
        UIObject particle3Obj = new UIObject("Particle 3", new Vector3f(800, 230, 0), new Vector3f(10, 10, 0));
        renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.GREEN);
        particle3Obj.addComponent(renderer);
        UIObject particle4Obj = new UIObject("Particle 4", new Vector3f(800, 500, 0), new Vector3f(10, 10, 0));
        renderer = new SpriteRenderer(new Sprite("images/defaultSprite.png"));
        renderer.setColor(Constants.YELLOW);
        particle4Obj.addComponent(renderer);
        this.addUIObject(particle1Obj);
        this.addUIObject(particle2Obj);
        this.addUIObject(particle3Obj);
        this.addUIObject(particle4Obj);

        ParticleGravity gravity = new ParticleGravity(new Vector3f(0.0f, -9.8f, 0.0f));

        particle = new Particle(particle1Obj.transform.position, new Vector3f(0), 0.1f, true);
        particle2 = new Particle(particle2Obj.transform.position, new Vector3f(0), 10, true);
        floater = new Particle(particle3Obj.transform.position, new Vector3f(0), 10, true);
        particle4 = new Particle(particle4Obj.transform.position, new Vector3f(0), 10f, true);

//        particleRegistry.add(floater, new ParticleBuoyancy(10.0f, 100.0f, 200.0f, 10.0f));
//        particleRegistry.add(floater, gravity);
//        particleRegistry.add(floater, new ParticleDrag(0.01f, 0.1f));

        //particleRegistry.add(particle, new ParticleBungee(particle2, 10.0f, 100.0f));
        particleRegistry.add(particle, new ParticleAnchoredSpring(particle2.getPosition(), 50.0f, 20f));
        //particleRegistry.add(particle2, new ParticleSpring(particle, 10.0f, 50.0f));

        particleRegistry.add(particle, gravity);
        //particleRegistry.add(particle2, gravity);
        particleRegistry.add(particle, new ParticleDrag(.47f, 1.2f));
        //particleRegistry.add(particle2, new ParticleDrag(0.1f, 0.1f));

        particleRegistry.add(particle4, new ParticleDrag(.47f, 1.2f));
        particleRegistry.add(particle4, new ParticleGroundBounce());
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

        GameObject testLight = new GameObject("Test Light", new Transform(new Vector3f(12.0f, 8.0f, -5.0f)));
        PointLight testLightComp = new PointLight(new Vector3f(1.0f, 0.95f, 0.71f), 1.0f);
        testLight.addComponent(testLightComp);
        this.addGameObject(testLight);

        GameObject testWall = new GameObject("Test wall", new Transform(new Vector3f(0.0f, 0.0f, 0.0f)));
        Model test = new Model("mesh-ext/brickWall.obj", "images/BrickPaint.png");
        test.addPointLight(testLightComp);
        testWall.addComponent(test);
        this.addGameObject(testWall);

        GameObject debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(0, 0.0f, 5)));
        Model debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
        debugModel.setTintColor(new Vector3f(0, 1, 0));
        debugGizmoArrow.addComponent(debugModel);
        debugGizmoArrow.setNonserializable();
        this.addGameObject(debugGizmoArrow);

        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(0, -1f, 4)));
        debugGizmoArrow.transform.rotation.x = -90;
        debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
        debugModel.setTintColor(new Vector3f(1, 0, 0));
        debugGizmoArrow.addComponent(debugModel);
        debugGizmoArrow.setNonserializable();
        this.addGameObject(debugGizmoArrow);

        debugGizmoArrow = new GameObject("Debug Gizmo Arrow", new Transform(new Vector3f(1, -1f, 5)));
        debugGizmoArrow.transform.rotation.z = -90;
        debugModel = new Model("mesh-ext/debugGizmo_Arrow.obj", "images/defaultSprite.png");
        debugModel.setTintColor(new Vector3f(0, 0, 1));
        debugGizmoArrow.addComponent(debugModel);
        debugGizmoArrow.setNonserializable();
        this.addGameObject(debugGizmoArrow);

        GameObject cube = new GameObject("Test Cube", new Transform(new Vector3f(10.0f, 0.0f, -12.0f)));
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

        float physicsDt = 1 / 60.0f;
        particleRegistry.updateForces(physicsDt);
        //forceRegistry.updateForces(physicsDt);
        particle.update(physicsDt);
        particle2.update(physicsDt);
        floater.update(physicsDt);
        particle4.update(physicsDt);
        //body.update(physicsDt);

        for (GameObject g : gameObjects) {
            g.update(dt);
        }

        for (UIObject u : uiObjects) {
            u.update(dt);
        }
    }
}
