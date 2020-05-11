package com.jade.renderer;

import com.jade.Window;
import com.jade.components.SpriteRenderer;
import com.jade.util.AssetPool;
import com.jade.util.Constants;
import com.jade.util.JMath;
import com.jade.util.enums.DataType;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class UIBatcher implements Comparable<UIBatcher> {
//
//         Vertex
//        ======
//        Pos                      Color                       TexCoord         TexID
//        123.0f, 232.0f, 10.f,    0.0f, 1.0f, 0.0f, 1.0f,     0.0f, 0.0f,      1

    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORD_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int START_OFFSET = 0;
    private final int POS_OFFSET = START_OFFSET * JMath.sizeof(DataType.FLOAT);
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int TEX_COORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int TEX_ID_OFFSET = TEX_COORD_OFFSET + TEX_COORD_SIZE * JMath.sizeof(DataType.FLOAT);
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = JMath.sizeof(DataType.FLOAT) * VERTEX_SIZE;

    private List<SpriteRenderer> sprites;
    private List<SpriteRenderer> spritesToRemove;

    private List<Texture> textures;
    private float[] vertices;
    private int[] indices;
    private Shader shader;
    private Renderer renderer;
    private int maxBatchSize;

    private int vaoID, vboID, eboID;

    private boolean hasRoom = true;
    private int zIndex;

    public UIBatcher(int maxBatchSize, Renderer renderer, int zIndex) {
        this.shader = AssetPool.getShader("shaders/default.glsl");
        this.textures = new ArrayList<>();
        this.sprites = new ArrayList<>();
        this.spritesToRemove = new ArrayList<>();
        this.maxBatchSize = maxBatchSize;

        // 4 Vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        // 6 indices per quad (3 per triangle)
        indices = new int[maxBatchSize * 6];
        this.renderer = renderer;

        this.zIndex = zIndex;

        generateIndices();
    }

    public void clear() {
        glDeleteVertexArrays(vaoID);
    }

    public void start() {
        // Generate and bind a Vertex Array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create an IntBuffer of the indices
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        // Create a Buffer Object and upload the vertices buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create another buffer object for the indices, then upload the indices
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_DYNAMIC_DRAW);

        // Enable the buffer to know which coords are positions and which ones are
        // colors...
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORD_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void add(SpriteRenderer spr) {
        // Get index and add renderable
        int index = this.sprites.size();
        this.sprites.add(spr);

        // If renderable has texture add it if it is needed to this batch's textures
        if (!textures.contains(spr.getSprite().getTexture())) {
            textures.add(spr.getSprite().getTexture());
        }

        // Add properties to local array
        loadVertexProperties(index);

        if (sprites.size() >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public int numSprites() {
        return this.sprites.size();
    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites.get(index);
        if (!sprite.shouldDisplay()) {
            loadEmptyVertexProperties(index);
            return;
        }
        // Add it's transform and stuff to the vertex array
        int offset = index * VERTEX_SIZE * 4;

        Vector4f color = sprite.getColor();

        Vector2f[] texCoords = sprite.getSprite().getTexCoords();
        assert texCoords.length == 4 : "Texture coordinates must come in array of Vector2f size 4.";

        Texture tex = sprite.getSprite().getTexture();
        int texSlot = 0;
        if (tex != null) {
            if (!textures.contains(tex)) {
                if (!hasTextureRoom()) {
                    assert true : "Texture cannot be added, and is required for this batch.";
                }
                textures.add(tex);
            }

            for (int i=0; i < textures.size(); i++) {
                if (textures.get(i) == tex) {
                    texSlot = i + 1;
                    break;
                }
            }

            assert texSlot != 0 : "Texture should be assigned to this batch.";
        }


        // Get transform matrix to apply transformations
        // TODO: Test this, consider making it faster by doing operations by hand
        Matrix4f transform = new Matrix4f();
        transform.translate(sprite.gameObject.transform.position);
        transform.rotate((float)Math.toRadians(sprite.gameObject.transform.rotation.z), Constants.FORWARD);
        transform.scale(sprite.gameObject.transform.scale);

        // Add 4 vertices with the appropriate properties to vertex array
        float xAdd = 0.5f;
        float yAdd = -0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = -0.5f;
            }

            Vector4f currentPos = new Vector4f(xAdd, yAdd, 0.0f, 1.0f).mul(transform);
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;
            vertices[offset + 2] = currentPos.z;

            // Load color
            vertices[offset + 3] = color.x;
            vertices[offset + 4] = color.y;
            vertices[offset + 5] = color.z;
            vertices[offset + 6] = color.w;

            // Load tex coords
            vertices[offset + 7] = texCoords[i].x;
            vertices[offset + 8] = texCoords[i].y;

            // Load tex id
            vertices[offset + 9] = texSlot;


            offset += VERTEX_SIZE;
        }
    }

    public void loadEmptyVertexProperties(int index) {
        // Add it's transform and stuff to the vertex array
        int offset = index * VERTEX_SIZE * 4;
        for (int i=0; i < 4; i++) {
            vertices[offset] = 0;
            vertices[offset + 1] = 0;
            vertices[offset + 2] = 0;

            // Load color
            vertices[offset + 3] = 0;
            vertices[offset + 4] = 0;
            vertices[offset + 5] = 0;
            vertices[offset + 6] = 0;

            // Load tex coords
            vertices[offset + 7] = 0;
            vertices[offset + 8] = 0;

            // Load tex id
            vertices[offset + 9] = 0;


            offset += VERTEX_SIZE;
        }
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 7;
    }

    public void render() {
        boolean rebufferData = false;
        for (int i=0; i < sprites.size(); i++) {
            SpriteRenderer spr = sprites.get(i);
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }

            if (spr.shouldDelete()) {
                deleteVertexProperties(i);
                rebufferData = true;
            }
        }
        // Remove deleted sprites if needed
        if (spritesToRemove.size() > 0) {
            removeDeletedSprites();
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, sprites.size() * VERTEX_SIZE * 4));
        }

        // Use our program
        shader.use();
        shader.uploadMat4f("uProjection", renderer.camera().getOrthoProjection());
        shader.uploadMat4f("uView", renderer.camera().getOrthoView());
        // Upload all the textures
        for (int i=0; i < textures.size(); i++) {
            shader.uploadTexture("TEX_" + (i + 1), i + 1);
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadFloat("uAspect", Window.getWindow().getAspect());

        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        // Draw the batch
        glDrawElements(GL_TRIANGLES, sprites.size() * 6, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        for (Texture tex : textures) {
            tex.unbind();
        }
        glBindVertexArray(0);

        // Un-bind our program
        shader.detach();
    }

    private void removeDeletedSprites() {
        for (SpriteRenderer sprite : spritesToRemove) {
            sprites.remove(sprite);
        }
        spritesToRemove.clear();
    }

    private void deleteVertexProperties(int index) {
        spritesToRemove.add(sprites.get(index));

        // Shift everything back one over this guy,
        // so that everything is in order...
        // We will remove him after we finish checking other sprites
        while (index < sprites.size() - 1) {
            int offset = index * VERTEX_SIZE * 4;
            int nextOffset = (index + 1) * VERTEX_SIZE * 4;
            for (int i = 0; i < 4; i++) {
                // Load position
                vertices[offset] = vertices[nextOffset];
                vertices[offset + 1] = vertices[nextOffset + 1];
                vertices[offset + 2] = vertices[nextOffset + 2];

                // Load color
                vertices[offset + 3] = vertices[nextOffset + 3];
                vertices[offset + 4] = vertices[nextOffset + 4];
                vertices[offset + 5] = vertices[nextOffset + 5];
                vertices[offset + 6] = vertices[nextOffset + 6];

                // Load tex coords
                vertices[offset + 7] = vertices[nextOffset + 7];
                vertices[offset + 8] = vertices[nextOffset + 8];

                // Load tex id
                vertices[offset + 9] = vertices[nextOffset + 9];

                offset += VERTEX_SIZE;
            }
            index++;
        }
    }

    private void loadElementIndices(int index) {
        int offsetArray = 6 * index;
        int offset = 4 * index;

        // Triangle 1
        indices[offsetArray] = offset + 3;
        indices[offsetArray + 1] = offset + 2;
        indices[offsetArray + 2] = offset + 0;

        // Triangle 2
        indices[offsetArray + 3] = offset + 0;
        indices[offsetArray + 4] = offset + 2;
        indices[offsetArray + 5] = offset + 1;
    }

    public void generateIndices() {
        for (int i=0; i < maxBatchSize; i++) {
            this.loadElementIndices(i);
        }
    }

    @Override
    public int compareTo(UIBatcher batch) {
        return Integer.compare(batch.zIndex, this.zIndex);
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}
