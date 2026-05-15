package com.kidbank.service;

import com.kidbank.model.*;
import com.kidbank.storage.JsonStorage;
import com.kidbank.util.SecurityUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core business-logic service for KidBank.
 *
 * <p>All operations go through this service; the UI layer should never
 * access {@link JsonStorage} directly. The service keeps an in-memory
 * cache of users, tasks and goals, persisting changes after each write.
 */
public class BankService {

    // ── In-memory state ──────────────────────────────────────────────────────


    //bu yerda foydalanuvchilar, vazifalar va tejash maqsadlari uchun xotira xaritasi va ro'yxatlari mavjud.
    // BankService yuklanganda barcha ma'lumotlarni JsonStorage dan yuklaydi va o'z xotirasida saqlaydi.

    //Nima uchun private? Chunki bu ma'lumotlarga faqat BankService orqali kirish kerak,
    // to'g'ridan-to'g'ri JsonStorage ga emas. Bu ma'lumot xavfsizligini ta'minlaydi.

    /** All users keyed by username. */
    private Map<String, User> users;

    /** All tasks. */
    private List<Task> tasks;

    /** All savings goals. */
    private List<SavingsGoal> goals;

    // ── Singleton ────────────────────────────────────────────────────────────

    private static BankService instance;


//bu joyda Singleton pattern ishlatilgan, ya'ni BankService bitta servis ishlatilishi uchun.
    //nima uchun? Chunki biz xohlaymizki, barcha UI komponentlari va operatsiyalar bir xil ma'lumotlar to'plami bilan ishlasin,
    // va har bir BankService yaratish ma'lumotlarni qayta yuklash va xotira kop joy egalanishi yani wasteni oshiradi.

    /** Returns the singleton service instance, loading data on first call. */
    public static BankService getInstance() {
        if (instance == null) {
            instance = new BankService();
        }
        return instance;
    }

    private BankService() {
        JsonStorage.initialise();
        users = JsonStorage.loadUsers();
        tasks = JsonStorage.loadTasks();
        goals = JsonStorage.loadGoals();
    }

    // ── Authentication ──

//bu yerda ota-ona va bola uchun alohida autentifikatsiya funksiyalari bor.


    /**
     * Authenticates a parent by username and password.
     *
     * @param username the parent's username
     * @param password plain-text password
     * @return the Parent if credentials match
     * @throws IllegalArgumentException if username not found or wrong password
     */
    public Parent authenticateParent(String username, String password) {
        User u = users.get(username);
        if (!(u instanceof Parent p))
            throw new IllegalArgumentException("No parent account found for: " + username);
        if (!SecurityUtil.verify(password, p.getPasswordHash()))
            throw new IllegalArgumentException("Incorrect password.");
        return p;
    }

    /**
     * Authenticates a child by username and PIN.
     *
     * @param username the child's username
     * @param pin      plain-text 4-digit PIN
     * @return the Child if credentials match
     * @throws IllegalArgumentException if username not found or wrong PIN
     */
    public Child authenticateChild(String username, String pin) {
        User u = users.get(username);
        if (!(u instanceof Child c))
            throw new IllegalArgumentException("No child account found for: " + username);
        if (!SecurityUtil.verify(pin, c.getPinHash()))
            throw new IllegalArgumentException("Incorrect PIN.");
        return c;
    }

    // ── Account creation ─────────────────────────────────────────────────────

    /**
     * Registers a new parent account.
     *
     * @param username  desired username (must be unique)
     * @param fullName  display name
     * @param password  plain-text password (>= 6 chars)
     * @return the created Parent
     * @throws IllegalArgumentException on validation failure
     */
    public Parent registerParent(String username, String fullName, String password) {
        validateNewUsername(username);
        if (!SecurityUtil.isValidPassword(password))
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        Parent p = new Parent(username, fullName, SecurityUtil.hash(password));
        users.put(username, p);
        JsonStorage.saveUsers(users);
        return p;
    }

    /**
     * Creates a new child account linked to a parent.
     *
     * @param parentUsername  the managing parent's username
     * @param childUsername   desired child username (must be unique)
     * @param fullName        display name
     * @param pin             4-digit PIN
     * @param initialDeposit  optional starting balance (>= 0)
     * @return the created Child
     * @throws IllegalArgumentException on validation failure
     */
    public Child createChild(String parentUsername, String childUsername,
                             String fullName, String pin, double initialDeposit) {
        validateNewUsername(childUsername);
        if (!SecurityUtil.isValidPin(pin))
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        if (initialDeposit < 0)
            throw new IllegalArgumentException("Initial deposit cannot be negative.");

        Child child = new Child(childUsername, fullName,
                                SecurityUtil.hash(pin), parentUsername);

        if (initialDeposit > 0) {
            child.getCurrentAccount().credit(initialDeposit);
            recordTransaction(child.getCurrentAccount(), Transaction.Type.DEPOSIT,
                              initialDeposit, "Initial deposit");
        }

        users.put(childUsername, child);

        // Link child to parent
        Parent parent = getParent(parentUsername);
        parent.addChild(childUsername);

        JsonStorage.saveUsers(users);
        return child;
    }

    // ── Deposit & Withdrawal ─────────────────────────────────────────────────


    //bu yerda bolalarning hisob raqamlariga pul qo'yish va pul yechish funksiyalari bor.


    /**
     * Deposits money into a child's account.
     *
     * @param childUsername the child's username
     * @param accountType   "Current" or "Savings"
     * @param amount        amount to deposit (> 0)
     * @param note          optional description
     * @throws IllegalArgumentException on invalid input
     */
    public void deposit(String childUsername, String accountType,
                        double amount, String note) {
        Child child = getChild(childUsername);
        Account account = selectAccount(child, accountType);
        account.credit(amount);
        recordTransaction(account, Transaction.Type.DEPOSIT, amount,
                          note.isBlank() ? "Deposit" : note);
        JsonStorage.saveUsers(users);
    }

    /**
     * Withdraws money from a child's account.
     *
     * @param childUsername the child's username
     * @param accountType   "Current" or "Savings"
     * @param amount        amount to withdraw (> 0, <= balance)
     * @param note          optional description
     * @throws IllegalArgumentException on invalid input or insufficient funds
     */
    public void withdraw(String childUsername, String accountType,
                         double amount, String note) {
        Child child = getChild(childUsername);
        Account account = selectAccount(child, accountType);
        account.debit(amount);  // throws if insufficient funds
        recordTransaction(account, Transaction.Type.WITHDRAWAL, amount,
                          note.isBlank() ? "Withdrawal" : note);
        JsonStorage.saveUsers(users);
    }

    // ── Tasks ────────────────────────────────────────────────────────────────


    //bu yerda vazifalar yaratish, bajarilgan vazifalarni tasdiqlash yoki rad etish funksiyalari bor.



    /**
     * Creates a new task and assigns it to a child.
     *
     * @param parentUsername parent creating the task
     * @param childUsername  child the task is assigned to
     * @param title          task title
     * @param description    optional details
     * @param rewardAmount   reward on completion (>= 0)
     * @return the created Task
     */
    public Task createTask(String parentUsername, String childUsername,
                           String title, String description, double rewardAmount) {
        Task task = new Task(UUID.randomUUID().toString(), title, description,
                             rewardAmount, parentUsername, childUsername);
        tasks.add(task);
        JsonStorage.saveTasks(tasks);
        return task;
    }

    /**
     * Child marks a task as completed (awaiting parent approval).
     *
     * @param taskId the task identifier
     */
    public void markTaskComplete(String taskId) {
        Task task = getTask(taskId);
        task.markCompletedByChild();
        JsonStorage.saveTasks(tasks);
    }

    /**
     * Parent approves a completed task and pays the reward.
     *
     * @param taskId the task identifier
     */
    public void approveTask(String taskId) {
        Task task = getTask(taskId);
        task.approve();
        // Pay reward to child's current account
        if (task.getRewardAmount() > 0) {
            Child child = getChild(task.getChildUsername());
            child.getCurrentAccount().credit(task.getRewardAmount());
            recordTransaction(child.getCurrentAccount(),
                              Transaction.Type.TASK_REWARD,
                              task.getRewardAmount(),
                              "Task reward: " + task.getTitle());
            JsonStorage.saveUsers(users);
        }
        JsonStorage.saveTasks(tasks);
    }

    /**
     * Parent rejects a completed task (returns to PENDING).
     *
     * @param taskId the task identifier
     */
    public void rejectTask(String taskId) {
        Task task = getTask(taskId);
        task.reject();
        JsonStorage.saveTasks(tasks);
    }

    // ── Savings Goals ────────────────────────────────────────────────────────



    //bu yerda bolalar uchun tejash maqsadlari yaratish va ularga pul qo'shish funksiyalari bor.



    /**
     * Creates a new savings goal for a child.
     *
     * @param childUsername    the child's username
     * @param name             goal name
     * @param targetAmount     target amount (> 0)
     * @param initialContrib   optional initial contribution (>= 0, deducted from current account)
     * @return the created SavingsGoal
     * @throws IllegalArgumentException if funds are insufficient for initial contribution
     */
    public SavingsGoal createGoal(String childUsername, String name,
                                  double targetAmount, double initialContrib) {
        Child child = getChild(childUsername);
        SavingsGoal goal = new SavingsGoal(UUID.randomUUID().toString(),
                                           name, targetAmount, childUsername);
        if (initialContrib > 0) {
            child.getCurrentAccount().debit(initialContrib); // throws if insufficient
            child.getSavingsAccount().credit(initialContrib);
            goal.contribute(initialContrib);
            recordTransaction(child.getCurrentAccount(), Transaction.Type.GOAL_TRANSFER,
                              initialContrib, "Goal contribution: " + name);
            JsonStorage.saveUsers(users);
        }
        goals.add(goal);
        JsonStorage.saveGoals(goals);
        return goal;
    }

    /**
     * Contributes money from the child's current account to a savings goal.
     *
     * @param goalId the goal identifier
     * @param amount amount to contribute (> 0)
     */
    public void contributeToGoal(String goalId, double amount) {
        SavingsGoal goal = getGoal(goalId);
        Child child = getChild(goal.getChildUsername());
        child.getCurrentAccount().debit(amount);
        child.getSavingsAccount().credit(amount);
        goal.contribute(amount);
        recordTransaction(child.getCurrentAccount(), Transaction.Type.GOAL_TRANSFER,
                          amount, "Goal contribution: " + goal.getName());
        JsonStorage.saveUsers(users);
        JsonStorage.saveGoals(goals);
    }

    // ── Queries ──────────────────────────────────────────────────────────────



    //bu yerda hisob raqamlaridagi tranzaksiyalarni ko'rish, bolalarga tayinlangan vazifalarni ko'rish,
    // tejash maqsadlarini ko'rish va ota-onalarga bog'langan bolalarni ko'rish funksiyalari bor.



    /**
     * Returns all transactions for a given account, newest first.
     *
     * @param accountId the account id
     * @return list of transactions
     */
    public List<Transaction> getTransactions(String accountId) {
        List<Transaction> list = JsonStorage.loadTransactions(accountId);
        list.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return list;
    }

    /**
     * Returns all tasks assigned to a specific child.
     *
     * @param childUsername the child's username
     * @return filtered list
     */
    public List<Task> getTasksForChild(String childUsername) {
        return tasks.stream()
                    .filter(t -> t.getChildUsername().equals(childUsername))
                    .collect(Collectors.toList());
    }

    /**
     * Returns all tasks created by a specific parent.
     *
     * @param parentUsername the parent's username
     * @return filtered list
     */
    public List<Task> getTasksForParent(String parentUsername) {
        return tasks.stream()
                    .filter(t -> t.getParentUsername().equals(parentUsername))
                    .collect(Collectors.toList());
    }

    /**
     * Returns all savings goals for a specific child.
     *
     * @param childUsername the child's username
     * @return filtered list
     */
    public List<SavingsGoal> getGoalsForChild(String childUsername) {
        return goals.stream()
                    .filter(g -> g.getChildUsername().equals(childUsername))
                    .collect(Collectors.toList());
    }

    /**
     * Returns all child accounts managed by a parent.
     *
     * @param parentUsername the parent's username
     * @return list of Child objects
     */
    public List<Child> getChildren(String parentUsername) {
        Parent parent = getParent(parentUsername);
        return parent.getChildUsernames().stream()
                     .map(this::getChild)
                     .collect(Collectors.toList());
    }

    /**
     * Checks whether a username already exists.
     *
     * @param username to check
     * @return true if taken
     */
    public boolean usernameExists(String username) {
        return users.containsKey(username);
    }



    // ── Internal helpers ─────────────────────────────────────────────────────


    //bu yerda yangi foydalanuvchi yaratishda username ni tekshirish,
    // ota-ona va bola obyektlarini olish, vazifa va maqsadlarni olish, hisob turini tanlash va tranzaksiya yozish funksiyalari bor.



    private void validateNewUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (username.length() < 3)
            throw new IllegalArgumentException("Username must be at least 3 characters.");
        if (users.containsKey(username))
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
    }

    private Parent getParent(String username) {
        User u = users.get(username);
        if (!(u instanceof Parent p))
            throw new IllegalArgumentException("Parent not found: " + username);
        return p;
    }

    public Child getChild(String username) {
        User u = users.get(username);
        if (!(u instanceof Child c))
            throw new IllegalArgumentException("Child not found: " + username);
        return c;
    }

    private Task getTask(String taskId) {
        return tasks.stream()
                    .filter(t -> t.getTaskId().equals(taskId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }

    private SavingsGoal getGoal(String goalId) {
        return goals.stream()
                    .filter(g -> g.getGoalId().equals(goalId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId));
    }

    private Account selectAccount(Child child, String accountType) {
        return switch (accountType) {
            case "Current" -> child.getCurrentAccount();
            case "Savings" -> child.getSavingsAccount();
            default -> throw new IllegalArgumentException("Unknown account type: " + accountType);
        };
    }

    private void recordTransaction(Account account, Transaction.Type type,
                                   double amount, String note) {
        Transaction tx = new Transaction(
            UUID.randomUUID().toString(),
            account.getAccountId(),
            type,
            amount,
            account.getBalance(),
            note
        );
        JsonStorage.saveTransaction(tx);
    }
}
