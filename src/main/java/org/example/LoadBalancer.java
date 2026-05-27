package org.example;

import org.example.Uitils.BackendServers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class LoadBalancer {
    private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class.getName());

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getProperty("lb.port", "8081"));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Load Balancer started on port " + port + " with algorithm " + BackendServers.getAlgorithm());

            while (true) {
                Socket socket = serverSocket.accept();
                LOGGER.info("TCP connection established with client: " + socket);
                handleSocket(socket);
            }
        }
    }

    private static void handleSocket(Socket socket) {

        ClientSocketHandler clientSocketHandler = new ClientSocketHandler(socket);
        Thread clientSocketHandlerThread = new Thread(clientSocketHandler);
        clientSocketHandlerThread.start();

    }
}
