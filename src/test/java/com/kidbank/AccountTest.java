package com.kidbank;

import com.kidbank.model.Account;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD unit tests for the {@link Account} class.
 * Tests cover credit, debit, validation and JSON serialisation.
 */
@DisplayName("Account Tests")
class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("test_current", "Current", 10.0);
    }

    // ── Construction ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Account initialises with correct balance")
    void testInitialBalance() {
        assertEquals(10.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Negative initial balance throws exception")
    void testNegativeInitialBalanceThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new Account("x", "Current", -1.0));
    }

    // ── Credit ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Credit adds to balance correctly")
    void testCredit() {
        account.credit(5.0);
        assertEquals(15.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Credit with zero throws exception")
    void testCreditZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> account.credit(0));
    }

    @Test
    @DisplayName("Credit with negative throws exception")
    void testCreditNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> account.credit(-5.0));
    }

    // ── Debit ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debit subtracts from balance correctly")
    void testDebit() {
        account.debit(4.0);
        assertEquals(6.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Debit full balance leaves zero")
    void testDebitFullBalance() {
        account.debit(10.0);
        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Debit exceeding balance throws exception (no overdraft)")
    void testDebitOverdraftThrows() {
        assertThrows(IllegalArgumentException.class, () -> account.debit(11.0));
    }

    @Test
    @DisplayName("Debit with zero throws exception")
    void testDebitZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> account.debit(0));
    }

    @Test
    @DisplayName("Debit with negative throws exception")
    void testDebitNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> account.debit(-3.0));
    }

    // ── JSON round-trip ───────────────────────────────────────────────────────

    @Test
    @DisplayName("toJson / fromJson round-trip preserves all fields")
    void testJsonRoundTrip() {
        Account original = new Account("abc_savings", "Savings", 42.5);
        Account restored = Account.fromJson(original.toJson());
        assertEquals(original.getAccountId(), restored.getAccountId());
        assertEquals(original.getType(),      restored.getType());
        assertEquals(original.getBalance(),   restored.getBalance(), 0.001);
    }
}
