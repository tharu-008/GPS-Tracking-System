package com.example.gpsclient;

import java.util.ArrayList;
import java.util.List;

public class MultiClientLauncher {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java -cp target/gps-client-1.0.0.jar com.example.gpsclient.MultiClientLauncher <serverHost> <serverPort>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // Create 5 IMEIs
        List<String> imeis = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            imeis.add(GeoUtils.generateRandomImei());
        }

        List<Thread> threads = new ArrayList<>();
        for (String imei : imeis) {
            Thread t = new Thread(() -> {
                try {
                    GpsClient.main(new String[]{host, String.valueOf(port), imei, "30", "500"});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "client-" + imei);
            t.start();
            threads.add(t);
        }

        // Wait for all to finish
        for (Thread t : threads) t.join();
        System.out.println("[LAUNCHER] All clients finished.");
    }
}
