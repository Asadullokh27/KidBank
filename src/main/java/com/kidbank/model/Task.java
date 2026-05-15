package com.kidbank.model;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a chore/task that a parent assigns to a child.
 * A task progresses through: PENDING → COMPLETED_BY_CHILD → APPROVED | REJECTED.
 */
// Task sinfi — ota-ona tomonidan beriladigan vazifa. Vazifa holatlari enum bilan ifodalanadi.
// Bu yerda vazifa yaratilib, farzand belgilab bo'lgach uni bajaradi, so'ng ota tekshiradi (tasdiqlash/rad qilish).
public class Task {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** Lifecycle states of a task. */
    public enum Status {
        PENDING,           // assigned, not yet done
        COMPLETED_BY_CHILD, // child marked complete
        APPROVED,          // parent approved → reward paid
        REJECTED           // parent rejected → back to PENDING
    }

    private final String        taskId;
    private       String        title;
    private       String        description;
    private final double        rewardAmount;
    private final String        parentUsername;
    private final String        childUsername;
    private       Status        status;
    private final LocalDateTime createdAt;

    /**
     * Creates a new Task in PENDING state.
     *
     * @param taskId        unique identifier (UUID)
     * @param title         short task name
     * @param description   optional details
     * @param rewardAmount  amount paid on approval (>= 0)
     * @param parentUsername creator parent
     * @param childUsername  assigned child
     */
    // Konstruktor: yangi Task PENDING holatda yaratiladi; reward >= 0 bo'lishi kerak.
    public Task(String taskId, String title, String description,
                double rewardAmount, String parentUsername, String childUsername) {
        if (rewardAmount < 0) throw new IllegalArgumentException("Reward cannot be negative.");
        this.taskId         = taskId;
        this.title          = title;
        this.description    = description;
        this.rewardAmount   = rewardAmount;
        this.parentUsername = parentUsername;
        this.childUsername  = childUsername;
        this.status         = Status.PENDING;
        this.createdAt      = LocalDateTime.now();
    }

    /** Private constructor for deserialisation. */
    // Deserializatsiya uchun maxfiy konstruktor — vaqt va holat tashqi JSON dan olinadi.
    private Task(String taskId, String title, String description,
                 double rewardAmount, String parentUsername, String childUsername,
                 Status status, LocalDateTime createdAt) {
        this.taskId         = taskId;
        this.title          = title;
        this.description    = description;
        this.rewardAmount   = rewardAmount;
        this.parentUsername = parentUsername;
        this.childUsername  = childUsername;
        this.status         = status;
        this.createdAt      = createdAt;
    }

    // ── State transitions ────────────────────────────────────────────────────

    /** Child marks the task as done. */
    // markCompletedByChild: faqat PENDING bo'lsa child uni bajarildi deb belgilashi mumkin.
    public void markCompletedByChild() {
        if (status != Status.PENDING)
            throw new IllegalStateException("Only PENDING tasks can be marked complete.");
        status = Status.COMPLETED_BY_CHILD;
    }

    /** Parent approves the completed task. */
    // approve: faqat child tomonidan COMPLETED_BY_CHILD holatida ota tomonidan tasdiqlanadi.
    public void approve() {
        if (status != Status.COMPLETED_BY_CHILD)
            throw new IllegalStateException("Only COMPLETED_BY_CHILD tasks can be approved.");
        status = Status.APPROVED;
    }

    /** Parent rejects the completed task (returns to PENDING). */
    // reject: ota rad etsa vazifa yana PENDING ga qaytadi.
    public void reject() {
        if (status != Status.COMPLETED_BY_CHILD)
            throw new IllegalStateException("Only COMPLETED_BY_CHILD tasks can be rejected.");
        status = Status.PENDING;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String        getTaskId()         { return taskId; }
    public String        getTitle()           { return title; }
    public String        getDescription()     { return description; }
    public double        getRewardAmount()    { return rewardAmount; }
    public String        getParentUsername()  { return parentUsername; }
    public String        getChildUsername()   { return childUsername; }
    public Status        getStatus()          { return status; }
    public LocalDateTime getCreatedAt()       { return createdAt; }

    public void setTitle(String t)       { this.title = t; }
    public void setDescription(String d) { this.description = d; }

    // ── Serialisation ────────────────────────────────────────────────────────

    /**
     * Serialises this task to JSON.
     *
     * @return JSON representation
     */
    // toJson: task obyektini JSON ga o'girish — holat va vaqtni string sifatida saqlaydi.
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("taskId",         taskId);
        obj.put("title",          title);
        obj.put("description",    description);
        obj.put("rewardAmount",   rewardAmount);
        obj.put("parentUsername", parentUsername);
        obj.put("childUsername",  childUsername);
        obj.put("status",         status.name());
        obj.put("createdAt",      createdAt.format(FMT));
        return obj;
    }

    /**
     * Deserialises a Task from a JSONObject.
     *
     * @param obj JSON source
     * @return reconstructed Task
     */
    // fromJson: JSON dan Task ni qayta tiklaydi, status va yaratilgan vaqtni ham o'qiydi.
    public static Task fromJson(JSONObject obj) {
        return new Task(
                obj.getString("taskId"),
                obj.getString("title"),
                obj.optString("description", ""),
                obj.getDouble("rewardAmount"),
                obj.getString("parentUsername"),
                obj.getString("childUsername"),
                Status.valueOf(obj.getString("status")),
                LocalDateTime.parse(obj.getString("createdAt"), FMT)
        );
    }

    @Override
    public String toString() {
        return String.format("Task[%s] '%s' → %s (£%.2f)", taskId, title, status, rewardAmount);
    }
}