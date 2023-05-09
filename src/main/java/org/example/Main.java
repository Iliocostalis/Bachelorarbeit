package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Schnittstelle.getInstance().start();

        UmgebungsLader.load();
        Fenster fenster = new Fenster();

        while(fenster.istOffen())
        {
            fenster.update();
        }
    }
}