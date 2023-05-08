import org.lwjgl.opengl.GL40;

public class Mesh {
    float[] positionen;
    float[] texturKoordinaten;
    ShaderTyp shaderTyp;
    int VBO;
    int VAO;
    int vertexCount;

    Mesh(float[] positionen, float[] texturKoordinaten)
    {
        if(texturKoordinaten == null)
            shaderTyp = ShaderTyp.NORMAL;
        else
            shaderTyp = ShaderTyp.MIT_TEXTUR;

        vertexCount = positionen.length / 3;


        VBO = GL40.glGenBuffers();

        VAO = GL40.glGenVertexArrays();

        GL40.glBindVertexArray(VAO);

        GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, VBO);
        GL40.glBufferData(GL40.GL_ARRAY_BUFFER, positionen, GL40.GL_STATIC_DRAW);

        GL40.glVertexAttribPointer(0, vertexCount, GL40.GL_FLOAT, false, 12, 0);
        GL40.glEnableVertexAttribArray(0);

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
}
