package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Schnittstelle.getInstance().start();

        Fenster fenster = new Fenster();
        UmgebungsLader.load();
        Umgebung.umgebung = UmgebungsLader.getEnviroment("start_settings");

        while(fenster.istOffen())
        {
            fenster.update();
        }

        Schnittstelle.getInstance().stop();
    }
}