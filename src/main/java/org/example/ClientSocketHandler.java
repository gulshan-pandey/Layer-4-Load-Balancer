package org.example;


import org.example.Uitils.BackendServers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientSocketHandler implements Runnable{


    private static final Logger LOGGER = Logger.getLogger(ClientSocketHandler.class.getName());
    private final Socket clientSocket;

    public ClientSocketHandler(final Socket socket){
        this.clientSocket  = socket;
    }

    @Override
    public void run() {
        String backendHost = BackendServers.getHost();
        BackendServers.registerConnection(backendHost);
        LOGGER.info("Routing connection to backend: " + backendHost);

        try {
            InputStream clientToLoadBalancerInputStream = clientSocket.getInputStream();
            OutputStream loadBalancerToClientOutputStream = clientSocket.getOutputStream();

            // open TCP connection to backend server
            try (Socket backendSocket = new Socket(backendHost, 8080)) {
                InputStream backendServerToLbIS = backendSocket.getInputStream();
                OutputStream lBToBackendServerOS = backendSocket.getOutputStream();

                Thread clientDataHandler = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        forwardStreamData(clientToLoadBalancerInputStream, lBToBackendServerOS);
                    }
                });

                Thread backendDataHandler = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        forwardStreamData(backendServerToLbIS, loadBalancerToClientOutputStream);
                    }
                });

                clientDataHandler.start();
                backendDataHandler.start();

                clientDataHandler.join();
                backendDataHandler.join();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Socket handling failed for backend " + backendHost, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Connection worker thread interrupted", e);
        } finally {
            BackendServers.releaseConnection(backendHost);
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to close client socket", e);
            }
            LOGGER.info("Connection finished for backend: " + backendHost);
        }
    }

    private void forwardStreamData(InputStream source, OutputStream target) {
        try {
            int data;
            while ((data = source.read()) != -1) {
                target.write(data);
            }
            target.flush();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Stream forwarding finished", e);
        }
    }
}
