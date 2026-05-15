package com.kidbank.model;

import org.json.JSONObject;

/**
 * Abstract base class representing a KidBank user.
 * Both Parent and Child extend this class.
 */
// Bu umumiy User sinfi — Parent va Child undan meros oladi.
// Har bir userda username, display name va role mavjud.
public abstract class User {

    /** Unique username used for login. */
    protected String username;

    /** Display name shown in the UI. */
    protected String fullName;

    /** User role: "PARENT" or "CHILD". */
    protected String role;

    /**
     * Constructs a User with the given credentials.
     *
     * @param username unique login identifier
     * @param fullName display name
     * @param role     "PARENT" or "CHILD"
     */
    // Konstruktor: foydalanuvchi yaratilganda username, fullName va role o'rnatiladi.
    public User(String username, String fullName, String role) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole()     { return role; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Serialises this user to a JSONObject for persistence.
     *
     * @return JSON representation
     */
    // Har bir subklass (Parent/Child) o'z JSON serializatsiyasini beradi.
    public abstract JSONObject toJson();

    @Override
    public String toString() {
        return role + "[" + username + "]";
    }
}