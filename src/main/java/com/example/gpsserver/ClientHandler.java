package com.example.gpsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Instant;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private static final Pattern IMEI_PATTERN = Pattern.compile("^\\d{15}$");

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String remote = socket.getRemoteSocketAddress().toString();
        System.out.println("[SERVER] Connected: " + remote);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                // protocol: IMEI,LAT,LON[,TIMESTAMP]
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    System.err.println("[SERVER] Invalid message from " + remote + ": " + line);
                    continue;
                }
                String imei = parts[0].trim();
                String latStr = parts[1].trim();
                String lonStr = parts[2].trim();
                String ts = (parts.length >= 4) ? parts[3].trim() : Instant.now().toString();

                if (!IMEI_PATTERN.matcher(imei).matches()) {
                    System.err.println("[SERVER] Invalid IMEI from " + remote + ": " + imei);
                    continue;
                }
                try {
                    double lat = Double.parseDouble(latStr);
                    double lon = Double.parseDouble(lonStr);
                    if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                        throw new IllegalArgumentException("Out of bounds lat/lon");
                    }
                    CsvStorage.append(imei, lat, lon, ts, remote);
                } catch (Exception ex) {
                    System.err.println("[SERVER] Parse error from " + remote + ": " + ex.getMessage() + " line=" + line);
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] IO error with " + remote + ": " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[SERVER] Disconnected: " + remote);
        }
    }
}
