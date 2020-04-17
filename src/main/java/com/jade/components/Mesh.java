package com.jade.components;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    FloatBuffer vertices;
    IntBuffer indices;

    int triCount;

    private int vaoID;

    public Mesh(List<Float> vertices, List<Integer> indices) {
        float[] floatArray = new float[vertices.size()];
        for (int i=0; i < vertices.size(); i++) {
            floatArray[i] = vertices.get(i);
        }
        int[] intArray = new int[indices.size()];
        for (int i=0; i < indices.size(); i++) {
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

        this.vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);

        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);
        glEnableVertexAttribArray(1);
    }

    public void render() {
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, triCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
