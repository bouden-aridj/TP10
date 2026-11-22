package com.example.tp10.util;

import java.security.MessageDigest;

public class PasswordUtils {

    /**
     * Hashes plain-text password using SHA-256 algorithm.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password; // Fallback
        }
    }
}
