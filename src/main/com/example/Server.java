package com.example;

import com.cpa.ClassCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sidhavratha on 28/12/14.
 */
public class Server {

    public static void main(String args[]) throws IOException, InterruptedException {
        int serverSocket = Integer.parseInt(args[0]);
        System.out.println("Server starting at : " + serverSocket);
        final ServerSocket ss = new ServerSocket(serverSocket);
        Thread waitingConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    System.out.println("Server : Waiting for connection");
                    socket = ss.accept();
                    System.out.println("Server : received connection");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
                System.out.println("At server id : " + ClassCollector.getId()+ " : "+ClassCollector.getClasses());
                String line = null;
                try {
                    while((line=br.readLine())!=null)
                    {
                        System.out.println("At server : " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ServerA serverA = new ServerA();
                serverA.printHello();
                ServerB.printHello();
                System.out.println("At server id : " + ClassCollector.getId()+ " : "+ClassCollector.getClasses());

            }
        });
        waitingConnectionThread.start();
        waitingConnectionThread.join();
    }
    public static class ServerA
    {
        public void printHello()
        {
            System.out.println("At server : ServerA Hello");
        }
    }

    public static class ServerB
    {
        public static void printHello()
        {
            System.out.println("At server : ServerB Hello");
        }
    }

}
