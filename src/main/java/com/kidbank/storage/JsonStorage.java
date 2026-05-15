package com.kidbank.storage;

import com.kidbank.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Handles all persistence to and from local JSON files.
 *
 * <p>Data directory layout:
 * <pre>
 *   data/
 *     users.json          – all Parent and Child accounts
 *     transactions.json   – all transaction records
 *     tasks.json          – all task records
 *     goals.json          – all savings goals
 * </pre>
 */
// JsonStorage sinfi — mahalliy JSON fayllarga o'qish/yozish ishlari.
// Bu oddiy fayl-persistence: users, transactions, tasks va goals ni saqlaydi va yuklaydi.
public class JsonStorage {

    private static final String DATA_DIR      = "data";
    private static final String USERS_FILE    = DATA_DIR + "/users.json";
    private static final String TX_FILE       = DATA_DIR + "/transactions.json";
    private static final String TASKS_FILE    = DATA_DIR + "/tasks.json";
    private static final String GOALS_FILE    = DATA_DIR + "/goals.json";

    /** Initialises the data directory on first run. */
    // initialise: dastur birinchi marta ishga tushganda data papkasini va bo'sh JSON fayllarni yaratadi.
    public static void initialise() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            for (String f : new String[]{USERS_FILE, TX_FILE, TASKS_FILE, GOALS_FILE}) {
                Path p = Paths.get(f);
                if (!Files.exists(p)) {
                    // bo'sh massiv sifatida boshlang'ich kontent yoziladi
                    Files.writeString(p, "[]", StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialise data directory: " + e.getMessage(), e);
        }
    }

    // ── Generic helpers ──────────────────────────────────────────────────────

    /**
     * Reads a JSON array from disk and returns it.
     *
     * @param filePath relative filename
     * @return JSONArray (never null)
     */
    // readArray: fayldan o'qiydi; agar fayl bo'sh yoki o'qishda xato bo'lsa bo'sh JSONArray qaytaradi.
    private static JSONArray readArray(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            // Windows/old editors bo'sh joy qoldirsa ham "[]" ga o'giradi
            return new JSONArray(content.isBlank() ? "[]" : content);
        } catch (IOException e) {
            // agar faylni o'qib bo'lmasa, bo'sh array bilan ishlashni davom ettiramiz
            return new JSONArray();
        }
    }

    /**
     * Writes a formatted JSON array to disk.
     *
     * @param filePath relative filename
     * @param arr      JSONArray to write
     */
    // writeArray: JSON ni faylga 2-space indentation bilan yozadi; xatolik bo'lsa RuntimeException otadi.
    private static void writeArray(String filePath, JSONArray arr) {
        try {
            Files.writeString(Paths.get(filePath), arr.toString(2), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + filePath + ": " + e.getMessage(), e);
        }
    }

    // ── Users ────────────────────────────────────────────────────────────────

    /**
     * Loads all users from disk.
     *
     * @return map of username → User (Parent or Child)
     */
    // loadUsers: users.json dan barcha yozuvlarni o'qib, role ga qarab Parent yoki Child ga deserializatsiya qiladi.
    public static Map<String, User> loadUsers() {
        Map<String, User> map = new LinkedHashMap<>();
        JSONArray arr = readArray(USERS_FILE);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String role = obj.getString("role");
            // role PARENT bo'lsa Parent.fromJson, aks holda Child.fromJson chaqiriladi
            User u = role.equals("PARENT") ? Parent.fromJson(obj) : Child.fromJson(obj);
            map.put(u.getUsername(), u);
        }
        return map;
    }

    /**
     * Saves the complete user map to disk (overwrites).
     *
     * @param users map of username → User
     */
    // saveUsers: barcha users ni JSON massivga joylab users.json ga yozadi (to'liq overwrite).
    public static void saveUsers(Map<String, User> users) {
        JSONArray arr = new JSONArray();
        users.values().forEach(u -> arr.put(u.toJson()));
        writeArray(USERS_FILE, arr);
    }

    // ── Transactions ────────────────────��────────────────────────────────────

    /**
     * Loads all transactions for a specific account.
     *
     * @param accountId the account whose transactions to load
     * @return list ordered oldest → newest
     */
    // loadTransactions: transactions.json dan faqat berilgan accountId ga tegishli yozuvlarni filtrlab oladi
    // va vaqt bo'yicha o'sish tartibida qaytaradi (oldest → newest).
    public static List<Transaction> loadTransactions(String accountId) {
        List<Transaction> list = new ArrayList<>();
        JSONArray arr = readArray(TX_FILE);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("accountId").equals(accountId)) {
                list.add(Transaction.fromJson(obj));
            }
        }
        // vaqt bo'yicha saralab qo'yamiz
        list.sort(Comparator.comparing(Transaction::getTimestamp));
        return list;
    }

    /**
     * Appends a new transaction to the store.
     *
     * @param tx the transaction to persist
     */
    // saveTransaction: yangi transaction ni o'qib olinayotgan JSON massiviga qo'shadi va qayta yozadi (append).
    public static void saveTransaction(Transaction tx) {
        JSONArray arr = readArray(TX_FILE);
        arr.put(tx.toJson());
        writeArray(TX_FILE, arr);
    }

    // ── Tasks ────────────────────────────────────────────────────────────────

    /**
     * Loads all tasks from disk.
     *
     * @return list of all tasks
     */
    // loadTasks: tasks.json dan barcha vazifalarni o'qiydi va Task obyektlariga aylantiradi.
    public static List<Task> loadTasks() {
        List<Task> list = new ArrayList<>();
        JSONArray arr = readArray(TASKS_FILE);
        for (int i = 0; i < arr.length(); i++) {
            list.add(Task.fromJson(arr.getJSONObject(i)));
        }
        return list;
    }

    /**
     * Saves the complete task list (overwrites).
     *
     * @param tasks list of tasks to persist
     */
    // saveTasks: tasks ro'yxatini JSON ga o'girib tasks.json ga yozadi (overwrite).
    public static void saveTasks(List<Task> tasks) {
        JSONArray arr = new JSONArray();
        tasks.forEach(t -> arr.put(t.toJson()));
        writeArray(TASKS_FILE, arr);
    }

    // ── Savings Goals ────────────────────────────────────────────────────────

    /**
     * Loads all savings goals from disk.
     *
     * @return list of all goals
     */
    // loadGoals: goals.json dan barcha SavingsGoal obyektlarini o'qiydi.
    public static List<SavingsGoal> loadGoals() {
        List<SavingsGoal> list = new ArrayList<>();
        JSONArray arr = readArray(GOALS_FILE);
        for (int i = 0; i < arr.length(); i++) {
            list.add(SavingsGoal.fromJson(arr.getJSONObject(i)));
        }
        return list;
    }

    /**
     * Saves the complete goals list (overwrites).
     *
     * @param goals list of goals to persist
     */
    // saveGoals: goals ro'yxatini JSON ga o'girib goals.json ga yozadi.
    public static void saveGoals(List<SavingsGoal> goals) {
        JSONArray arr = new JSONArray();
        goals.forEach(g -> arr.put(g.toJson()));
        writeArray(GOALS_FILE, arr);
    }
}