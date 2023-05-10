package org.example;

import com.google.gson.Gson;
import org.example.assets.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UmgebungsLader {


    private static HashMap<String, JsonScene> scenes;
    private static HashMap<String, JsonMap> maps;
    private static HashMap<String, JsonObjekt> objects;
    private static HashMap<String, JsonCar> cars;
    private static HashMap<String, JsonSensor> sensors;


    private static HashMap<Integer, Mesh> meshs;

    public static void load()
    {
        meshs = new HashMap<>();
        try {
            scenes = loadFilesInFolder("scenes", JsonScene.class);
            maps = loadFilesInFolder("maps", JsonMap.class);
            objects = loadFilesInFolder("objekts", JsonObjekt.class);
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

    private static int loadMesh(JsonObjekt jsonObjekt)
    {
        int hash = jsonObjekt.name.hashCode();
        if(meshs.containsKey(hash))
            return hash;

        Mesh mesh = new Mesh(jsonObjekt.mesh);
        meshs.put(hash, mesh);
        return hash;
    }

    public static Umgebung getEnviroment(String scene)
    {
        Umgebung umgebung = new Umgebung();

        JsonScene jsonScene = scenes.get(scene);
        if(jsonScene == null)
            throw new IllegalArgumentException();

        JsonMap jsonMap = maps.get(jsonScene.enviroment);
        if(jsonMap == null)
            throw new RuntimeException();

        JsonObjektInstance jsonCarInstance = jsonScene.car;

        JsonCar jsonCar = cars.get(jsonCarInstance.name);
        if(jsonCar == null)
            throw new RuntimeException();

        JsonObjektInstance[] jsonObjektInstances = jsonMap.objekts;

        for(JsonObjektInstance child : jsonObjektInstances)
        {
            JsonObjekt jsonObjekt = objects.get(child.name);
            if(jsonObjekt == null)
                throw new RuntimeException();

            int meshId = loadMesh(jsonObjekt);

            Objekt objekt = new Objekt(child, meshId);

            umgebung.objekte.add(objekt);
        }

        int meshId = loadMesh(jsonCar);

        umgebung.auto = new Auto(jsonCarInstance, meshId);



        return umgebung;
    }

    public static Mesh getMesh(int hash)
    {
        return meshs.get(hash);
    }
}
