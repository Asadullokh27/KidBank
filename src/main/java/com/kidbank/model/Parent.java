package com.kidbank.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parent user who manages child accounts and tasks.
 */
public class Parent extends User {

    /** Hashed password (SHA-256 hex). */
    private String passwordHash;

    /** Usernames of child accounts linked to this parent. */
    private List<String> childUsernames;

    /**
     * Constructs a Parent.
     *
     * @param username     unique login id
     * @param fullName     display name
     * @param passwordHash SHA-256 hex of the parent's password
     */
    public Parent(String username, String fullName, String passwordHash) {
        super(username, fullName, "PARENT");
        this.passwordHash = passwordHash;
        this.childUsernames = new ArrayList<>();
    }

    // ── Child management ─────────────────────────────────────────────────────

    /**
     * Links a child account to this parent.
     *
     * @param childUsername the child's username
     */
    public void addChild(String childUsername) {
        if (!childUsernames.contains(childUsername)) {
            childUsernames.add(childUsername);
        }
    }

    /**
     * Removes a child link.
     *
     * @param childUsername the child's username
     */
    public void removeChild(String childUsername) {
        childUsernames.remove(childUsername);
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getPasswordHash()       { return passwordHash; }
    public List<String> getChildUsernames() { return childUsernames; }
    public void setPasswordHash(String h) { this.passwordHash = h; }

    // ── Serialisation ────────────────────────────────────────────────────────

    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("username",     username);
        obj.put("fullName",     fullName);
        obj.put("role",         role);
        obj.put("passwordHash", passwordHash);
        obj.put("children",     new JSONArray(childUsernames));
        return obj;
    }

    /**
     * Deserialises a Parent from a JSONObject.
     *
     * @param obj JSON source
     * @return reconstructed Parent
     */
    public static Parent fromJson(JSONObject obj) {
        Parent p = new Parent(
            obj.getString("username"),
            obj.getString("fullName"),
            obj.getString("passwordHash")
        );
        JSONArray arr = obj.optJSONArray("children");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                p.addChild(arr.getString(i));
            }
        }
        return p;
    }
}
