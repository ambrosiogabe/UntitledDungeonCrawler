package com.jade.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    FloatBuffer vertices;
    IntBuffer indices;
    FloatBuffer normals;

    int triCount;
    int arrayCount;

    private boolean drawArrays = false;
    private int vaoID;

    public Mesh(List<Float> vertices, List<Integer> indices) {
        float[] floatArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            floatArray[i] = vertices.get(i);
        }
        int[] intArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            intArray[i] = indices.get(i);
        }

        ByteBuffer vertBytes = ByteBuffer.allocateDirect(floatArray.length * Float.BYTES);
        vertBytes.order(ByteOrder.nativeOrder());
        this.vertices = vertBytes.asFloatBuffer();
        this.vertices.put(floatArray);
        this.vertices.position(0);

        ByteBuffer indBytes = ByteBuffer.allocateDirect(intArray.length * Float.BYTES);
        indBytes.order(ByteOrder.nativeOrder());
        this.indices = indBytes.asIntBuffer();
        this.indices.put(intArray);
        this.indices.position(0);

        this.triCount = indices.size();
        this.allocateGPUMemory();
    }

    public Mesh(FloatBuffer verts, IntBuffer indices) {
        verts.flip();
        indices.flip();
        this.vertices = verts;
        this.indices = indices;
        allocateGPUMemory();
    }

    public Mesh(float[] verts, int[] indices) {
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
        this.allocateGPUMemory();
    }

    public Mesh(float[] verts) {
        drawArrays = true;
        ByteBuffer vertBytes = ByteBuffer.allocateDirect(verts.length * Float.BYTES);
        vertBytes.order(ByteOrder.nativeOrder());
        this.vertices = vertBytes.asFloatBuffer();
        this.vertices.put(verts);
        this.vertices.position(0);
        this.allocateGPUMemory();

        this.arrayCount = verts.length;
    }

    private void allocateGPUMemory() {
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
}
