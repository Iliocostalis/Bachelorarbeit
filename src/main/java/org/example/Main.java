package org.example;

import org.example.virtualEnvironment.EnviromentLoader;
import org.example.virtualEnvironment.Umgebung;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {

        Schnittstelle.getInstance().start();

        Fenster fenster = new Fenster();
        EnviromentLoader.loadMeshs();
        Umgebung.umgebung = EnviromentLoader.loadEnviroment("env");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        long timeOffset = 0;
        long waitNanos = 10*1000*1000;
        long startTime = System.nanoTime();
        while(fenster.istOffen())
        {
            timeOffset = startTime + waitNanos - System.nanoTime();
            long nanos = Math.min(System.nanoTime() - startTime, 100*1000*1000);
            startTime = System.nanoTime();

            UserCommandOperator.update(reader);

            Umgebung.umgebung.aktualisieren(nanos);
            fenster.update(nanos);

            long waitTime = waitNanos - (System.nanoTime() - startTime) + timeOffset;
            waitTime = Math.min(waitNanos, waitTime);
            waitTime = Math.max(0, waitTime);

            //wait
            try {
                Thread.sleep(waitTime/(1000*1000));
            } catch (InterruptedException e) {
            }
        }

        Schnittstelle.getInstance().stop();
    }
}