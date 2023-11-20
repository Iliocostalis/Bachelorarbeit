package org.example.virtualEnvironment;

import com.google.gson.Gson;
import org.example.jsonClasses.*;
import org.example.sensors.Sensor;
import org.example.sensors.SensorCreator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnvironmentLoader {

    private static Map<String, Mesh> meshes;

    public static String getFilenameWithoutExtension(Path path) {
        String fileName = path.getFileName().toString();
        int index = fileName.lastIndexOf('.');
        if(index == -1)
            return fileName;
        else
            return fileName.substring(0, index);
    }

    private static List<Path> getFilesInFolder(String folderName) throws IOException {

        List<Path> fileNames = null;
        try (Stream<Path> paths = Files.walk(Paths.get("assets", folderName))) {
            fileNames = paths.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        return fileNames;
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

    public static void loadMeshes() {
        if(meshes == null)
            meshes = new HashMap<>();

        try {
            List<Path> files = getFilesInFolder("meshes");

            Gson g = new Gson();
            for(Path path : files) {
                String file = readFile(path);
                JsonObject jsonObject = g.fromJson(file, JsonObject.class);

                meshes.put(getFilenameWithoutExtension(path), new Mesh(jsonObject));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static VirtualObject getVirtualObject(String name) {
        return new VirtualObject(meshes.get(name));
    }

    public static Environment loadEnvironment(String name) {
        Environment environment = new Environment(name);

        try {
            Path path = Paths.get("assets", name + ".json");
            if(!Files.exists(path))
                System.out.println("File not found: " + path.toString());

            String file = readFile(path);

            Gson g = new Gson();

            JsonAll jsonAll = g.fromJson(file, JsonAll.class);

            ArrayList<Sensor> sensoren = new ArrayList<>();
            for(JsonSensor jsonSensor : jsonAll.car.sensors) {
                Sensor sensor = SensorCreator.create(jsonSensor);
                if(sensor != null)
                    sensoren.add(sensor);
            }

            environment.auto = new Auto(jsonAll.car, sensoren);

            for(JsonObject jsonObject : jsonAll.objects)
                environment.objects.add(new VirtualObject(jsonObject));

        } catch (IOException e) {
            System.out.println("Error while loading environment!");
        }

        return environment;
    }

    public static void destroyMeshes() {
        for(Mesh mesh : meshes.values())
            mesh.destroy();

        meshes.clear();
    }
}
