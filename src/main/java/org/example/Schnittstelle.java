package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.LinkedList;

public class Schnittstelle {

    private Thread thread;
    private static Schnittstelle schnittstelle;

    private InputStream inputStream;
    private OutputStream outputStream;


    private boolean isRunnung;

    public static Schnittstelle getInstance() {
        if (schnittstelle == null) {
            schnittstelle = new Schnittstelle();
        }
        return schnittstelle;
    }

    Schnittstelle()
    {
        System.out.println("Schnittstelle startet");
        start();
    }

    public void start()
    {
        isRunnung = true;

        thread = new Thread(() -> {
            System.out.println("Schnittstelle läuft!");
        });
    }

    public void stop()
    {
        isRunnung = false;
    }

    public void runSenden()
    {
        while(isRunnung)
        {
            //outputStream.write(1);
        }
    }

    public void runEmpfangen()
    {
        System.out.println("Schnittstelle läuft!");

        while(isRunnung)
        {
            //inputStream.read();
        }
        /*if(sendImage)
        {
            try {
                serverSocket = new ServerSocket(25131);
                clientSocket = serverSocket.accept();
                os = clientSocket.getOutputStream();
                is = clientSocket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/
    }

    LinkedList<Sensor> sensoren;

    public void sendeSensorDaten(Sensor sensor)
    {
        sensoren.add(sensor);
    }

    private void senden()
    {
        Sensor sensor = sensoren.pop();


    }
}
