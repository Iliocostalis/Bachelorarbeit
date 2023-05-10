package org.example;

import org.lwjgl.opengl.GL40;

public class Mesh {
    float[] positionen;
    float[] texturKoordinaten;
    ShaderTyp shaderTyp;
    int VBO;
    int VBOTexture;
    int VAO;
    int vertexCount;

    int textureId;

    Mesh(float[] positionen, float[] texturKoordinaten, String texturePath)
    {
        boolean hasTexture = false;
        hasTexture = texturKoordinaten.length > 0 && !texturePath.equals("");

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
        vertexCount = positionen.length / 3;
        this.textureId = textureId;


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
}
