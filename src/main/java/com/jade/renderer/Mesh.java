package com.jade.renderer;

import com.jade.file.ObjParser;
import com.jade.util.AssetPool;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.*;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private static float[] SQUARE_VERTICES = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
    };

    FloatBuffer vertices;
    IntBuffer indices;

    int triCount;
    int arrayCount;

    private boolean drawArrays = false;
    private int vaoID;
    private String resourceName;

    /*
        @type : 0 = VTN,
                1 = VTS
     */
    public Mesh(float[] verts, int[] indices, int type) {
        ByteBuffer vertBytes = ByteBuffer.allocateDirect(verts.length * Float.BYTES);
        vertBytes.order(ByteOrder.nativeOrder());
        this.vertices = vertBytes.asFloatBuffer();
        this.vertices.put(verts);
        this.vertices.position(0);

        ByteBuffer indBytes = ByteBuffer.allocateDirect(indices.length * Float.BYTES);
        indBytes.order(ByteOrder.nativeOrder());
        this.indices = indBytes.asIntBuffer();
        this.indices.put(indices);
        this.indices.position(0);

        this.triCount = indices.length;
        this.allocateGPUMemory(type);
    }

    private void allocateGPUMemory(int type) {
        this.vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);

        if (!drawArrays) {
            int eboID = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.indices, GL_STATIC_DRAW);
        }

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * 4, 3 * 4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * 4, 5 * 4);
        glEnableVertexAttribArray(1);
    }

    public void clear() {
        glDeleteVertexArrays(this.vaoID);
    }

    public void render() {
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        if (drawArrays) {
            glDrawArrays(GL_TRIANGLES, 0, this.arrayCount);
        } else {
            glDrawElements(GL_TRIANGLES, triCount, GL_UNSIGNED_INT, 0);
        }

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }


    // ===================================================
    // Assimp model loading wrappers
    // Keys:
    //      VTN -- Vertex, Texture, Normal
    //      VTS -- Vertex, Texture, Square Verts
    // ===================================================
    public static Mesh loadVTNMesh(String resourceName) {
        AIScene scene = getMeshScene(resourceName);

        return ObjParser.processVTNObj(resourceName);

//        Mesh mesh = processVTNNode(scene.mRootNode(), scene, resourceName);
//        AssetPool.addMesh(resourceName, mesh);
//        return mesh;
    }

    public static Mesh loadVTSMesh(String resourceName) {
        AIScene scene = getMeshScene(resourceName);

        Mesh mesh = processVTSNode(scene.mRootNode(), scene, resourceName);
        AssetPool.addMesh(resourceName, mesh);
        return mesh;
    }

    private static AIScene getMeshScene(String resourceName) {
        URL url = Mesh.class.getClassLoader().getResource(resourceName);
        assert url != null : "Error: Model file not found '" + resourceName + "'";

        File file = new File(url.getFile());
        AIScene scene = Assimp.aiImportFile(file.getAbsolutePath(), Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);

        assert (scene != null && ((scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) == 0) && scene.mRootNode() != null) :
                "Error: Assimp could not load model properly '" + resourceName + "'\n" + Assimp.aiGetErrorString();
        return scene;
    }

    private static Mesh processVTNNode(AINode node, AIScene scene, String resourceName) {
        if (node.mNumMeshes() == 0 && node.mNumChildren() > 0) {
            long nodePointer = node.mChildren().get(0);
            AINode child = AINode.create(nodePointer);
            return processVTNNode(child, scene, resourceName);
        }
        assert node.mNumMeshes() == 1 : "Assertion: Currently we can only load one mesh at a time: '" + resourceName + "'";

        long meshPointer = scene.mMeshes().get(node.mMeshes().get(0));
        AIMesh mesh = AIMesh.create(meshPointer);
        return processVTNMesh(mesh);

        // Process all the node's meshes (if any)
//        for (int i=0; i < node.mNumMeshes(); i++) {
//            long meshPointer = scene.mMeshes().get(node.mMeshes().get(i));
//            AIMesh mesh = AIMesh.create(meshPointer);
//            return processMesh(mesh, scene);
//        }
        // Then do the same for each of its children
//        for (int i=0; i < node.mNumChildren(); i++) {
//            long nodePointer = node.mChildren().get(i);
//            AINode child = AINode.create(nodePointer);
//            processNode(child, scene);
//        }
    }

    private static Mesh processVTSNode(AINode node, AIScene scene, String resourceName) {
        if (node.mNumMeshes() == 0 && node.mNumChildren() > 0) {
            long nodePointer = node.mChildren().get(0);
            AINode child = AINode.create(nodePointer);
            return processVTNNode(child, scene, resourceName);
        }
        assert node.mNumMeshes() == 1 : "Assertion: Currently we can only load one mesh at a time: '" + resourceName + "'";

        long meshPointer = scene.mMeshes().get(node.mMeshes().get(0));
        AIMesh mesh = AIMesh.create(meshPointer);
        return processVTSMesh(mesh);
    }

    private static Mesh processVTNMesh(AIMesh aiMesh) {
        int numVertices = 0;
        for (int i=0; i < aiMesh.mNumVertices(); i++) {
            numVertices += 8;
        }
        float[] vertices = new float[numVertices];

        // Process verts
        int current = 0;
        for (int i=0; i < aiMesh.mNumVertices(); i++) {
            AIVector3D position = aiMesh.mVertices().get(i);
            vertices[current++] = position.x();
            vertices[current++] = position.y();
            vertices[current++] = position.z();

            // Add texture coordinates if they exist
            // If they do not, add two 0's so that our vertex is still formatted properly
            AIVector3D.Buffer texCoordsBuffer = aiMesh.mTextureCoords(0);
            if (texCoordsBuffer != null) {
                vertices[current++] = texCoordsBuffer.get(i).x();
                vertices[current++] = texCoordsBuffer.get(i).y();
            } else {
                vertices[current++] = 0.0f;
                vertices[current++] = 0.0f;
            }

            vertices[current++] = aiMesh.mNormals().x();
            vertices[current++] = aiMesh.mNormals().y();
            vertices[current++] = aiMesh.mNormals().z();
        }

        int indexCount = 0;
        // Process indices
        for (int i=0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);
            indexCount += face.mNumIndices();
        }

        int[] indices = new int[indexCount];
        current = 0;
        for (int i=0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);
            for (int j=0; j < face.mNumIndices(); j++) {
                indices[current] = face.mIndices().get(j);
                current++;
            }
        }

        // Process textures
        // TODO: Actually process the textures!!

        // TODO: Create resource manager for meshes so that they are not allocating GPU memory here!!!
        return new Mesh(vertices, indices, 0);
    }

    private static Mesh processVTSMesh(AIMesh aiMesh) {
        float[] vertices = new float[aiMesh.mNumVertices() * 8];
        assert aiMesh.mNumVertices() == 4 : "Assertion: VTS Mesh expects a rectangular mesh with only 4 vertices.";

        // Process verts
        int current = 0;
        for (int i=0; i < aiMesh.mNumVertices(); i++) {
            AIVector3D position = aiMesh.mVertices().get(i);
            vertices[current++] = position.x();
            vertices[current++] = position.y();
            vertices[current++] = position.z();

            // Add texture coordinates if they exist
            // If they do not, add two 0's so that our vertex is still formatted properly
            AIVector3D.Buffer texCoordsBuffer = aiMesh.mTextureCoords(0);
            if (texCoordsBuffer != null) {
                vertices[current++] = texCoordsBuffer.get(i).x();
                vertices[current++] = texCoordsBuffer.get(i).y();
            } else {
                vertices[current++] = 0.0f;
                vertices[current++] = 0.0f;
            }

            vertices[current++] = SQUARE_VERTICES[(i * 3)];
            vertices[current++] = SQUARE_VERTICES[(i * 3) + 1];
            vertices[current++] = SQUARE_VERTICES[(i * 3) + 2];
        }

        int indexCount = 0;
        // Process indices
        for (int i=0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);
            indexCount += face.mNumIndices();
        }

        int[] indices = new int[indexCount];
        current = 0;
        for (int i=0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);
            for (int j=0; j < face.mNumIndices(); j++) {
                indices[current] = face.mIndices().get(j);
                current++;
            }
        }

        return new Mesh(vertices, indices, 1);
    }
}
