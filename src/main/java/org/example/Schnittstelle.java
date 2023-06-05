package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
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
    byte[] byteBufferA;

    int sendIndexBufferB;
    int reciveIndexBufferB;
    byte[] byteBufferB;

    boolean sending = false;
    int sendingLength;


    private AtomicBoolean isRunning = new AtomicBoolean();

    public static Schnittstelle getInstance() {
        if (schnittstelle == null) {
            schnittstelle = new Schnittstelle();
        }
        return schnittstelle;
    }

    Schnittstelle()
    {
        System.out.println("Schnittstelle startet");
        byteBufferA = new byte[1000*1000*50];
        byteBufferB = new byte[1000*1000*50];
        //start();
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
            if(sending)
            {
                outputStream.write(byteBufferA, 0, sendingLength);
                sending = false;
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

    LinkedList<Sensor> sensoren;

    public void sendeSensorDaten(Sensor sensor)
    {
        sensoren.add(sensor);
    }

    private void senden()
    {
        Sensor sensor = sensoren.pop();


    }

    public void senden(byte[] data)
    {
        if(sending)
            return;

        sendingLength = data.length;
        System.arraycopy(data, 0, byteBufferA, 0, data.length);
        sending = true;
    }
}
