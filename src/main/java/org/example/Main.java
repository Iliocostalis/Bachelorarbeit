package org.example;

import org.example.virtualEnvironment.EnvironmentLoader;
import org.example.virtualEnvironment.Environment;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args)
    {
        StartConfiguration startConfiguration = new StartConfiguration();
        startConfiguration.parseArgs(args);

        Schnittstelle.getInstance().start(startConfiguration.port);

        Window window = new Window(startConfiguration.width, startConfiguration.height);
        EnvironmentLoader.loadMeshes();
        Environment.environment = EnvironmentLoader.loadEnvironment(startConfiguration.startEnvironment);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        long waitTimePerUpdate = 2*1000*1000;
        long updateCount = 0;
        long startTime = System.nanoTime();
        long timeLastUpdate = System.nanoTime();

        while(window.isOpen())
        {
            Schnittstelle.getInstance().signalContinueLoop();

            long timeNow = System.nanoTime();
            long deltaTime = timeNow - timeLastUpdate;
            timeLastUpdate = timeNow;

            UserCommandOperator.update(reader);

            Environment.environment.update(deltaTime);
            window.update(deltaTime);

            updateCount += 1;
            long endTimeNextUpdate = startTime + updateCount * waitTimePerUpdate;

            //if(updateCount % 500 == 0)
            //    System.out.println("sec");

            //wait
            while(System.nanoTime() < endTimeNextUpdate)
            {
                Thread.onSpinWait();
            }
        }

        Schnittstelle.getInstance().stop();
    }
}