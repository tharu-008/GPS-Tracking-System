package com.example.gpsclient;

import java.util.Random;

public class GeoUtils {
    private static final Random RND = new Random();

    public static double[] randomStep(double lat, double lon, double maxMeters) {
        // Convert meters to degrees approximately
        double degLatPerMeter = 1.0 / 111_320.0;
        double degLonPerMeter = 1.0 / (111_320.0 * Math.cos(Math.toRadians(lat == 0 ? 1e-6 : lat)));
        double dLat = (RND.nextDouble() * 2 - 1) * maxMeters * degLatPerMeter;
        double dLon = (RND.nextDouble() * 2 - 1) * maxMeters * degLonPerMeter;
        return new double[]{lat + dLat, lon + dLon};
    }

    public static String generateRandomImei() {
        // 15-digit numeric string (Luhn not enforced for simplicity)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++) sb.append(RND.nextInt(10));
        return sb.toString();
    }
}
