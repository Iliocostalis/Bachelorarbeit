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
        Umgebung.umgebung = EnviromentLoader.loadEnviroment("nxp");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        long waitTimePerUpdate = 2*1000*1000;
        long updateCount = 0;
        long startTime = System.nanoTime();
        long timeLastUpdate = System.nanoTime();

        while(fenster.istOffen())
        {
            long timeNow = System.nanoTime();
            long deltaTime = timeNow - timeLastUpdate;
            timeLastUpdate = timeNow;

            UserCommandOperator.update(reader);

            Umgebung.umgebung.aktualisieren(deltaTime);
            fenster.update(deltaTime);

            updateCount += 1;
            long endTimeNextUpdate = startTime + updateCount * waitTimePerUpdate;

            if(updateCount % 500 == 0)
                System.out.println("sec");

            //wait
            while(System.nanoTime() < endTimeNextUpdate)
            {
                Thread.onSpinWait();
            }
        }

        Schnittstelle.getInstance().stop();
    }
}