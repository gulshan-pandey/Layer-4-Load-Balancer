package org.example;


import org.example.Uitils.BackendServers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSocketHandler implements Runnable{


    private Socket clientSocket;

    public ClientSocketHandler(final Socket socket){
        this.clientSocket  = socket;
    }

    @Override
    public void run() {
        try{
            InputStream cleintToLoadBalancerInputStream = clientSocket.getInputStream();
            OutputStream loadBalancertoClientOutputStream = clientSocket.getOutputStream();

            String BackendHost = BackendServers.getHost();
            System.out.println("Host is selected to handle this request  : " + BackendHost);

            // now to make a socket request, basically a TCP connection with the Backend Server
            Socket backendSocket  = new Socket(BackendHost, 8080);

            InputStream backendServerToLbIS = backendSocket.getInputStream();

            OutputStream lBToBackendServerOS = backendSocket.getOutputStream();
            // input stream is to read the data and output stream is to write the data

            Thread clientDataHandler = new Thread(){
                public void run(){
                    try{
                        int data;
                        while((data=cleintToLoadBalancerInputStream.read())!=1){
                            lBToBackendServerOS.write(data);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };

            clientDataHandler.start();


            // creating another thread that will read data byte by byte form the backend server to the LB and then parse it to the Client

            Thread backendDataHandler = new Thread(){
                public void run(){
                    try{
                        int data;
                        while((data=backendServerToLbIS.read())!=1){
                            loadBalancertoClientOutputStream.write(data);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };

            backendDataHandler.start();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
