package org.example.Uitils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class BackendServers {

    private static final Logger LOGGER = Logger.getLogger(BackendServers.class.getName());
    private static final List<String> SERVERS = Arrays.asList("IP1", "IP2");
    private static final Map<String, AtomicInteger> ACTIVE_CONNECTIONS = new ConcurrentHashMap<String, AtomicInteger>();
    private static final AtomicInteger ROUND_ROBIN_COUNTER = new AtomicInteger(0);
    private static final String ALGORITHM = System.getProperty("lb.algorithm", "round_robin").trim().toLowerCase();

    static {
        for (String server : SERVERS) {
            ACTIVE_CONNECTIONS.put(server, new AtomicInteger(0));
        }
        LOGGER.info("Load balancing algorithm selected: " + ALGORITHM);
    }

    public static String getHost() {
        if ("least_connections".equals(ALGORITHM)) {
            return getLeastConnectionsHost();
        }
        return getRoundRobinHost();
    }

    public static void registerConnection(String host) {
        AtomicInteger count = ACTIVE_CONNECTIONS.get(host);
        if (count != null) {
            count.incrementAndGet();
        }
    }

    public static void releaseConnection(String host) {
        AtomicInteger count = ACTIVE_CONNECTIONS.get(host);
        if (count != null) {
            int updatedCount = count.decrementAndGet();
            if (updatedCount < 0) {
                count.set(0);
            }
        }
    }

    public static String getAlgorithm() {
        return ALGORITHM;
    }

    private static String getRoundRobinHost() {
        int index = Math.abs(ROUND_ROBIN_COUNTER.getAndIncrement() % SERVERS.size());
        return SERVERS.get(index);
    }

    private static String getLeastConnectionsHost() {
        String selectedHost = SERVERS.get(0);
        int minConnections = ACTIVE_CONNECTIONS.get(selectedHost).get();

        for (int i = 1; i < SERVERS.size(); i++) {
            String currentHost = SERVERS.get(i);
            int currentConnections = ACTIVE_CONNECTIONS.get(currentHost).get();
            if (currentConnections < minConnections) {
                minConnections = currentConnections;
                selectedHost = currentHost;
            }
        }
        return selectedHost;
    }
}
