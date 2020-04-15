package com.jade;

import com.jade.Component;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class UIObject {

    private List<Component> components;

    public Transform transform;

    public UIObject() {
        init(new Vector3f(0.0f), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public UIObject(Vector3f position) {
        init(position, new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public UIObject(Vector3f position, Vector3f scale) {
        init(position, scale, new Vector3f(0.0f));
    }

    public UIObject(Vector3f position, Vector3f scale, Vector3f rotation) {
        init(position, scale, rotation);
    }

    public void init(Vector3f position, Vector3f scale, Vector3f rotation) {
        this.components = new ArrayList<>();
        this.transform = new Transform(position, scale, rotation);
    }

    public void update(float dt) {
        for (Component c : this.components) {
            c.update(dt);
        }
    }

    public void start() {
        for (Component c : this.components) {
            c.start();
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(c);
                return;
            }
        }
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void addComponent(Component c) {
        components.add(c);
        c.uiObject = this;
    }
}
