package com.kidbank.model;

import org.json.JSONObject;

/**
 * Represents a bank account (Current or Savings) belonging to a Child.
 * Holds the account balance only; transactions are stored separately.
 */


//Account modelning ning asosiy klassi bo'lib, u bolalarning joriy va tejash hisoblari bor.


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


    //logika oddiy bu joyda hisobni yaratishda balans manfiy(yani 0 dan kichik) bo'lishi mumkin emasligini tekshiradi.


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

    //albatta bu joyda ham tepadagi bilan bir xil logika.Farqi bu yerda xisobni kredit qilishda.


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


    //bu yerda esa hisobni debit qilishda tekshiradi

    public void debit(double amount) {
        if (amount <= 0)        throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > balance)   throw new IllegalArgumentException("Insufficient funds.");
        this.balance -= amount;
    }

    // ── Getters ──────────────────────────────────────────────────────────────


    //Getter lar oddiygina hisobning id sini, turini va balansini qaytaradi.simple hammasi

    public String getAccountId() { return accountId; }
    public String getType()      { return type; }
    public double getBalance()   { return balance; }

    // ── Serialisation ────────────────────────────────────────────────────────

    /**
     * Serialises this account to JSON.
     *
     * @return JSON representation
     */


    //bu yerda hisobni json formatiga o'tkazish, yani serializer.

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


    //bu yerda esa json formatidan hisobni yaratish, yani deserializer.


    public static Account fromJson(JSONObject obj) {
        return new Account(
            obj.getString("accountId"),
            obj.getString("type"),
            obj.getDouble("balance")
        );
    }


    //bu yerda esa hisobning string ko'rinishini qaytaradi, masalan "Current Account [£10.50]"
    // yani sooda qilib etganda override qilib toString methodini yozib qo'yganmiz.

    @Override
    public String toString() {
        return String.format("%s Account [£%.2f]", type, balance);
    }
}
