package org.example;

import java.io.BufferedReader;
import java.io.IOException;

public class UserCommandOperator {

    public static void update(BufferedReader reader)
    {
        try {
            if(reader.ready())
            {
                String input = reader.readLine();
                String[] split = input.split(" ");
                String command = split[0];

                switch(command)
                {
                    case "reload" -> reload(split);
                    case "load" -> load(split);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reload(String[] split)
    {
        var time = System.currentTimeMillis();
        Umgebung.umgebung.destroy();
        UmgebungsLader.reload();
        Umgebung.umgebung = UmgebungsLader.getEnviroment(Umgebung.umgebung.sceneName);
        System.out.println(System.currentTimeMillis()-time);
    }

    private static void load(String[] split)
    {
        if(split.length <= 1)
        {
            System.out.println("load error");
            return;
        }

        var time = System.currentTimeMillis();
        Umgebung.umgebung.destroy();
        UmgebungsLader.reload();
        Umgebung.umgebung = UmgebungsLader.getEnviroment(split[1]);
        System.out.println(System.currentTimeMillis()-time);
    }
}
