package org.example;

import org.example.assets.JsonMesh;
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

    Mesh(JsonMesh jsonMesh)
    {
        this(jsonMesh.vertex, jsonMesh.normal, jsonMesh.color, jsonMesh.texture, jsonMesh.texturePath);
    }

    Mesh(float[] positionen, float[] normals, float[] color, float[] texturKoordinaten, String texturePath)
    {
        this.vertices = positionen.clone();
        this.normals = normals.clone();
        this.textures = texturKoordinaten.clone();
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];

        boolean hasTexture = texturKoordinaten.length > 0 && !texturePath.equals("");

        if(hasTexture)
            generateMesh(positionen, normals, texturKoordinaten, TextureLoader.getInstance().loadTexture(texturePath));
        else
            generateMesh(positionen, normals, null, 0);

    }

    Mesh(float[] positionen, float[] normals, float[] color, float[] texturKoordinaten, int textureId)
    {
        this.vertices = positionen.clone();
        this.normals = normals.clone();
        this.textures = texturKoordinaten.clone();
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];

        generateMesh(positionen, normals, texturKoordinaten, textureId);
    }

    private void generateMesh(float[] positionen, float[] normals, float[] texturKoordinaten, int textureId)
    {
        // calculate size
        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;
        float zMin = Float.MAX_VALUE;
        float xMax = -Float.MAX_VALUE;
        float yMax = -Float.MAX_VALUE;
        float zMax = -Float.MAX_VALUE;
        for(int i = 0; i < positionen.length; i+=3)
        {
            float x = positionen[i];
            float y = positionen[i+1];
            float z = positionen[i+2];

            xMin = Math.min(xMin, x);
            yMin = Math.min(yMin, y);
            zMin = Math.min(zMin, z);

            xMax = Math.max(xMax, x);
            yMax = Math.max(yMax, y);
            zMax = Math.max(zMax, z);
        }
        size = new Vector3f(xMax-xMin, yMax-yMin, zMax-zMin);

        vertexCount = positionen.length / 3;
        this.textureId = textureId;

        // load mesh to gpu
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, positionen, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        VBONormals = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBONormals);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        if(texturKoordinaten == null)
        {
            shaderTyp = ShaderTyp.NORMAL;
        }
        else
        {
            shaderTyp = ShaderTyp.MIT_TEXTUR;

            VBOTexture = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, VBOTexture);
            glBufferData(GL_ARRAY_BUFFER, texturKoordinaten, GL_STATIC_DRAW);

            glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            glEnableVertexAttribArray(2);
        }

        glBindVertexArray(0);
    }

    public int getVAO()
    {
        return VAO;
    }

    public int getVertexCount()
    {
        return vertexCount;
    }

    public int getTextureId()
    {
        return textureId;
    }

    public ShaderTyp getShaderTyp()
    {
        return shaderTyp;
    }

    public void getSize(Vector3f out)
    {
        out.set(size);
    }

    public void destroy()
    {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(VBONormals);
        if(shaderTyp == ShaderTyp.MIT_TEXTUR)
            glDeleteBuffers(VBOTexture);
    }
}
