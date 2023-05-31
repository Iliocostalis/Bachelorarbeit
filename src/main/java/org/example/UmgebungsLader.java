package org.example;

import com.google.gson.Gson;
import org.example.assets.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.glFlush;

public class UmgebungsLader {


    private static HashMap<String, JsonScene> scenes;
    private static HashMap<String, JsonMap> maps;
    private static HashMap<String, JsonMesh> meshs;
    private static HashMap<String, JsonCar> cars;
    private static HashMap<String, JsonSensor> sensors;


    private static HashMap<Integer, Mesh> loadedMeshs;

    public static void load()
    {
        loadedMeshs = new HashMap<>();
        try {
            scenes = loadFilesInFolder("scenes", JsonScene.class);
            maps = loadFilesInFolder("maps", JsonMap.class);
            meshs = loadFilesInFolder("meshs", JsonMesh.class);
            cars = loadFilesInFolder("cars", JsonCar.class);
            sensors = loadFilesInFolder("sensors", JsonSensor.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends JsonSuperClass> HashMap<String, T> loadFilesInFolder(String folderName, Class<T> classOfT) throws IOException {

        HashMap<String, T> map = new HashMap<>();

        List<Path> files = getFilesInFolder(folderName);

        Gson g = new Gson();
        for(Path path : files)
        {
            String file = readFile(path);
            T t = g.fromJson(file, classOfT);
            map.put(t.name, t);
        }

        return map;
    }

    private static String readFile(Path path) throws IOException {

        try(BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    private static List<Path> getFilesInFolder(String folderName) throws IOException {

        List<Path> fileNames = null;
        try (Stream<Path> paths = Files.walk(Paths.get("assets/" + folderName))) {
            fileNames = paths.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        return fileNames;
    }

    private static int loadMesh(JsonMesh jsonMesh)
    {
        int hash = jsonMesh.name.hashCode();
        if(loadedMeshs.containsKey(hash))
            return hash;

        Mesh mesh = new Mesh(jsonMesh);
        loadedMeshs.put(hash, mesh);
        return hash;
    }

    public static Umgebung getEnviroment(String scene)
    {
        Umgebung umgebung = new Umgebung(scene);

        try {
            JsonScene jsonScene = scenes.get(scene);
            if(jsonScene == null)
            {
                System.out.println("Can not read scene: " + scene);
                return umgebung;
            }

            //JsonMap jsonMap = maps.get(jsonScene.enviroment);
            //if(jsonMap == null)
            //{
            //    System.out.println("Can not read maps: " + jsonScene.enviroment);
            //    return umgebung;
            //}

            JsonCar jsonCar = cars.get(jsonScene.car.name);
            if(jsonCar == null)
            {
                System.out.println("Can not read car: " + jsonScene.car.name);
                return umgebung;
            }

            JsonMesh jsonCarObjekt = meshs.get(jsonCar.mesh_name);
            if(jsonCarObjekt == null)
            {
                System.out.println("Can not read mesh: " + jsonCar.mesh_name);
                return umgebung;
            }

            ArrayList<Sensor> sensoren = new ArrayList<>();
            for(JsonObjektInstance sensorInstance : jsonCar.sensors)
            {
                JsonSensor sensor = sensors.get(sensorInstance.name);
                if(sensor == null)
                {
                    System.out.println("Can not read sensor: " + sensorInstance.name);
                    continue;
                }
                sensoren.add(SensorCreator.create(sensor, sensorInstance));
            }


            umgebung.auto = new Auto(jsonCar, jsonScene.car, loadMesh(jsonCarObjekt), sensoren);

            for(JsonObjektInstance instance : jsonScene.objects)
            {
                JsonMesh jsonMesh = meshs.get(instance.name);
                if(jsonMesh == null)
                {
                    System.out.println("Can not read mesh: " + instance.name);
                    continue;
                }
                Objekt objekt = new Objekt(instance, loadMesh(jsonMesh));
                umgebung.objekte.add(objekt);
            }
        } catch (Exception e)
        {
            System.out.println("Fehler beim laden der Umgebung!");
        }

        return umgebung;
    }

    public static Mesh getMesh(int hash)
    {
        return loadedMeshs.get(hash);
    }

    public static void reload()
    {
        glFlush();
        scenes.clear();
        maps.clear();
        meshs.clear();
        scenes.clear();
        cars.clear();
        sensors.clear();

        for(Mesh mesh : loadedMeshs.values())
            mesh.destroy();

        loadedMeshs.clear();

        load();
    }
}


/*import bpy


# flip z (in blender its y)
def printVector(v):
    print(f"{v.x:.4f}", end='')
    print(",", end='')
    print(f"{v.z:.4f}", end='')
    print(",", end='')
    print(f"{-v.y:.4f}", end='')
    print(",", end='')

# print all objects
#for obj in bpy.data.objects:
#    print(obj.name)

bpy.context.selected_objects[0].data.vertices

for v in bpy.context.selected_objects[0].data.vertices:
    print(v.co)

#for v in bpy.context.selected_objects[0].data.vertices:
#    printVector(v.co)


#for v in bpy.context.selected_objects[0].data.vertices:
#    print(v.index)

#for poly in bpy.context.selected_objects[0].data.polygons:
#    print("Polygon index: %d, length: %d" % (poly.index, poly.loop_total))
    #for l in poly.loop_indices:
    #    print(l)
#    print("len: ")
#    print(len(poly.vertices))
#    for v in poly.vertices:
#        print(v)



mylist = []

for v in bpy.context.selected_objects[0].data.vertices:
    mylist.append(v.co)

print(mylist)

print("")

counter = 0

for poly in bpy.context.selected_objects[0].data.polygons:
    counter = counter + 1

    #print(poly.loop_indices)

    for i in range(len(poly.vertices) - 2):
        printVector(mylist[poly.vertices[0]])
        printVector(mylist[poly.vertices[i+1]])
        printVector(mylist[poly.vertices[i+2]])

    #printVector(mylist[poly.vertices[0]])
    #printVector(mylist[poly.vertices[1]])
    #printVector(mylist[poly.vertices[2]])

    #printVector(mylist[poly.vertices[0]])
    #printVector(mylist[poly.vertices[2]])
    #printVector(mylist[poly.vertices[3]])



    #print(poly.vertices)




    #for v in poly.vertices:
    #    printVector(mylist[v])

print("")
print(counter)

*/

/*
import bpy


#flip y (1 -> 0 / 0.9 -> 0.1)
def print2Vector(v):
    print(f"{v.x:.4f}", end='')
    print(",", end='')
    print(f"{1-v.y:.4f}", end='')
    print(",", end='')

# print all objects
#for obj in bpy.data.objects:
#    print(obj.name)

me = bpy.context.object.data
uv_layer = me.uv_layers.active.data

print(bpy.context.selected_objects[0].data.vertices)

print(bpy.context.selected_objects[0].data)
print(uv_layer)
print(me.uv_layers.active.name)
print(bpy.context.selected_objects[0].data.polygons)
print(bpy.context.selected_objects[0].vertex_groups)

print("")
for uv in uv_layer:
    print2Vector(uv.uv)
print("")

print(bpy.context.selected_objects[0].active_material)






mylist = []

for v in bpy.context.selected_objects[0].data.vertices:
    mylist.append(v.co)

print(mylist)
print(len(mylist))

mylist2 = []

for uv in uv_layer:
    mylist2.append(uv.uv)

print("")
index = 0
for poly in bpy.context.selected_objects[0].data.polygons:

    for i in range(len(poly.vertices) - 2):
        print2Vector(mylist2[index])
        print2Vector(mylist2[index+i+1])
        print2Vector(mylist2[index+i+2])

    #print2Vector(mylist2[index])
    #print2Vector(mylist2[index+2])
    #print2Vector(mylist2[index+3])

    index = index + len(poly.vertices)


print("")


 */