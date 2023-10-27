package org.example;

import org.example.sensors.DataPackage;
import org.example.sensors.Sensor;
import org.example.virtualEnvironment.Umgebung;

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

    private final Object lock;

    private Thread thread;
    private static Schnittstelle schnittstelle;

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    boolean sending;

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
        lock = new Object();
        sending = false;
    }

    public void start(int port)
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
                    System.out.println("Schnittstelle error");
                } catch (IOException e) {
                    System.out.println("Schnittstelle error");
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

    private void waitForMainThreadUpdate()
    {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void signalContinueLoop()
    {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void runSenden() throws IOException
    {
        DataPackage dataPackageIn = new DataPackage(8);
        byte[] dataTmp = new byte[1024];

        while(isRunning.get())
        {
            if(inputStream.available() <= 0 && queue.isEmpty())
                waitForMainThreadUpdate();

            // send
            DataPackage dataPackage = queue.poll();
            if(dataPackage == null)
                continue;

            sensorPackageCounter[dataPackage.header[1]] -= 1;

            outputStream.write(dataPackage.header);
            outputStream.write(dataPackage.customData);

            // receive
            if(inputStream.available() > 0)
            {
                int read = 0;
                while(read < dataPackageIn.header.length)
                    read += inputStream.read(dataPackageIn.header, read, dataPackageIn.header.length - read);

                int type = dataPackageIn.header[0];
                int address = dataPackageIn.header[1];
                int readSize = ConstValues.byteArrayToInt(2, dataPackageIn.header);

                if(type == DataPackage.TYPE_CAR && readSize == 8)
                {
                    read = 0;
                    while(read < readSize)
                        read += inputStream.read(dataPackageIn.customData, read, readSize - read);

                    float throttle = ConstValues.byteArrayToFloat(0, dataPackageIn.customData);
                    float steeringAngle = ConstValues.byteArrayToFloat(4, dataPackageIn.customData);
                    Umgebung.umgebung.auto.setSpeed(throttle);
                    Umgebung.umgebung.auto.setSteeringAngle(steeringAngle);
                }
                else // wrong data -> skipp
                {
                    read = 0;
                    while(read < readSize)
                    {
                        int bytesToRead = Math.min(readSize - read, dataTmp.length);
                        read += inputStream.read(dataTmp, 0, bytesToRead);
                    }
                }
            }
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
