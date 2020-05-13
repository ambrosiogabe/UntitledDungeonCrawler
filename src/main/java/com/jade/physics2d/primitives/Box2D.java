package com.jade.physics2d.primitives;

import com.jade.Component;
import com.jade.util.JMath;
import org.joml.Vector2f;

public class Box2D extends Component {



    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public Vector2f getMin() {
        return JMath.vector2fFrom3f(this.gameObject.transform.position).sub(JMath.vector2fFrom3f(this.gameObject.transform.scale).div(2.0f));
    }

    public Vector2f getMax() {
        return JMath.vector2fFrom3f(this.gameObject.transform.position).add(JMath.vector2fFrom3f(this.gameObject.transform.scale).div(2.0f));
    }

    public Vector2f getHalfSize() {
        return new Vector2f(this.gameObject.transform.scale.x / 2.0f, this.gameObject.transform.scale.y / 2.0f);
    }

    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        for (Vector2f vert : vertices) {
            JMath.rotate(vert, this.gameObject.transform.rotation.z, this.gameObject.transform.position);
        }

        return vertices;
    }
}
