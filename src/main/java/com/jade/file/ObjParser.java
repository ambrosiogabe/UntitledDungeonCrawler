package com.jade.file;

import com.jade.renderer.Mesh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ObjParser {
    private static char peek = '\0';
    private static char current = '\0';

    private static float[] resize(float[] src) {
        float[] dst = new float[src.length * 2];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    private static int[] resize(int[] src) {
        int[] dst = new int[src.length * 2];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public static Mesh processVTNObj(String resourceName) {
        current = '\0';
        peek = 'b';

        URL url = Mesh.class.getClassLoader().getResource(resourceName);
        assert url != null : "Error: Model file not found '" + resourceName + "'";

        float[] finalVertexArray = new float[8];
        int finalVertexIndex = 0;

        float[] vertices = new float[8];
        float[] normals = new float[8];
        float[] texCoords = new float[8];
        int[] indices = new int[8];

        int vertex = 0;
        int normal = 0;
        int texCoord = 0;
        int index = 0;
        int indexVal = 0;

        File file = new File(url.getFile());
        try {
            InputStream is = new FileInputStream(file);

            while (peek != '\0') {
                readChar(is);
                if (current != 'v' && current != 'f') {
                    readChar(is);
                    while (current != '\n' && current != '\r') {
                        readChar(is);
                    }
                } else if (current == 'v') {
                    readChar(is);
                    if (current == 't') {
                        readChar(is);
                        assert current == ' ' : "Expected space after 'vt' in '" + resourceName + "' file";

                        float x = parseFloat(is);
                        float y = parseFloat(is);
                        texCoords = addToArray(texCoords, texCoord++, x);
                        texCoords = addToArray(texCoords, texCoord++, y);
                    } else if (current == 'n') {
                        readChar(is);
                        assert current == ' ' : "Expected space after 'vn' in '" + resourceName + "' file";

                        float x = parseFloat(is);
                        float y = parseFloat(is);
                        float z = parseFloat(is);
                        normals = addToArray(normals, normal++, x);
                        normals = addToArray(normals, normal++, y);
                        normals = addToArray(normals, normal++, z);
                    } else if (current == ' ') {
                        float x = parseFloat(is);
                        float y = parseFloat(is);
                        float z = parseFloat(is);
                        vertices = addToArray(vertices, vertex++, x);
                        vertices = addToArray(vertices, vertex++, y);
                        vertices = addToArray(vertices, vertex++, z);
                    }
                } else if (current == 'f') {
                    readChar(is);
                    assert current == ' ' : "Expect space after 'f' in .obj file '" + resourceName + "'";

                    boolean wasThreeOnly = true;
                    for (int i=0; i < 3; i++) {
                        int v = (parseInt(is) - 1) * 3;
                        int vt = (parseInt(is) - 1) * 2;
                        int vn = (parseInt(is) - 1) * 3;

                        finalVertexArray = addVertex(v, vt, vn, finalVertexArray, finalVertexIndex, vertices, texCoords, normals);
                        finalVertexIndex += 8;
                    }
                    if (peek == ' ' || (peek >= '0' && peek <= '9')) {
                        wasThreeOnly = false;
                        int v = (parseInt(is) - 1) * 3;
                        int vt = (parseInt(is) - 1) * 2;
                        int vn = (parseInt(is) - 1) * 3;

                        finalVertexArray = addVertex(v, vt, vn, finalVertexArray, finalVertexIndex, vertices, texCoords, normals);
                        finalVertexIndex += 8;
                    }

                    // Add indices
                    indices = addToArray(indices, index, indexVal);
                    indices = addToArray(indices, index + 1, indexVal + 1);
                    indices = addToArray(indices, index + 2, indexVal + 2);

                    if (!wasThreeOnly) {
                        indices = addToArray(indices, index + 3, indexVal);
                        indices = addToArray(indices, index + 4, indexVal + 2);
                        indices = addToArray(indices, index + 5, indexVal + 3);
                        indexVal += 4;
                        index += 6;
                    } else {
                        indexVal += 3;
                        index += 3;
                    }
                }
            }
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error reading file.";
        }

        float[] verts = new float[finalVertexIndex];
        System.arraycopy(finalVertexArray, 0, verts, 0, finalVertexIndex);
        int[] inds = indices.length > index ? new int[index] : indices;
        if (inds.length == index)
            System.arraycopy(indices, 0, inds, 0, index);

        return new Mesh(verts, inds, 0);
    }

    public static void readChar(InputStream is) throws IOException {
        if (current == '\0') {
            current = (char)is.read();
            peek = (char)is.read();
        } else {
            current = peek;
            int p = is.read();
            peek = p == -1 ? '\0' : (char)p;
        }
    }

    public static float[] addVertex(int v, int vt, int vn, float[] finalVertexArray, int finalVertexIndex, float[] vertices, float[] texCoords, float[] normals) {
        // Add positions
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, vertices[v]);
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, vertices[v + 1]);
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, vertices[v + 2]);
        // Add texture coords
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, texCoords[vt]);
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, texCoords[vt + 1]);
        // Add normals
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, normals[vn]);
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, normals[vn + 1]);
        finalVertexArray = addToArray(finalVertexArray, finalVertexIndex++, normals[vn + 2]);
        return finalVertexArray;
    }

    public static float parseFloat(InputStream is) throws IOException {
        String numString = "";
        readChar(is);
        while ((current >= '0' && current <= '9' )|| current == '.' || current == '-') {
            numString += current;
            readChar(is);
        }
        return Float.parseFloat(numString);
    }

    public static int parseInt(InputStream is) throws IOException {
        String numString = "";
        readChar(is);
        while (current >= '0' && current <= '9') {
            numString += current;
            readChar(is);
        }

        return Integer.parseInt(numString);
    }

    public static float[] addToArray(float[] arr, int index, float val) {
        if (index >= arr.length) {
            arr = resize(arr);
        }
        arr[index] = val;
        return arr;
    }

    public static int[] addToArray(int[] arr, int index, int val) {
        if (index >= arr.length) {
            arr = resize(arr);
        }
        arr[index] = val;
        return arr;
    }
}
