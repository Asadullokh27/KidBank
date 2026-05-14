package com.kidbank;

import com.kidbank.util.SecurityUtil;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD unit tests for {@link SecurityUtil}.
 */
@DisplayName("SecurityUtil Tests")
class SecurityUtilTest {

    @Test
    @DisplayName("Hashing same input always gives same result")
    void testHashConsistency() {
        assertEquals(SecurityUtil.hash("hello"), SecurityUtil.hash("hello"));
    }

    @Test
    @DisplayName("Different inputs produce different hashes")
    void testDifferentInputsDifferentHashes() {
        assertNotEquals(SecurityUtil.hash("abc"), SecurityUtil.hash("xyz"));
    }

    @Test
    @DisplayName("verify returns true for matching input")
    void testVerifyMatch() {
        String hash = SecurityUtil.hash("mypassword");
        assertTrue(SecurityUtil.verify("mypassword", hash));
    }

    @Test
    @DisplayName("verify returns false for wrong input")
    void testVerifyMismatch() {
        String hash = SecurityUtil.hash("mypassword");
        assertFalse(SecurityUtil.verify("wrongpassword", hash));
    }

    @Test
    @DisplayName("Valid 4-digit PIN passes validation")
    void testValidPin() {
        assertTrue(SecurityUtil.isValidPin("1234"));
        assertTrue(SecurityUtil.isValidPin("0000"));
        assertTrue(SecurityUtil.isValidPin("9999"));
    }

    @Test
    @DisplayName("Invalid PINs fail validation")
    void testInvalidPin() {
        assertFalse(SecurityUtil.isValidPin("123"));    // too short
        assertFalse(SecurityUtil.isValidPin("12345")); // too long
        assertFalse(SecurityUtil.isValidPin("abcd"));  // not digits
        assertFalse(SecurityUtil.isValidPin(null));    // null
        assertFalse(SecurityUtil.isValidPin(""));      // empty
    }

    @Test
    @DisplayName("Valid passwords pass (>= 6 chars)")
    void testValidPassword() {
        assertTrue(SecurityUtil.isValidPassword("abc123"));
        assertTrue(SecurityUtil.isValidPassword("longerPassword!"));
    }

    @Test
    @DisplayName("Short or null passwords fail")
    void testInvalidPassword() {
        assertFalse(SecurityUtil.isValidPassword("abc"));
        assertFalse(SecurityUtil.isValidPassword(""));
        assertFalse(SecurityUtil.isValidPassword(null));
    }
}
