package org.example;

import org.example.assets.JsonMesh;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL40;

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

    Mesh(JsonMesh jsonMesh)
    {
        this(jsonMesh.vertex, jsonMesh.normal, jsonMesh.texture, jsonMesh.texturePath);
    }

    Mesh(float[] positionen, float[] normals, float[] texturKoordinaten, String texturePath)
    {
        this.vertices = positionen.clone();
        this.normals = normals.clone();
        this.textures = texturKoordinaten.clone();

        boolean hasTexture = texturKoordinaten.length > 0 && !texturePath.equals("");

        if(hasTexture)
            generateMesh(positionen, normals, texturKoordinaten, TextureLoader.getInstance().loadTexture(texturePath));
        else
            generateMesh(positionen, normals, null, 0);

    }

    Mesh(float[] positionen, float[] normals, float[] texturKoordinaten, int textureId)
    {
        this.vertices = positionen.clone();
        this.normals = normals.clone();
        this.textures = texturKoordinaten.clone();

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
        VAO = GL40.glGenVertexArrays();
        GL40.glBindVertexArray(VAO);

        VBO = GL40.glGenBuffers();
        GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, VBO);
        GL40.glBufferData(GL40.GL_ARRAY_BUFFER, positionen, GL40.GL_STATIC_DRAW);

        GL40.glVertexAttribPointer(0, 3, GL40.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL40.glEnableVertexAttribArray(0);

        VBONormals = GL40.glGenBuffers();
        GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, VBONormals);
        GL40.glBufferData(GL40.GL_ARRAY_BUFFER, normals, GL40.GL_STATIC_DRAW);

        GL40.glVertexAttribPointer(1, 3, GL40.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL40.glEnableVertexAttribArray(1);

        if(texturKoordinaten == null)
        {
            shaderTyp = ShaderTyp.NORMAL;
        }
        else
        {
            shaderTyp = ShaderTyp.MIT_TEXTUR;

            VBOTexture = GL40.glGenBuffers();

            GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, VBOTexture);
            GL40.glBufferData(GL40.GL_ARRAY_BUFFER, texturKoordinaten, GL40.GL_STATIC_DRAW);

            GL40.glVertexAttribPointer(2, 2, GL40.GL_FLOAT, false, 2 * Float.BYTES, 0);
            GL40.glEnableVertexAttribArray(2);
        }

        GL40.glBindVertexArray(0);
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
}
