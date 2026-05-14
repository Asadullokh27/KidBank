package com.kidbank.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing passwords and PINs using SHA-256.
 * Passwords are never stored in plain text.
 */
public final class SecurityUtil {

    private SecurityUtil() { /* utility class */ }

    /**
     * Hashes a plain-text string using SHA-256.
     *
     * @param input the plain text to hash (password or PIN)
     * @return hex-encoded SHA-256 digest
     * @throws RuntimeException if SHA-256 is unavailable (should never happen on Java 17)
     */
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Verifies that a plain-text input matches a stored hash.
     *
     * @param input      plain-text candidate
     * @param storedHash the hash to compare against
     * @return true if they match
     */
    public static boolean verify(String input, String storedHash) {
        return hash(input).equals(storedHash);
    }

    /**
     * Validates that a PIN is exactly 4 numeric digits.
     *
     * @param pin the PIN string to validate
     * @return true if valid
     */
    public static boolean isValidPin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    /**
     * Validates that a password meets minimum requirements (>= 6 chars).
     *
     * @param password the password to validate
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
