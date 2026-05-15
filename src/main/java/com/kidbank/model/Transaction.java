package com.kidbank.model;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction on an account.
 * Transactions are immutable once created.
 */
// Transaction sinfi — hisobda yuz bergan har bir harakat (deposit, withdrawal va boshqalar).
// Bu obyekt o'zgarmas (immutable) qilib yaratiladi: vaqt va balans yozib qo'yiladi.
public class Transaction {

    /** ISO-8601 date-time format used for JSON storage. */
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** Supported transaction types. */
    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TASK_REWARD,
        GOAL_TRANSFER,
        ALLOWANCE
    }

    private final String        transactionId;
    private final String        accountId;
    private final Type          type;
    private final double        amount;
    private final double        balanceAfter;
    private final String        note;
    private final LocalDateTime timestamp;

    /**
     * Creates a new Transaction with the current timestamp.
     *
     * @param transactionId unique id (UUID)
     * @param accountId     account this transaction belongs to
     * @param type          transaction type
     * @param amount        transaction amount (positive)
     * @param balanceAfter  account balance after this transaction
     * @param note          optional human-readable description
     */
    // Konstruktor: transaction yaratishda timestamp hozirgi vaqt sifatida olinadi.
    public Transaction(String transactionId, String accountId, Type type,
                       double amount, double balanceAfter, String note) {
        this.transactionId = transactionId;
        this.accountId     = accountId;
        this.type          = type;
        this.amount        = amount;
        this.balanceAfter  = balanceAfter;
        this.note          = note;
        this.timestamp     = LocalDateTime.now();
    }

    /** Private constructor used during deserialisation. */
    private Transaction(String transactionId, String accountId, Type type,
                        double amount, double balanceAfter, String note,
                        LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountId     = accountId;
        this.type          = type;
        this.amount        = amount;
        this.balanceAfter  = balanceAfter;
        this.note          = note;
        this.timestamp     = timestamp;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String        getTransactionId() { return transactionId; }
    public String        getAccountId()     { return accountId; }
    public Type          getType()          { return type; }
    public double        getAmount()        { return amount; }
    public double        getBalanceAfter()  { return balanceAfter; }
    public String        getNote()          { return note; }
    public LocalDateTime getTimestamp()     { return timestamp; }

    /** Returns true if this transaction adds money to the account. */
    // isCredit: bu transaction hisobga pul qo'shadimi yoki olib tashlaydimi tekshiradi.
    public boolean isCredit() {
        return type == Type.DEPOSIT || type == Type.TASK_REWARD || type == Type.ALLOWANCE;
    }

    /** Formatted timestamp string for display (e.g. "28 Apr 2026 14:32"). */
    // getFormattedDate: foydalanuvchi interfeysida ko'rsatish uchun formatlangan vaqt.
    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    /**
     * Serialises this transaction to JSON.
     *
     * @return JSON representation
     */
    // toJson: transactionni JSON ga konvertatsiya qiladi, timestamp ni formatlab saqlaydi.
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        obj.put("transactionId", transactionId);
        obj.put("accountId",     accountId);
        obj.put("type",          type.name());
        obj.put("amount",        amount);
        obj.put("balanceAfter",  balanceAfter);
        obj.put("note",          note);
        obj.put("timestamp",     timestamp.format(FMT));
        return obj;
    }

    /**
     * Deserialises a Transaction from a JSONObject.
     *
     * @param obj JSON source
     * @return reconstructed Transaction
     */
    // fromJson: JSON dan transactionni qayta tiklaydi.
    public static Transaction fromJson(JSONObject obj) {
        return new Transaction(
                obj.getString("transactionId"),
                obj.getString("accountId"),
                Type.valueOf(obj.getString("type")),
                obj.getDouble("amount"),
                obj.getDouble("balanceAfter"),
                obj.optString("note", ""),
                LocalDateTime.parse(obj.getString("timestamp"), FMT)
        );
    }

    @Override
    public String toString() {
        String sign = isCredit() ? "+" : "-";
        return String.format("[%s] %s %s£%.2f — %s", getFormattedDate(), type, sign, amount, note);
    }
}