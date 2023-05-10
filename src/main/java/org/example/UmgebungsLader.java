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

public class UmgebungsLader {


    private static HashMap<String, JsonScene> scenes;
    private static HashMap<String, JsonMap> maps;
    private static HashMap<String, JsonObjekt> objects;
    private static HashMap<String, JsonCar> cars;
    private static HashMap<String, JsonSensor> sensors;

    public static void load()
    {
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

        for(Path path : files)
        {
            String file = readFile(path);
            Gson g = new Gson();
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

    public static Umgebung getEnviroment(String scene)
    {
        Umgebung umgebung = new Umgebung();

        JsonScene jsonScene = scenes.get(scene);
        if(jsonScene == null)
            throw new IllegalArgumentException();

        JsonMap jsonMap = maps.get(jsonScene.enviroment);
        if(jsonMap == null)
            throw new RuntimeException();

        JsonCar jsonCar = cars.get(jsonScene.car.name);
        if(jsonCar == null)
            throw new RuntimeException();

        //umgebung.auto = new Auto();
        //umgebung.auto.transformation

        umgebung.objekte.clear();

        for(JsonChild child : jsonMap.objekts)
        {
            Objekt objekt = new Objekt();
            objekt.transformation.position.x = child.position[0];
            objekt.transformation.position.y = child.position[1];
            objekt.transformation.position.z = child.position[2];

            objekt.transformation.quaternion.rotateXYZ((float)Math.toRadians(child.rotation[0]), (float)Math.toRadians(child.rotation[1]), (float)Math.toRadians(child.rotation[2]));
            objekt.transformation.calculateMatrix();

            JsonObjekt jsonObjekt = objects.get(child.name);
            if(jsonObjekt == null)
                throw new RuntimeException();

            JsonMesh jsonMesh = jsonObjekt.mesh;
            objekt.mesh = new Mesh(jsonMesh.vertex, jsonMesh.texture, jsonMesh.texturePath);

            umgebung.objekte.add(objekt);
        }

        Auto auto = new Auto();
        auto.transformation.position.x = jsonScene.car.position[0];
        auto.transformation.position.y = jsonScene.car.position[1];
        auto.transformation.position.z = jsonScene.car.position[2];

        auto.transformation.quaternion.rotateXYZ((float)Math.toRadians(jsonScene.car.rotation[0]), (float)Math.toRadians(jsonScene.car.rotation[1]), (float)Math.toRadians(jsonScene.car.rotation[2]));
        auto.transformation.calculateMatrix();

        JsonMesh jsonMesh = jsonCar.mesh;
        auto.mesh = new Mesh(jsonMesh.vertex, jsonMesh.texture, jsonMesh.texturePath);

        umgebung.auto = auto;



        //umgebung.objekte;

        return umgebung;
    }
}
