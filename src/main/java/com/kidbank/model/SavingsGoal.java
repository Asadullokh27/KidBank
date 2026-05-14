package com.kidbank.model;

import org.json.JSONObject;

/**
 * Represents a savings goal set by a child.
 * Tracks target amount and current progress.
 */
public class SavingsGoal {

    private final String goalId;
    private       String name;
    private final double targetAmount;
    private       double savedAmount;
    private final String childUsername;
    private       boolean completed;

    /**
     * Creates a new SavingsGoal with zero saved.
     *
     * @param goalId        unique identifier (UUID)
     * @param name          goal name (e.g. "New bicycle")
     * @param targetAmount  amount to reach (> 0)
     * @param childUsername owning child
     */
    public SavingsGoal(String goalId, String name, double targetAmount, String childUsername) {
        if (targetAmount <= 0)
            throw new IllegalArgumentException("Target amount must be positive.");
        this.goalId        = goalId;
        this.name          = name;
        this.targetAmount  = targetAmount;
        this.savedAmount   = 0.0;
        this.childUsername = childUsername;
        this.completed     = false;
    }

    /** Private constructor for deserialisation. */
    private SavingsGoal(String goalId, String name, double targetAmount,
                        double savedAmount, String childUsername, boolean completed) {
        this.goalId        = goalId;
        this.name          = name;
        this.targetAmount  = targetAmount;
        this.savedAmount   = savedAmount;
        this.childUsername = childUsername;
        this.completed     = completed;
    }

    // ── Operations ───────────────────────────────────────────────────────────

    /**
     * Adds an amount to this goal's saved total.
     *
     * @param amount amount to contribute (> 0)
     * @throws IllegalArgumentException if amount is invalid
     */
    public void contribute(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Contribution must be positive.");
        savedAmount += amount;
        if (savedAmount >= targetAmount) {
            savedAmount = targetAmount; // cap at target
            completed = true;
        }
    }

    /**
     * Returns the completion percentage (0–100).
     *
     * @return percentage as double
     */
    public double getProgressPercent() {
        return Math.min(100.0, (savedAmount / targetAmount) * 100.0);
    }

    /** Returns the remaining amount needed to reach the goal. */
    public double getRemaining() {
        return Math.max(0.0, targetAmount - savedAmount);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getGoalId()        { return goalId; }
    public String  getName()           { return name; }
    public double  getTargetAmount()   { return targetAmount; }
    public double  getSavedAmount()    { return savedAmount; }
    public String  getChildUsername()  { return childUsername; }
    public boolean isCompleted()       { return completed; }

    public void setName(String n) { this.name = n; }

    // ── Serialisation ────────────────────────────────────────────────────────

    /**
     * Serialises this goal to JSON.
     *
     * @return JSON representation
     */
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("goalId",        goalId);
        obj.put("name",          name);
        obj.put("targetAmount",  targetAmount);
        obj.put("savedAmount",   savedAmount);
        obj.put("childUsername", childUsername);
        obj.put("completed",     completed);
        return obj;
    }

    /**
     * Deserialises a SavingsGoal from a JSONObject.
     *
     * @param obj JSON source
     * @return reconstructed SavingsGoal
     */
    public static SavingsGoal fromJson(JSONObject obj) {
        return new SavingsGoal(
            obj.getString("goalId"),
            obj.getString("name"),
            obj.getDouble("targetAmount"),
            obj.getDouble("savedAmount"),
            obj.getString("childUsername"),
            obj.getBoolean("completed")
        );
    }

    @Override
    public String toString() {
        return String.format("Goal[%s] '%s' %.0f%% (£%.2f/£%.2f)",
            goalId, name, getProgressPercent(), savedAmount, targetAmount);
    }
}
