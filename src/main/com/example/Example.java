package com.example;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sidhavratha on 27/12/14.
 */
public class Example {

    public static class ExampleA {

        public String printHelloWorld()
        {
            return "Hello world A";
        }

    }

    public static class ExampleB {

        public String printHelloWorld()
        {
            return "Hello world B";
        }

    }

    public void serverClient()
    {
        final int PORT = 8010;

        final StringBuffer address = new StringBuffer();
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                Socket sc = null;
                try {
                    serverSocket = new ServerSocket(PORT);
                    sc = serverSocket.accept();
                    System.out.println("Connection received at server : " + getScoketInfo(sc));
                    address.append(sc.getInetAddress().getHostAddress()+":"+sc.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (serverSocket != null) {
                            serverSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        });
        serverThread.start();
        final StringBuffer clientAddress = new StringBuffer();
        Socket clientSocket = null;
        try {
            clientSocket = new Socket("localhost", PORT);
            PrintStream ps = new PrintStream(clientSocket.getOutputStream());
            ps.write("Hello from client.".getBytes());
            System.out.println("Connection received at client : " + getScoketInfo(clientSocket));
            clientAddress.append(clientSocket.getLocalAddress().getHostAddress()+":"+clientSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String getScoketInfo(Socket sc) {
        return sc.getInetAddress() + ":" + sc.getPort() + ":" + sc.getLocalAddress() + ":" + sc.getLocalPort();
    }



}
