package com.kidbank.model;

import org.json.JSONObject;

/**
 * Represents a child user who earns, saves and spends virtual money.
 * Each Child has a Current Account and a Savings Account.
 */
public class Child extends User {

    /** 4-digit PIN stored as a hash. */
    private String pinHash;

    /** Username of the parent who manages this child. */
    private String parentUsername;

    /** The child's current (spending) account. */
    private Account currentAccount;

    /** The child's savings account. */
    private Account savingsAccount;

    /**
     * Constructs a Child with both account types initialised to zero.
     *
     * @param username       unique login id
     * @param fullName       display name
     * @param pinHash        SHA-256 hex of the 4-digit PIN
     * @param parentUsername owning parent's username
     */
    public Child(String username, String fullName, String pinHash, String parentUsername) {
        super(username, fullName, "CHILD");
        this.pinHash        = pinHash;
        this.parentUsername = parentUsername;
        this.currentAccount = new Account(username + "_current", "Current", 0.0);
        this.savingsAccount = new Account(username + "_savings", "Savings", 0.0);
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getPinHash()         { return pinHash; }
    public String getParentUsername()  { return parentUsername; }
    public Account getCurrentAccount() { return currentAccount; }
    public Account getSavingsAccount() { return savingsAccount; }
    public void setPinHash(String h)   { this.pinHash = h; }

    // ── Serialisation ────────────────────────────────────────────────────────

    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("username",       username);
        obj.put("fullName",       fullName);
        obj.put("role",           role);
        obj.put("pinHash",        pinHash);
        obj.put("parentUsername", parentUsername);
        obj.put("currentAccount", currentAccount.toJson());
        obj.put("savingsAccount", savingsAccount.toJson());
        return obj;
    }

    /**
     * Deserialises a Child from a JSONObject.
     *
     * @param obj JSON source
     * @return reconstructed Child
     */
    public static Child fromJson(JSONObject obj) {
        Child c = new Child(
            obj.getString("username"),
            obj.getString("fullName"),
            obj.getString("pinHash"),
            obj.getString("parentUsername")
        );
        if (obj.has("currentAccount")) {
            c.currentAccount = Account.fromJson(obj.getJSONObject("currentAccount"));
        }
        if (obj.has("savingsAccount")) {
            c.savingsAccount = Account.fromJson(obj.getJSONObject("savingsAccount"));
        }
        return c;
    }
}
