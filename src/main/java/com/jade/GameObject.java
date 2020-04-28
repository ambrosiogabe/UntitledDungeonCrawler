package com.jade;

import com.jade.events.KeyListener;
import com.jade.file.Parser;
import com.jade.file.Serialize;
import com.jade.util.Constants;
import com.jade.util.DebugDraw;
import imgui.ImBool;
import imgui.ImGui;
import imgui.enums.ImGuiCond;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class GameObject extends Serialize {
    private List<Component> components;

    public Transform transform;

    private Transform lastTransform;
    private String name;
    private boolean serializable = true;
    private boolean isVisible = true;

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        this.components = new ArrayList<>();
        this.lastTransform = new Transform();
        Transform.copyValues(this.transform, this.lastTransform);
    }

    public String getName() {
        return this.name;
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
        c.gameObject = this;
    }

    public GameObject copy() {
        GameObject newGameObject = new GameObject(this.name, transform.copy());
        for (Component c : components) {
            Component copy = c.copy();
            if (copy != null) {
                newGameObject.addComponent(copy);
            }
        }

        newGameObject.start();

        return newGameObject;
    }


    public void update(float dt) {
        for (int i=0; i < this.components.size(); i++) {
            Component c = this.components.get(i);
            c.update(dt);
        }
    }

    public void setNonserializable() {
        serializable = false;
    }

    public boolean isSerializable() {
        return this.serializable;
    }

    public void imgui() {
        ImGui.labelText("##gameObjectName", this.name);

        float[] xyzPosition = {this.transform.position.x, this.transform.position.y, this.transform.position.z};
        float[] xyzScale = {this.transform.scale.x, this.transform.scale.y, this.transform.scale.z};
        float[] xyzRotation = {this.transform.rotation.x, this.transform.rotation.y, this.transform.rotation.z};

        ImGui.columns(2, "columns", false);
        ImGui.setColumnWidth(0, ImGui.getFontSize() * 3);
        ImGui.text("Position: ");
        ImGui.sameLine();
        ImGui.nextColumn();
        ImGui.dragFloat3("##xyzPos", xyzPosition, KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) ? 0.05f : 0.1f);

        ImGui.nextColumn();
        ImGui.text("Scale: ");
        ImGui.sameLine();
        ImGui.nextColumn();
        ImGui.dragFloat3("##xyzScale", xyzScale,  KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) ? 0.05f : 0.1f);

        ImGui.nextColumn();
        ImGui.text("Rotation: ");
        ImGui.sameLine();
        ImGui.nextColumn();
        ImGui.dragFloat3("##xyzRotation", xyzRotation,  KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) ? 0.05f : 0.1f);
        ImGui.columns(1);

        ImBool imVisible = new ImBool(isVisible);
        if (ImGui.checkbox("Is Visible", imVisible)) {
            isVisible = !isVisible;
        }

        this.transform.position.set(xyzPosition);
        this.transform.scale.set(xyzScale);
        if (!this.transform.rotation.equals(xyzRotation[0], xyzRotation[1], xyzRotation[2])) {
            this.transform.rotation.set(xyzRotation);
            this.transform.orientation.set(0, 0, 0, 1);
            this.transform.orientation.rotateAxis((float)Math.toRadians(this.transform.rotation.x), Constants.RIGHT);
            this.transform.orientation.rotateAxis((float)Math.toRadians(this.transform.rotation.y), Constants.UP);
            this.transform.orientation.rotateAxis((float)Math.toRadians(this.transform.rotation.z), Constants.FORWARD);
            this.transform.orientation.positiveZ(this.transform.forward).negate();
            Transform.copyValues(this.transform, this.lastTransform);
        } else if (!this.transform.orientation.equals(this.lastTransform.orientation)) {
            Transform.copyValues(this.transform, this.lastTransform);
            this.transform.orientation.getEulerAnglesXYZ(this.transform.rotation);
            this.transform.orientation.positiveZ(this.transform.forward).negate();
        }
        ImGui.separator();
        //ImGui.endMenu();

        for (int i=0; i < this.components.size(); i++) {
            if (ImGui.collapsingHeader(this.components.get(i).getClass().getSimpleName())) {
                this.components.get(i).imgui();
                ImGui.separator();
            }
        }
    }

    public void start() {
        for (int i=0; i < this.components.size(); i++) {
            Component c = this.components.get(i);
            c.start();
        }
    }

    public void drawGizmo() {
        DebugDraw.addLine(this.transform.position, new Vector3f(this.transform.position).add(new Vector3f(this.transform.forward).mul(10)), 0.1f, Constants.COLOR3_RED);

        for (int i=0; i < this.components.size(); i++) {
            Component c = this.components.get(i);
            c.drawGizmo();
        }
    }

    @Override
    public String serialize(int tabSize) {
        if (!serializable) return "";

        StringBuilder builder = new StringBuilder();
        // Game Object
        builder.append(beginObjectProperty("GameObject", tabSize));

        // Transform
        builder.append(transform.serialize(tabSize + 1));
        builder.append(addEnding(true, true));

        // Name
        if (components.size() > 0) {
            builder.append(addStringProperty("Name", name, tabSize + 1, true, true));
            builder.append(beginObjectProperty("Components", tabSize + 1));
        } else {
            builder.append(addStringProperty("Name", name, tabSize + 1, true, false));
        }

        int i = 0;
        for (Component c : components) {
            String str = c.serialize(tabSize + 2);
            if (str.compareTo("") != 0) {
                builder.append(str);
                if (i != components.size() - 1) {
                    builder.append(addEnding(true, true));
                } else {
                    builder.append(addEnding(true, false));
                }
            }
            i++;
        }

        if (components.size() > 0) {
            builder.append(closeObjectProperty(tabSize + 1));
        }

        builder.append(addEnding(true, false));
        builder.append(closeObjectProperty(tabSize));

        return builder.toString();
    }

    public static GameObject deserialize() {
        Parser.consumeBeginObjectProperty("GameObject");

        Transform transform = Transform.deserialize();
        Parser.consume(',');
        String name = Parser.consumeStringProperty("Name");

        GameObject go = new GameObject(name, transform);

        if (Parser.peek() == ',') {
            Parser.consume(',');
            Parser.consumeBeginObjectProperty("Components");
            go.addComponent(Parser.parseComponent());

            while (Parser.peek() == ',') {
                Parser.consume(',');
                go.addComponent(Parser.parseComponent());
            }
            Parser.consumeEndObjectProperty();
        }
        Parser.consumeEndObjectProperty();

        return go;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
