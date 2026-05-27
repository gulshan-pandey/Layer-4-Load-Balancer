package org.example.Uitils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class BackendServers {

    private static List<String> servers = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(BackendServers.class.getName());
    private static final Random RANDOM = new Random();

    private static  int count =0;
    private static String algorithm = "ROUND_ROBIN";

   static{
       servers.add("IP1");
       servers.add("IP2");
   }

   public static String getHost(){
       if ("RANDOM".equalsIgnoreCase(algorithm)) {
           int index = RANDOM.nextInt(servers.size());
           String host = servers.get(index);
           LOGGER.info("Selected backend using RANDOM : " + host);
           return host;
       }

       String host = servers.get(count% servers.size());    // Round-Robin fashion
       count++;
       LOGGER.info("Selected backend using ROUND_ROBIN : " + host);
       return host;
   }

   public static void setAlgorithm(String selectedAlgorithm){
       if ("RANDOM".equalsIgnoreCase(selectedAlgorithm) || "ROUND_ROBIN".equalsIgnoreCase(selectedAlgorithm)) {
           algorithm = selectedAlgorithm.toUpperCase();
           LOGGER.info("Load balancing algorithm set to : " + algorithm);
           return;
       }
       LOGGER.warning("Invalid algorithm '" + selectedAlgorithm + "'. Falling back to ROUND_ROBIN");
       algorithm = "ROUND_ROBIN";
   }


}
