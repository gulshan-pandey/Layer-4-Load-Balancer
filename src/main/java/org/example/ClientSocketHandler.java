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

            // now to make a socket request, basically a TCP connection to the Backend Server
            Socket backendSocket  = new Socket(BackendHost, 8080);

            InputStream LoadBalancerToBackendInputStream = backendSocket.getInputStream();

            OutputStream BackendToLoadBalancerOutputStream = backendSocket.getOutputStream();

            Thread clientDataHandler = new Thread(){
                public void run(){
                    try{
                        int data;
                        while((data=cleintToLoadBalancerInputStream.read())!=1){

                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
