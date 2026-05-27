package org.example;

import org.example.Uitils.BackendServers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class LoadBalancer {
    private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class.getName());

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            BackendServers.setAlgorithm("RANDOM");
            LOGGER.info(String.format("Load Balancer Started at port %d...!", 8081));

            while(true) {


                Socket socket = serverSocket.accept();
                LOGGER.info("TCP connection established with client  :" + socket.toString());
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
