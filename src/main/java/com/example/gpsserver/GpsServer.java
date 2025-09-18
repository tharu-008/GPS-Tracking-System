package com.example.gpsserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GpsServer {

    private final int port;
    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public GpsServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        System.out.println("[SERVER] Starting GPS server on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(0); // blocking
                pool.submit(new ClientHandler(socket));
            }
        }
    }

    public void shutdown() {
        pool.shutdown();
        try {
            pool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) throws Exception {
        int port = 5000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        // CSV file path can be configured with env var if needed
        String output = System.getProperty("gps.csv", "gps_messages.csv");
        CsvStorage.setOutputFile(output);

        GpsServer server = new GpsServer(port);
        server.start();
    }
}
