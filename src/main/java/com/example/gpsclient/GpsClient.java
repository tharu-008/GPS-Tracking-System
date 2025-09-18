package com.example.gpsclient;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.Random;

public class GpsClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java -jar gps-client.jar <serverHost> <serverPort> <imei> [messages=20] [sleepMs=750]");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String imei = args[2];
        int messages = args.length >= 4 ? Integer.parseInt(args[3]) : 20;
        long sleepMs = args.length >= 5 ? Long.parseLong(args[4]) : 750;

        // Start near Hikkaduwa Turtle Beach (approx) to match Sri Lanka context
        double lat = 6.1392;
        double lon = 80.1020;
        Random rnd = new Random();

        try (Socket socket = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            System.out.printf("[CLIENT %s] Connected to %s:%d%n", imei, host, port);
            for (int i = 0; i < messages; i++) {
                // Move up to 25 meters per step
                double[] step = GeoUtils.randomStep(lat, lon, 25.0);
                lat = step[0];
                lon = step[1];
                String payload = String.format("%s,%.6f,%.6f,%s", imei, lat, lon, Instant.now().toString());
                out.write(payload);
                out.newLine();
                out.flush();
                System.out.println("[CLIENT " + imei + "] Sent: " + payload);
                Thread.sleep(sleepMs + rnd.nextInt(250));
            }
            System.out.printf("[CLIENT %s] Done sending %d messages%n", imei, messages);
        }
    }
}
