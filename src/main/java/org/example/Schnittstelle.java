package org.example;

import org.example.sensors.DataPackage;
import org.example.sensors.Sensor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Schnittstelle {

    private Thread thread;
    private static Schnittstelle schnittstelle;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    int sendIndexBufferA;
    int reciveIndexBufferA;

    int sendIndexBufferB;
    int reciveIndexBufferB;

    boolean sending = false;
    int sendingLength;

    Queue<DataPackage> queue;
    int[] sensorPackageCounter;


    private AtomicBoolean isRunning;

    public static Schnittstelle getInstance() {
        if (schnittstelle == null) {
            schnittstelle = new Schnittstelle();
        }
        return schnittstelle;
    }

    Schnittstelle()
    {
        queue = new ConcurrentLinkedQueue<>();
        sensorPackageCounter = new int[128];
        isRunning = new AtomicBoolean();
    }

    public void start()
    {
        thread = new Thread(() -> {
            isRunning.set(true);

            try {
                serverSocket = new ServerSocket(26134);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(isRunning.get())
            {
                System.out.println("Schnittstelle läuft!");
                try {
                    sending = false;
                    isRunning.set(false);
                    clientSocket = serverSocket.accept();
                    outputStream = clientSocket.getOutputStream();
                    inputStream = clientSocket.getInputStream();
                    isRunning.set(true);
                    runSenden();
                } catch (SocketException e) {
                    //return;
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }
        });

        thread.start();
    }

    public void stop()
    {
        if(isRunning.get())
        {
            isRunning.set(false);
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void runSenden() throws IOException {
        //System.out.println("sending");
        while(isRunning.get())
        {
            DataPackage dataPackage = queue.poll();
            if(dataPackage == null)
                continue;

            outputStream.write(dataPackage.header);
            outputStream.write(dataPackage.customData);
        }
    }

    public void runEmpfangen()
    {
        System.out.println("Schnittstelle läuft!");

        while(isRunning.get())
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

    public void senden(DataPackage dataPackage, int maxQueueCount)
    {
        int address = dataPackage.header[1];
        if(address < 0 || address >= sensorPackageCounter.length) {
            System.out.println("Wrong address: " + address);
            return;
        }

        if(sensorPackageCounter[address] < maxQueueCount)
        {
            sensorPackageCounter[address] += 1;
            queue.add(new DataPackage(dataPackage));
        }
    }
}
