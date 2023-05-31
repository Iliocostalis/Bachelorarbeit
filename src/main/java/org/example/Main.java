package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Schnittstelle.getInstance().start();

        Fenster fenster = new Fenster();
        UmgebungsLader.load();
        Umgebung.umgebung = UmgebungsLader.getEnviroment("plugin");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(fenster.istOffen())
        {
            UserCommandOperator.update(reader);
            fenster.update();
        }

        Schnittstelle.getInstance().stop();
    }
}