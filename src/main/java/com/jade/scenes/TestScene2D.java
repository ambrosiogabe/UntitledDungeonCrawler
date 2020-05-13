package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.events.MouseListener;
import com.jade.physics.rigidbody.colliders.CollisionDetector;
import com.jade.physics2d.CollisionDetector2D;
import com.jade.physics2d.primitives.Box2D;
import com.jade.physics2d.primitives.Circle;
import com.jade.renderer.Line2D;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class TestScene2D extends Scene {

    GameObject boxOne, boxTwo;
    GameObject circle;
    GameObject circle2;

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

        Sprite defaultSprite = new Sprite("images/defaultSprite.png");

        GameObject ground = new GameObject("Ground", new Transform(new Vector3f(1920f / 2.0f, 0, 0), new Vector3f(3840, 50, 0)));
        ground.addComponent(new SpriteRenderer(defaultSprite));
        ground.addComponent(new Box2D());
        this.addGameObject(ground);

        this.boxOne = new GameObject("Box One", new Transform(new Vector3f(500f, 500f, 0f), new Vector3f(64f, 64f, 0f)));
        this.boxOne.addComponent(new SpriteRenderer(defaultSprite));
        this.boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        this.boxOne.addComponent(new Box2D());
        this.addGameObject(boxOne);

        this.boxTwo = new GameObject("Box Two", new Transform(new Vector3f(550f, 500f, 0f), new Vector3f(32f, 64f, 0f)));
        this.boxTwo.addComponent(new SpriteRenderer(defaultSprite));
        this.boxTwo.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_CYAN);
        this.boxTwo.addComponent(new Box2D());
        this.addGameObject(boxTwo);

        this.circle = new GameObject("Circle", new Transform(new Vector3f(800f, 500f, 0f), new Vector3f(64, 64, 0)));
        this.circle.addComponent(new SpriteRenderer(new Sprite("images/defaultCircle.png")));
        this.circle.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        this.circle.addComponent(new Circle(32f));
        this.addGameObject(circle);

        this.circle2 = new GameObject("Circle 2", new Transform(new Vector3f(900, 500, 0), new Vector3f(32, 32, 0)));
        this.circle2.addComponent(new SpriteRenderer(new Sprite("images/defaultCircle.png")));
        this.circle2.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_CYAN);
        this.circle2.addComponent(new Circle(16f));
        this.addGameObject(circle2);
    }

    private float height = 900;
    private Vector2f end = new Vector2f(800, 800);
    @Override
    public void update(float dt) {
        Vector2f mousePos = JMath.vector2fFrom4f(MouseListener.positionScreenCoords());

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            end.x = mousePos.x;
            end.y = mousePos.y;
        }

        if (CollisionDetector2D.pointInBox2D(mousePos, boxOne.getComponent(Box2D.class))) {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
        } else {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        }

        if (CollisionDetector2D.circleAndCircle(circle.getComponent(Circle.class), circle2.getComponent(Circle.class))) {
            circle.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
            circle2.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
        } else {
            circle.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
            circle2.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_CYAN);
        }

        if (CollisionDetector2D.circleAndBox2D(circle2.getComponent(Circle.class), boxOne.getComponent(Box2D.class))) {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
        } else {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        }

        if (CollisionDetector2D.box2DAndBox2D(boxOne.getComponent(Box2D.class), boxTwo.getComponent(Box2D.class))) {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
        } else {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        }

        Line2D line = new Line2D(new Vector2f(100, 100), end);
        if (CollisionDetector2D.lineAndCircle(line, circle.getComponent(Circle.class))) {
            DebugDraw.addLine2D(new Vector2f(100, 100), end, 1f, Constants.COLOR3_GREEN);
        } else {
            DebugDraw.addLine2D(new Vector2f(100, 100), end, 1f, Constants.COLOR3_RED);
        }

        for (GameObject go : gameObjects) {
            go.update(dt);
        }
    }
}
