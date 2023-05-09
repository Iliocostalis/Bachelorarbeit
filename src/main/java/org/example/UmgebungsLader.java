package org.example;

import com.google.gson.Gson;
import org.example.assets.JsonScene;

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


    //private static HashMap<String, String> scenes;
    private static HashMap<String, String> maps;
    private static HashMap<String, String> objects;
    private static HashMap<String, String> cars;
    private static HashMap<String, String> sensors;
    private static List<JsonScene> scenes;

    public static void load()
    {
        try {
            scenes = loadFilesInFolder("scenes", JsonScene.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> loadFilesInFolder(String folderName, Class<T> classOfT) throws IOException {

        ArrayList<T> list = new ArrayList<>();

        List<Path> files = getFilesInFolder(folderName);

        for(Path path : files)
        {
            String file = readFile(path);
            Gson g = new Gson();
            list.add(g.fromJson(file, classOfT));
        }

        return list;
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
}
