package org.example.virtualEnvironment;

import org.example.TextureLoader;
import org.example.jsonClasses.JsonObject;
import org.example.shader.ShaderTyp;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL40.*;

public class Mesh {
    private ShaderTyp shaderTyp;
    private int VBO;
    private int VBONormals;
    private int VBOTexture;
    private int VAO;
    private int vertexCount;

    private Vector3f size;

    private int textureId;

    public final float[] vertices;
    public final float[] normals;
    public final float[] textures;

    public final float r;
    public final float g;
    public final float b;

    Mesh(JsonObject jsonObject) {
        this(jsonObject.vertexPositions, jsonObject.normalVectors, jsonObject.color, jsonObject.textureCoordinates, jsonObject.texturePath);
    }

    Mesh(float[] positions, float[] normals, float[] color, float[] textureCoordinates, String texturePath) {
        this.vertices = positions.clone();
        this.normals = normals.clone();
        this.textures = textureCoordinates.clone();
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];

        boolean hasTexture = textureCoordinates.length > 0 && !texturePath.equals("");

        if(hasTexture)
            generateMesh(positions, normals, textureCoordinates, TextureLoader.getInstance().loadTexture(texturePath));
        else
            generateMesh(positions, normals, null, 0);

    }

    Mesh(float[] positions, float[] normals, float[] color, float[] textureCoordinates, int textureId) {
        this.vertices = positions.clone();
        this.normals = normals.clone();
        this.textures = textureCoordinates.clone();
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];

        generateMesh(positions, normals, textureCoordinates, textureId);
    }

    private void generateMesh(float[] positions, float[] normals, float[] textureCoordinates, int textureId) {
        // calculate size
        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;
        float zMin = Float.MAX_VALUE;
        float xMax = -Float.MAX_VALUE;
        float yMax = -Float.MAX_VALUE;
        float zMax = -Float.MAX_VALUE;
        for(int i = 0; i < positions.length; i+=3) {
            float x = positions[i];
            float y = positions[i+1];
            float z = positions[i+2];

            xMin = Math.min(xMin, x);
            yMin = Math.min(yMin, y);
            zMin = Math.min(zMin, z);

            xMax = Math.max(xMax, x);
            yMax = Math.max(yMax, y);
            zMax = Math.max(zMax, z);
        }
        size = new Vector3f(xMax-xMin, yMax-yMin, zMax-zMin);

        vertexCount = positions.length / 3;
        this.textureId = textureId;

        // load mesh to gpu
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        VBONormals = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBONormals);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        if(textureCoordinates == null) {
            shaderTyp = ShaderTyp.BASIC;
        }
        else {
            shaderTyp = ShaderTyp.WITH_TEXTURE;

            VBOTexture = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, VBOTexture);
            glBufferData(GL_ARRAY_BUFFER, textureCoordinates, GL_STATIC_DRAW);

            glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            glEnableVertexAttribArray(2);
        }

        glBindVertexArray(0);
    }

    public int getVAO() {
        return VAO;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getTextureId() {
        return textureId;
    }

    public ShaderTyp getShaderTyp() {
        return shaderTyp;
    }

    public void getSize(Vector3f out) {
        out.set(size);
    }

    public void destroy() {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(VBONormals);
        if(shaderTyp == ShaderTyp.WITH_TEXTURE)
            glDeleteBuffers(VBOTexture);
    }
}
