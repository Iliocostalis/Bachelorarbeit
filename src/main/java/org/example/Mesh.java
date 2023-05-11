package org.example;

import org.example.assets.JsonMesh;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL40;

public class Mesh {
    private ShaderTyp shaderTyp;
    private int VBO;
    private int VBOTexture;
    private int VAO;
    private int vertexCount;

    private Vector3f size;

    private int textureId;

    Mesh(JsonMesh jsonMesh)
    {
        this(jsonMesh.vertex, jsonMesh.texture, jsonMesh.texturePath);
    }

    Mesh(float[] positionen, float[] texturKoordinaten, String texturePath)
    {
        boolean hasTexture = texturKoordinaten.length > 0 && !texturePath.equals("");

        if(hasTexture)
            generateMesh(positionen, texturKoordinaten, TextureLoader.getInstance().loadTexture(texturePath));
        else
            generateMesh(positionen, null, 0);
    }

    Mesh(float[] positionen, float[] texturKoordinaten, int textureId)
    {
        generateMesh(positionen, texturKoordinaten, textureId);
    }

    private void generateMesh(float[] positionen, float[] texturKoordinaten, int textureId)
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

            GL40.glVertexAttribPointer(1, 2, GL40.GL_FLOAT, false, 2 * Float.BYTES, 0);
            GL40.glEnableVertexAttribArray(1);
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
