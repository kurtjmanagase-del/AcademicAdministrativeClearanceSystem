package com.clearancesystem.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {

    public static String hashPassword(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltHex = bytesToHex(salt);
        String hashHex = sha256Hex(saltHex + password);
        return "sha256$" + saltHex + "$" + hashHex;
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("sha256$")) {
            return false;
        }
        String[] parts = storedHash.split("\\$");
        if (parts.length != 3) {
            return false;
        }
        String saltHex = parts[1];
        String storedHashHex = parts[2];
        String computedHashHex = sha256Hex(saltHex + password);
        return computedHashHex.equals(storedHashHex);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
