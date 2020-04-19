package com.jade.components;

import com.jade.Component;
import com.jade.Window;
import com.jade.renderer.Mesh;
import com.jade.renderer.Shader;
import com.jade.renderer.Texture;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Billboard extends Component {

    private Shader shader = AssetPool.getShader("shaders/billboard.glsl");
    private Mesh mesh;
    private Matrix4f modelMatrix;
    private Texture texture;

    public Billboard(String textureResource) {
        this.modelMatrix = new Matrix4f();
        if (AssetPool.getMesh("mesh-ext/billboard.obj") == null) {
            AssetPool.addMesh("mesh-ext/billboard.obj", Mesh.loadVTSMesh("mesh-ext/billboard.obj"));
        }
        this.mesh = AssetPool.getMesh("mesh-ext/billboard.obj");
        this.texture = AssetPool.getTexture(textureResource);
    }

    private void calculateModelMatrix() {
        this.modelMatrix.identity();

        this.modelMatrix.scale(this.gameObject.transform.scale);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.y), Constants.UP);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.x), Constants.RIGHT);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.z), Constants.FORWARD);
        this.modelMatrix.translate(this.gameObject.transform.position);
    }

    public void render() {
        calculateModelMatrix();

        shader.use();

        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        shader.uploadMat4f("uModel", this.modelMatrix);
        shader.uploadVec2f("uSize", new Vector2f(0.2f, 0.05f));

        shader.uploadTexture("uTexture", 0);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        mesh.render();

        texture.unbind();
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
}
