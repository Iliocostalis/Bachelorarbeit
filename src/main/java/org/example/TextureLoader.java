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

    int loadTexture(String path)
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
}


/*

import bpy


class Object:
    def __init__(self):
        self.vertices = []
        self.normals = []
        self.textureCoords = []


    def load(self, data):
        verticesList = []
        normalList = []
        textureList = []

        uv_layer = data.uv_layers.active.data

        for v in data.vertices:
            verticesList.append(v.co)

        for n in data.vertex_normals:
            normalList.append(n.vector)

        for uv in uv_layer:
            textureList.append(uv.uv)

        for poly in data.polygons:
            for i in range(len(poly.vertices) - 2):
                self.vertices.append(verticesList[poly.vertices[0]])
                self.vertices.append(verticesList[poly.vertices[i+1]])
                self.vertices.append(verticesList[poly.vertices[i+2]])

        for poly in data.polygons:
            for i in range(len(poly.vertices) - 2):
                self.normals.append(normalList[poly.vertices[0]])
                self.normals.append(normalList[poly.vertices[i+1]])
                self.normals.append(normalList[poly.vertices[i+2]])

        index = 0
        for poly in data.polygons:
            for i in range(len(poly.vertices) - 2):
                self.textureCoords.append(textureList[index])
                self.textureCoords.append(textureList[index+i+1])
                self.textureCoords.append(textureList[index+i+2])

            index = index + len(poly.vertices)



    def print(self):
        string = ""
        string += "\"vertex\": ["

        isFirstRun = True
        for v in self.vertices:
            if isFirstRun:
                isFirstRun = False
            else:
                string += ","
            string += f"{v.x:.4f},{v.z:.4f},{-v.y:.4f}"

        string += "],\n\"normal\": ["

        isFirstRun = True
        for v in self.normals:
            if isFirstRun:
                isFirstRun = False
            else:
                string += ","
            string += f"{v.x:.4f},{v.z:.4f},{-v.y:.4f}"

        string += "],\n\"texture\": ["

        isFirstRun = True
        for v in self.textureCoords:
            if isFirstRun:
                isFirstRun = False
            else:
                string += ","
            string += f"{v.x:.4f},{1-v.y:.4f}"

        string += "],"

        print(string)





print(bpy.context.selected_objects[0].data.vertex_normals)
print(bpy.context.selected_objects[0].data.vertices)


for v in bpy.context.selected_objects[0].data.vertex_normals:
    print(v.vector)

#for poly in bpy.context.selected_objects[0].data.polygons:
#    counter = counter + 1

    #print(poly.loop_indices)

#    for i in range(len(poly.vertices) - 2):
#        printVector(mylist[poly.vertices[0]])
#        printVector(mylist[poly.vertices[i+1]])
#        printVector(mylist[poly.vertices[i+2]])


print("")
print("")
print("")
print("")

object = Object()
object.load(bpy.context.selected_objects[0].data)
object.print()

 */