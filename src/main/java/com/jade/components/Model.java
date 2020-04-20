package com.jade.components;

import com.jade.Component;
import com.jade.Window;
import com.jade.renderer.Mesh;
import com.jade.renderer.Shader;
import com.jade.renderer.Texture;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Model extends Component {

    private Matrix4f modelMatrix;
    private Shader shader = AssetPool.getShader("shaders/defaultObj.glsl");
    private Texture texture = null;
    private Mesh mesh;
    private Vector3f tintColor;

    private PointLight[] pointLights = new PointLight[4];

    public Model(String resourceName, String texResource) {
        this.mesh = Mesh.loadVTNMesh(resourceName);
        this.modelMatrix = new Matrix4f();
        this.texture = AssetPool.getTexture(texResource);
        this.tintColor = new Vector3f(1, 1, 1);
    }

    public Model(String resourceName) {
        this.mesh = Mesh.loadVTNMesh(resourceName);
        this.modelMatrix = new Matrix4f();
    }

    public void clear() {
        mesh.clear();
    }

    private void calculateModelMatrix() {
        this.modelMatrix.identity();

        this.modelMatrix.translate(this.gameObject.transform.position);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.x), Constants.RIGHT);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.y), Constants.UP);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.z), Constants.FORWARD);
        this.modelMatrix.scale(this.gameObject.transform.scale);
    }

    public Matrix4f getModelMatrix() {
        return this.modelMatrix;
    }

    public void addPointLight(PointLight light) {
        for (int i=0; i < this.pointLights.length; i++) {
            if (pointLights[i] == null) {
                pointLights[i] = light;
            }
        }
    }

    public void render() {
        calculateModelMatrix();

        shader.use();

        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        shader.uploadMat4f("uModel", this.modelMatrix);
        shader.uploadVec3f("uViewPos", Window.getScene().camera().transform.position);
        if (texture != null) {
            shader.uploadTexture("uTexture", 0);
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
            shader.uploadInt("uUseTexture", 1);
            shader.uploadVec3f("uTint", this.tintColor);
        } else {
            shader.uploadInt("uUseTexture", 0);
        }

        if (pointLights[0] != null) {
            shader.uploadInt("uUseLight", 1);
            shader.uploadVec3f("uLightPos", pointLights[0].gameObject.transform.position);
            shader.uploadVec3f("uLightColor", pointLights[0].getColor());
        } else {
            shader.uploadInt("uUseLight", 0);
        }

        mesh.render();

        if (texture != null) {
            texture.unbind();
        }
        shader.detach();
    }

    @Override
    public Component copy() {
        return null;
    }

    @Override
    public String serialize(int tabSize) {
        return null;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void setTintColor(Vector3f color) {
        this.tintColor = color;
    }
}
