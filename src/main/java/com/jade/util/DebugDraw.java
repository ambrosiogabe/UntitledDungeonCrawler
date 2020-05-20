package com.jade.util;

import com.jade.Transform;
import com.jade.Window;
import com.jade.renderer.Line;
import com.jade.renderer.Line2D;
import com.jade.renderer.Shader;
import com.jade.renderer.Texture;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3f;
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
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 500;

    private static List<Line> lines = new ArrayList<>();
    private static List<Line2D> line2D = new ArrayList<>();
    private static float[] vertexArray = new float[MAX_LINES * 6 * 8];
    private static float[] vertexArray2D = new float[MAX_LINES * 6];
    private static Shader shader = AssetPool.getShader("shaders/debugLines.glsl");
    private static Shader shader2D = AssetPool.getShader("shaders/debugLines2D.glsl");

    private static int vaoID;
    private static int vboID;

    private static int vaoID2d;
    private static int vboID2d;

    private static boolean started = false;

    public static void start() {
        // Generate and bind a Vertex Array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a Buffer Object and allocate space
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create another buffer object for the indices, then upload the indices
        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndicesBuffer(), GL_DYNAMIC_DRAW);

        // Enable the buffer to know which coords are positions and which ones are
        // colors...
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        //===================================================================================================
        // 2D lines
        //===================================================================================================
        // Generate and bind a Vertex Array
        vaoID2d = glGenVertexArrays();
        glBindVertexArray(vaoID2d);

        // Create a Buffer Object and allocate space
        vboID2d = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID2d);
        glBufferData(GL_ARRAY_BUFFER, vertexArray2D.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create another buffer object for the indices, then upload the indices
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndicesBuffer2D(), GL_DYNAMIC_DRAW);

        // Enable the buffer to know which coords are positions and which ones are
        // colors...
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    private static IntBuffer getIndicesBuffer2D() {
        int[] indices = new int[MAX_LINES * 6];
        for (int i=0; i < MAX_LINES; i++) {
            loadElementIndices2D(i, indices);
        }
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        return indexBuffer;
    }

    private static void loadElementIndices2D(int index, int[] indices) {
        int offsetArray = 6 * index;
        int offset = 4 * index;

        // Quad 1
        // Triangle 1
        indices[offsetArray] = offset + 3;
        indices[offsetArray + 1] = offset + 2;
        indices[offsetArray + 2] = offset + 1;

        // Triangle 2
        indices[offsetArray + 3] = offset + 1;
        indices[offsetArray + 4] = offset + 2;
        indices[offsetArray + 5] = offset + 0;
    }

    private static IntBuffer getIndicesBuffer() {
        int[] indices = new int[MAX_LINES * 6 * 6];
        for (int i=0; i < MAX_LINES; i++) {
            loadElementIndices(i, indices);
        }
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        return indexBuffer;
    }

    private static void loadElementIndices(int index, int[] indices) {
        int offsetArray = 36 * index;
        int offset = 8 * index;

        // Quad 1
        // Triangle 1
        indices[offsetArray] = offset + 3;
        indices[offsetArray + 1] = offset + 2;
        indices[offsetArray + 2] = offset + 0;

        // Triangle 2
        indices[offsetArray + 3] = offset + 3;
        indices[offsetArray + 4] = offset + 0;
        indices[offsetArray + 5] = offset + 1;

        // Quad 2
        // Triangle 3
        indices[offsetArray + 6] = offset + 2;
        indices[offsetArray + 7] = offset + 6;
        indices[offsetArray + 8] = offset + 4;

        // Triangle 4
        indices[offsetArray + 9] = offset + 2;
        indices[offsetArray + 10] = offset + 4;
        indices[offsetArray + 11] = offset + 0;

        // Quad 3
        // Triangle 5
        indices[offsetArray + 12] = offset + 6;
        indices[offsetArray + 13] = offset + 7;
        indices[offsetArray + 14] = offset + 5;

        // Triangle 6
        indices[offsetArray + 15] = offset + 6;
        indices[offsetArray + 16] = offset + 5;
        indices[offsetArray + 17] = offset + 4;

        // Quad 4
        // Triangle 7
        indices[offsetArray + 18] = offset + 2;
        indices[offsetArray + 19] = offset + 3;
        indices[offsetArray + 20] = offset + 7;

        // Triangle 8
        indices[offsetArray + 21] = offset + 2;
        indices[offsetArray + 22] = offset + 7;
        indices[offsetArray + 23] = offset + 6;

        // Quad 5
        // Triangle 9
        indices[offsetArray + 24] = offset + 7;
        indices[offsetArray + 25] = offset + 3;
        indices[offsetArray + 26] = offset + 1;

        // Triangle 10
        indices[offsetArray + 27] = offset + 7;
        indices[offsetArray + 28] = offset + 1;
        indices[offsetArray + 29] = offset + 5;

        // Quad 6
        // Triangle 11
        indices[offsetArray + 30] = offset + 4;
        indices[offsetArray + 31] = offset + 5;
        indices[offsetArray + 32] = offset + 1;

        // Triangle 12
        indices[offsetArray + 33] = offset + 4;
        indices[offsetArray + 34] = offset + 1;
        indices[offsetArray + 35] = offset + 0;
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }
        for (int i=0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() <= 0) {
                lines.remove(i);
                i--;
            }
        }

        for (int i=0; i < line2D.size(); i++) {
            if (line2D.get(i).beginFrame() <= 0) {
                line2D.remove(i);
                i--;
            }
        }
    }

    public static void endFrame() {
        int index = 0;
        for (Line line : lines) {
            Vector3f[] verts = line.getVerts();
            if (index >= vertexArray.length) break;

            for (int i=0; i < 8; i++) {
                vertexArray[index] = verts[i].x;
                vertexArray[index + 1] = verts[i].y;
                vertexArray[index + 2] = verts[i].z;

                vertexArray[index + 3] = line.getColor().x;
                vertexArray[index + 4] = line.getColor().y;
                vertexArray[index + 5] = line.getColor().z;
                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 8));

        // Use our program
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        shader.uploadFloat("uAspect", Window.getWindow().getAspect());

        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawElements(GL_TRIANGLES,lines.size() * 12 * 3, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        // Un-bind our program
        shader.detach();


        // =====================================================================================
        // 2D lines
        // =====================================================================================
        index = 0;
        for (Line2D line : line2D) {
            Vector2f[] verts = line.getVerts();
            if (index >= vertexArray2D.length) break;

            for (int i=0; i < 4; i++) {
                vertexArray2D[index] = verts[i].x;
                vertexArray2D[index + 1] = verts[i].y;

                vertexArray2D[index + 2] = line.getColor().x;
                vertexArray2D[index + 3] = line.getColor().y;
                vertexArray2D[index + 4] = line.getColor().z;
                index += 5;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID2d);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray2D, 0, line2D.size() * 5 * 4));

        // Use our program
        shader2D.use();
        shader2D.uploadMat4f("uProjection", Window.getScene().camera().getOrthoProjection());
        shader2D.uploadMat4f("uView", Window.getScene().camera().getOrthoView());

        // Bind the vertex array and enable our location
        glBindVertexArray(vaoID2d);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawElements(GL_TRIANGLES,line2D.size() * 6, GL_UNSIGNED_INT, 0);

        // Disable our location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        // Un-bind our program
        shader2D.detach();
    }

    // =======================================================================================================
    // Add line2D methods
    // =======================================================================================================
    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, 1f, Constants.COLOR3_GREEN, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, float strokeWidth) {
        addLine2D(from, to, strokeWidth, Constants.COLOR3_GREEN, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, float strokeWidth, Vector3f color) {
        addLine2D(from, to, strokeWidth, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, float strokeWidth, Vector3f color, int lifetime) {
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.line2D.add(new Line2D(from, to, color, strokeWidth, lifetime));
    }

    // =======================================================================================================
    // Add box 2D methods
    // =======================================================================================================
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
        addBox2D(center, dimensions, rotation,1f, Constants.COLOR3_GREEN, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, float strokeWidth) {
        addBox2D(center, dimensions, rotation, strokeWidth, Constants.COLOR3_GREEN, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, float strokeWidth, Vector3f color) {
        addBox2D(center, dimensions, rotation, strokeWidth, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, float strokeWidth, Vector3f color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2.0f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2.0f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (rotation != 0f) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[1], strokeWidth, color, lifetime);
        addLine2D(vertices[0], vertices[3], strokeWidth, color, lifetime);
        addLine2D(vertices[1], vertices[2], strokeWidth, color, lifetime);
        addLine2D(vertices[2], vertices[3], strokeWidth, color, lifetime);
    }

    // =======================================================================================================
    // Add line methods
    // =======================================================================================================
    public static void addLine(Vector3f from, Vector3f to) {
        addLine(from, to, 0.05f, Constants.COLOR3_GREEN, 1);
    }

    public static void addLine(Vector3f from, Vector3f to, float strokeWidth) {
        addLine(from, to, strokeWidth, Constants.COLOR3_GREEN, 1);
    }

    public static void addLine(Vector3f from, Vector3f to, float strokeWidth, Vector3f color) {
        addLine(from, to, strokeWidth, color, 1);
    }

    public static void addLine(Vector3f from, Vector3f to, float strokeWidth, Vector3f color, int lifetime) {
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line(from, to, color, strokeWidth, lifetime));
    }

    // =======================================================================================================
    // Add box methods
    // =======================================================================================================
    public static void addBox(Vector3f center, Vector3f dimensions, Transform transform) {
        addBox(center, dimensions, transform,0.05f, Constants.COLOR3_GREEN, 1);
    }

    public static void addBox(Vector3f center, Vector3f dimensions, Transform transform, float strokeWidth) {
        addBox(center, dimensions, transform, strokeWidth, Constants.COLOR3_GREEN, 1);
    }

    public static void addBox(Vector3f center, Vector3f dimensions, Transform transform, float strokeWidth, Vector3f color) {
        addBox(center, dimensions, transform, strokeWidth, color, 1);
    }

    public static void addBox(Vector3f center, Vector3f dimensions, Transform transform, float strokeWidth, Vector3f color, int lifetime) {
        Vector3f forward = new Vector3f(transform.forward);
        Vector3f up = new Vector3f(transform.up);
        Vector3f right = new Vector3f(transform.right);

        addLine(center, new Vector3f(center).add(forward), 0.1f, Constants.COLOR3_GREEN);
        addLine(center, new Vector3f(center).add(right), 0.1f, Constants.COLOR3_RED);
        addLine(center, new Vector3f(center).add(up), 0.1f, Constants.COLOR3_BLUE);

        float halfWidth = dimensions.x / 2.0f;
        float halfHeight = dimensions.y / 2.0f;
        float halfLength = dimensions.z / 2.0f;

        Vector3f[] verts = new Vector3f[8];
        verts[0] = new Vector3f(center).add(new Vector3f(up).mul(halfHeight)).sub(new Vector3f(right).mul(halfWidth)).sub(new Vector3f(forward).mul(halfLength));
        verts[1] = new Vector3f(center).add(new Vector3f(up).mul(halfHeight)).add(new Vector3f(right).mul(halfWidth)).sub(new Vector3f(forward).mul(halfLength));
        verts[2] = new Vector3f(verts[0]).sub(new Vector3f(up).mul(dimensions.y));
        verts[3] = new Vector3f(verts[1]).sub(new Vector3f(up).mul(dimensions.y));

        Vector3f addVector = new Vector3f(forward.mul(dimensions.z));
        verts[4] = new Vector3f(verts[0]).add(addVector);
        verts[5] = new Vector3f(verts[1]).add(addVector);
        verts[6] = new Vector3f(verts[2]).add(addVector);
        verts[7] = new Vector3f(verts[3]).add(addVector);

        addLine(verts[0], verts[1], strokeWidth, color, lifetime);
        addLine(verts[2], verts[0], strokeWidth, color, lifetime);
        addLine(verts[2], verts[3], strokeWidth, color, lifetime);
        addLine(verts[1], verts[3], strokeWidth, color, lifetime);

        addLine(verts[4], verts[5], strokeWidth, color, lifetime);
        addLine(verts[4], verts[6], strokeWidth, color, lifetime);
        addLine(verts[6], verts[7], strokeWidth, color, lifetime);
        addLine(verts[7], verts[5], strokeWidth, color, lifetime);

        addLine(verts[0], verts[4], strokeWidth, color, lifetime);
        addLine(verts[2], verts[6], strokeWidth, color, lifetime);
        addLine(verts[1], verts[5], strokeWidth, color, lifetime);
        addLine(verts[3], verts[7], strokeWidth, color, lifetime);
    }

    // =======================================================================================================
    // Add circle methods
    // =======================================================================================================
    public static void addCircle(Vector3f center, Vector3f normal, float radius) {
        addCircle(center, normal, radius, 0.05f, Constants.COLOR3_GREEN, 1);
    }

    public static void addCircle(Vector3f center, Vector3f normal, float radius, float strokeWidth) {
        addCircle(center, normal, radius, strokeWidth, Constants.COLOR3_GREEN, 1);
    }

    public static void addCircle(Vector3f center, Vector3f normal, float radius, float strokeWidth, Vector3f color) {
        addCircle(center, normal, radius, strokeWidth, color, 1);
    }

    public static void addCircle(Vector3f center, Vector3f normal, float radius, float strokeWidth, Vector3f color, int lifetime) {
        Vector3f forward = new Vector3f(normal);
        forward.normalize();
        // Make sure to cross with non-parallel axis. We check which axis is greater
        // then our 'tolerance' angle, then cross with that axis
        float tolerance = 0.1f;
        Vector3f right;
        if (Math.abs(forward.dot(Constants.UP)) < tolerance) {
            right = new Vector3f(forward).cross(Constants.UP);
        } else if (Math.abs(forward.dot(Constants.FORWARD)) < tolerance) {
            right = new Vector3f(forward).cross(Constants.FORWARD);
        } else {
            right = new Vector3f(forward).cross(Constants.RIGHT);
        }
        right.normalize();

        Vector3f[] points = new Vector3f[20];
        Vector3f firstPoint = new Vector3f().add(right).mul(radius);
        points[0] = new Vector3f(firstPoint).add(center);
        Quaternionf rotation = new Quaternionf();//.fromAxisAngleDeg(normal.x, normal.y, normal.z, 0);

        int increment = 360 / points.length;
        int currentAngle = increment;
        for (int i=1; i < points.length; i++) {
            rotation.fromAxisAngleDeg(normal.x, normal.y, normal.z, currentAngle);
            points[i] = rotation.transform(new Vector3f(firstPoint));
            points[i].add(center);

            addLine(points[i - 1], points[i], strokeWidth, color, lifetime);
            currentAngle += increment;
        }

        addLine(points[points.length - 1], points[0], strokeWidth, color, lifetime);
    }

    // =======================================================================================================
    // Add sphere methods
    // =======================================================================================================
    public static void addSphere(Vector3f center, float radius) {
        addSphere(center, radius, 0.05f, Constants.COLOR3_GREEN, 1);
    }

    public static void addSphere(Vector3f center, float radius, float strokeWidth) {
        addSphere(center, radius, strokeWidth, Constants.COLOR3_GREEN, 1);
    }

    public static void addSphere(Vector3f center, float radius, float strokeWidth, Vector3f color) {
        addSphere(center, radius, strokeWidth, color, 1);
    }

    public static void addSphere(Vector3f center, float radius, float strokeWidth, Vector3f color, int lifetime) {
        addCircle(center, Constants.UP, radius, strokeWidth, color, lifetime);
        addCircle(center, Constants.FORWARD, radius, strokeWidth, color, lifetime);
        addCircle(center, Constants.RIGHT, radius, strokeWidth, color, lifetime);
    }
}

