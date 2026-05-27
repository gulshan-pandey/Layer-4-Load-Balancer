package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class LoadBalancer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.format("Load Balancer Started at port %d...!", 8081);

        while(true) {


            Socket socket = serverSocket.accept();
            System.out.println("TCP connection established with client  :"+ socket.toString());


        }
    }
}
