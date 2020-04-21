package com.jade;

import com.jade.Component;
import imgui.ImGui;
import imgui.ImGuiInputTextData;
import imgui.enums.ImGuiCond;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class UIObject {

    private List<Component> components;

    public Transform transform;
    private String name;
    private boolean isSerializable = true;

    public UIObject(String name) {
        this.name = name;
        init(new Vector3f(0.0f), new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public UIObject(String name, Vector3f position) {
        this.name = name;
        init(position, new Vector3f(1.0f), new Vector3f(0.0f));
    }

    public UIObject(String name, Vector3f position, Vector3f scale) {
        this.name = name;
        init(position, scale, new Vector3f(0.0f));
    }

    public UIObject(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        this.name = name;
        init(position, scale, rotation);
    }

    public void init(Vector3f position, Vector3f scale, Vector3f rotation) {
        this.components = new ArrayList<>();
        this.transform = new Transform(position, scale, rotation);
    }

    public void imgui() {
        ImGui.setNextWindowSize(600, Window.getWindow().getHeight(), ImGuiCond.Appearing);
        ImGui.setNextWindowPos(Window.getWindow().getWidth() - 600, 0, ImGuiCond.Appearing);

        ImGui.begin(this.name);
        float[] xyzPosition = {this.transform.position.x, this.transform.position.y, this.transform.position.z};
        float[] xyzScale = {this.transform.scale.x, this.transform.scale.y, this.transform.scale.z};
        float[] xyzRotation = {this.transform.rotation.x, this.transform.rotation.y, this.transform.rotation.z};

        ImGui.columns(2, "columns", false);
        ImGui.setColumnWidth(0, ImGui.getFontSize() * 3);
        ImGui.text("Position: ");
        ImGui.sameLine();
        ImGui.nextColumn();
        ImGui.dragFloat3("##xyzPos", xyzPosition);

        ImGui.nextColumn();
        ImGui.text("Scale: ");
        ImGui.sameLine();
        ImGui.nextColumn();
        ImGui.dragFloat3("##xyzScale", xyzScale);

        ImGui.nextColumn();
        ImGui.text("Rotation: ");
        ImGui.sameLine();
        ImGui.nextColumn();
        ImGui.dragFloat3("##xyzRotation", xyzRotation);
        ImGui.columns(1);

        this.transform.position.set(xyzPosition);
        this.transform.scale.set(xyzScale);
        this.transform.rotation.set(xyzRotation);
        ImGui.separator();

        for (int i=0; i < this.components.size(); i++) {
            this.components.get(i).imgui();
        }

        ImGui.end();
    }

    public void update(float dt) {
        for (int i=0; i < this.components.size(); i++) {
            this.components.get(i).update(dt);
        }
    }

    public void start() {
        for (int i=0; i < this.components.size(); i++) {
            this.components.get(i).start();
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

    public void setNonSerializable() {
        this.isSerializable = false;
    }

    public boolean isSerializable() {
        return this.isSerializable;
    }

    public void addComponent(Component c) {
        components.add(c);
        c.uiObject = this;
    }
}
