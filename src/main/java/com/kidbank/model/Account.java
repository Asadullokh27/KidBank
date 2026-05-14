package com.kidbank.model;

import org.json.JSONObject;

/**
 * Represents a bank account (Current or Savings) belonging to a Child.
 * Holds the account balance only; transactions are stored separately.
 */
public class Account {

    /** Unique account identifier (e.g. "tommy_current"). */
    private String accountId;

    /** Human-readable type label: "Current" or "Savings". */
    private String type;

    /** Current balance in pounds. Always >= 0. */
    private double balance;

    /**
     * Constructs an Account.
     *
     * @param accountId unique identifier
     * @param type      "Current" or "Savings"
     * @param balance   starting balance (must be >= 0)
     * @throws IllegalArgumentException if balance is negative
     */
    public Account(String accountId, String type, double balance) {
        if (balance < 0) throw new IllegalArgumentException("Balance cannot be negative.");
        this.accountId = accountId;
        this.type      = type;
        this.balance   = balance;
    }

    // ── Balance operations ───────────────────────────────────────────────────

    /**
     * Credits (adds) an amount to this account.
     *
     * @param amount amount to add (must be > 0)
     * @throws IllegalArgumentException if amount <= 0
     */
    public void credit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
    }

    /**
     * Debits (subtracts) an amount from this account.
     *
     * @param amount amount to subtract (must be > 0 and <= balance)
     * @throws IllegalArgumentException if amount <= 0 or would overdraft
     */
    public void debit(double amount) {
        if (amount <= 0)        throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > balance)   throw new IllegalArgumentException("Insufficient funds.");
        this.balance -= amount;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getAccountId() { return accountId; }
    public String getType()      { return type; }
    public double getBalance()   { return balance; }

    // ── Serialisation ────────────────────────────────────────────────────────

    /**
     * Serialises this account to JSON.
     *
     * @return JSON representation
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("accountId", accountId);
        obj.put("type",      type);
        obj.put("balance",   balance);
        return obj;
    }

    /**
     * Deserialises an Account from a JSONObject.
     *
     * @param obj JSON source
     * @return reconstructed Account
     */
    public static Account fromJson(JSONObject obj) {
        return new Account(
            obj.getString("accountId"),
            obj.getString("type"),
            obj.getDouble("balance")
        );
    }

    @Override
    public String toString() {
        return String.format("%s Account [£%.2f]", type, balance);
    }
}
