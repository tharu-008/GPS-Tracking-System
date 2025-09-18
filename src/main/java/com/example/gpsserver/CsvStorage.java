package com.example.gpsserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class CsvStorage {
    private static Path outputFile = Paths.get("gps_messages.csv");
    private static volatile boolean headerWritten = false;

    public static void setOutputFile(String path) {
        outputFile = Paths.get(path);
    }

    public static synchronized void append(String imei, double lat, double lon, String timestamp, String remote) {
        try {
            if (!Files.exists(outputFile)) {
                Files.createDirectories(outputFile.getParent() != null ? outputFile.getParent() : Paths.get("."));
                headerWritten = false;
            }
            if (!headerWritten) {
                Files.write(outputFile,
                        ("imei,latitude,longitude,timestamp,remote\n").getBytes(StandardCharsets.UTF_8),
                        Files.exists(outputFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
                headerWritten = true;
            }
            String line = String.format("%s,%.6f,%.6f,%s,%s%n", imei, lat, lon,
                    (timestamp == null || timestamp.isEmpty() ? Instant.now().toString() : timestamp), remote);
            Files.write(outputFile, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            System.out.print("[SERVER] Saved: " + line);
        } catch (IOException e) {
            System.err.println("[SERVER] Failed to write CSV: " + e.getMessage());
        }
    }
}
