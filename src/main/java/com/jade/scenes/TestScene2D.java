package com.jade.scenes;

import com.jade.GameObject;
import com.jade.Transform;
import com.jade.Window;
import com.jade.components.Sprite;
import com.jade.components.SpriteRenderer;
import com.jade.events.MouseListener;
import com.jade.physics2d.CollisionDetector2D;
import com.jade.physics2d.primitives.Box2D;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import com.jade.util.JMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestScene2D extends Scene {

    GameObject boxOne;

    @Override
    public void init() {
        Window.getScene().camera().transform.position.z = -5.0f;
        Window.getScene().camera().transform.position.x = 25.0f;
        Window.getScene().camera().transform.rotation.y = 90.0f;

        Sprite defaultSprite = new Sprite("images/defaultSprite.png");

        GameObject ground = new GameObject("Ground", new Transform(new Vector3f(1920f / 2.0f, 0, 0), new Vector3f(3840, 50, 0)));
        ground.addComponent(new SpriteRenderer(defaultSprite));
        this.addGameObject(ground);

        boxOne = new GameObject("Box One", new Transform(new Vector3f(500f, 500f, 0f), new Vector3f(64f, 64f, 0f)));
        boxOne.addComponent(new SpriteRenderer(defaultSprite));
        boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        boxOne.addComponent(new Box2D());
        this.addGameObject(boxOne);
    }

    private float height = 900;
    @Override
    public void update(float dt) {
        Vector2f mousePos = JMath.vector2fFrom4f(MouseListener.positionScreenCoords());
        if (CollisionDetector2D.pointInBox2D(mousePos, boxOne.getComponent(Box2D.class))) {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_GREEN);
        } else {
            boxOne.getComponent(SpriteRenderer.class).setColor(Constants.COLOR4_RED);
        }


        DebugDraw.addLine2D(new Vector2f(100, 100), new Vector2f(800, height), 10f, Constants.COLOR3_PURPLE);
        height -= 90f * dt;

        for (GameObject go : gameObjects) {
            go.update(dt);
        }
    }
}
