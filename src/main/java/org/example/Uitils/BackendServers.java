package org.example.Uitils;

import java.util.ArrayList;
import java.util.List;

public class BackendServers {

    private static List<String> servers = new ArrayList<>();

    private static  int count =0;

   static{
       servers.add("IP1");
       servers.add("IP2");
   }

   public static String getHost(){
       String host = servers.get(count% servers.size());    // Round Robin fashion
       count++;
       return host;
   }



}
