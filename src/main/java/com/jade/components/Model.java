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
    private List<Mesh> meshes;

    private PointLight[] pointLights = new PointLight[4];

    public Model(String resourceName, String texResource) {
        this.modelMatrix = new Matrix4f();
        this.texture = AssetPool.getTexture(texResource);

        this.meshes = new ArrayList<>();

        URL url = this.getClass().getClassLoader().getResource(resourceName);
        assert url != null : "Error: Model file not found '" + resourceName + "'";

        File file = new File(url.getFile());
        AIScene scene = Assimp.aiImportFile(file.getAbsolutePath(), Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);

        assert (scene != null && ((scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) == 0) && scene.mRootNode() != null) :
                "Error: Assimp could not load model properly '" + resourceName + "'\n" + Assimp.aiGetErrorString();

        processNode(scene.mRootNode(), scene);
    }

    public Model(String resourceName) {
        this.modelMatrix = new Matrix4f();

        this.meshes = new ArrayList<>();

        URL url = this.getClass().getClassLoader().getResource(resourceName);
        assert url != null : "Error: Model file not found '" + resourceName + "'";

        File file = new File(url.getFile());
        AIScene scene = Assimp.aiImportFile(file.getAbsolutePath(), Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);

        assert (scene != null && ((scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) == 0) && scene.mRootNode() != null) :
                "Error: Assimp could not load model properly '" + resourceName + "'\n" + Assimp.aiGetErrorString();

        processNode(scene.mRootNode(), scene);
    }

    public Model(Mesh mesh) {
        this.modelMatrix = new Matrix4f();
        this.meshes = new ArrayList<>();
        this.meshes.add(mesh);
    }

    private void processNode(AINode node, AIScene scene) {
        // Process all the node's meshes (if any)
        for (int i=0; i < node.mNumMeshes(); i++) {
            long meshPointer = scene.mMeshes().get(node.mMeshes().get(i));
            AIMesh mesh = AIMesh.create(meshPointer);
            meshes.add(processMesh(mesh, scene));
        }
        // Then do the same for each of its children
        for (int i=0; i < node.mNumChildren(); i++) {
            long nodePointer = node.mChildren().get(i);
            AINode child = AINode.create(nodePointer);
            processNode(child, scene);
        }
    }

    private Mesh processMesh(AIMesh aiMesh, AIScene scene) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        // Process verts
        for (int i=0; i < aiMesh.mNumVertices(); i++) {
            AIVector3D position = aiMesh.mVertices().get(i);
            vertices.add(position.x());
            vertices.add(position.y());
            vertices.add(position.z());

            // Add texture coordinates if they exist
            // If they do not, add two 0's so that our vertex is still formatted properly
            AIVector3D.Buffer texCoordsBuffer = aiMesh.mTextureCoords(0);
            if (texCoordsBuffer != null) {
                vertices.add(texCoordsBuffer.get(i).x());
                vertices.add(texCoordsBuffer.get(i).y());
            } else {
                vertices.add(0.0f);
                vertices.add(0.0f);
            }

            vertices.add(-aiMesh.mNormals().x());
            vertices.add(-aiMesh.mNormals().y());
            vertices.add(aiMesh.mNormals().z());
        }

        // Process indices
        for (int i=0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);
            for (int j=0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        // Process textures
        // TODO: Actually process the textures!!

        // TODO: Create resource manager for meshes so that they are not allocating GPU memory here!!!
        return new Mesh(vertices, indices);
    }

    public void clear() {
        for (Mesh mesh : meshes) {
            mesh.clear();
        }
    }

    private void calculateModelMatrix() {
        this.modelMatrix.identity();

        this.modelMatrix.scale(this.gameObject.transform.scale);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.y), Constants.UP);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.x), Constants.RIGHT);
        this.modelMatrix.rotate((float)Math.toRadians(this.gameObject.transform.rotation.z), Constants.FORWARD);
        this.modelMatrix.translate(this.gameObject.transform.position);
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
        if (texture != null) {
            shader.uploadTexture("uTexture", 0);
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
            shader.uploadFloat("uUseTexture", 1.0f);
        } else {
            shader.uploadFloat("uUseTexture", 0.0f);
        }

        if (pointLights[0] != null) {
            Vector3f pos = pointLights[0].gameObject.transform.position;
            shader.uploadVec3f("uLightPos", new Vector3f(pos.x, pos.y, pos.z));
        }

        for (Mesh mesh : meshes) {
            mesh.render();
        }

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
}
