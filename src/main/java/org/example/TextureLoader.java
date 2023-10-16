package org.example;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL40.*;

public class TextureLoader {
    private static TextureLoader textureLoader;

    public static TextureLoader getInstance()
    {
        if(textureLoader == null)
        {
            textureLoader = new TextureLoader();
        }

        return textureLoader;
    }

    IntBuffer intBufferX = BufferUtils.createIntBuffer(1);
    IntBuffer intBufferY = BufferUtils.createIntBuffer(1);
    IntBuffer intBufferChannelsInFile = BufferUtils.createIntBuffer(1);

    HashMap<String, Integer> map;

    private TextureLoader()
    {
        map = new HashMap<String, Integer>();
    }

    public int loadTexture(String path)
    {
        Integer imageId = map.get(path);
        if(imageId != null)
            return imageId;

        ByteBuffer image = STBImage.stbi_load(path, intBufferX, intBufferY, intBufferChannelsInFile, 3);

        if(image == null)
            throw new NullPointerException();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, intBufferX.get(0), intBufferY.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        //glGenerateMipmap(GL_TEXTURE_2D);

        STBImage.stbi_image_free(image);


        map.put(path, textureId);
        return textureId;
    }

    public void clean()
    {

    }
}