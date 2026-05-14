# 🏦 KidBank – Virtual Bank Application for Kids

A JavaFX desktop application that teaches children about money management through a
fun, gamified virtual banking experience.

---

## Requirements

| Tool       | Minimum Version | Download |
|------------|-----------------|---------|
| **Java JDK** | 17             | https://adoptium.net |
| **Maven**    | 3.8+           | https://maven.apache.org |
| **IntelliJ IDEA** | Any (Community is free) | https://www.jetbrains.com/idea/ |

---

## Project Structure

```
KidBank/
├── pom.xml                        ← Maven build file (dependencies)
├── data/                          ← JSON data files (auto-created on first run)
│   ├── users.json
│   ├── transactions.json
│   ├── tasks.json
│   └── goals.json
└── src/
    ├── main/java/com/kidbank/
    │   ├── model/                 ← Data classes (User, Child, Parent, Account, Task, SavingsGoal, Transaction)
    │   ├── service/               ← Business logic (BankService)
    │   ├── storage/               ← JSON file persistence (JsonStorage)
    │   ├── util/                  ← Utilities (SecurityUtil)
    │   └── ui/
    │       ├── KidBankApp.java    ← JavaFX entry point
    │       ├── components/        ← Shared UI helpers (StyleUtil)
    │       └── screens/           ← All application screens
    └── test/java/com/kidbank/     ← JUnit 5 test classes
```

---

## How to Run

### Option A – IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA
2. Click **File → Open** and select the `KidBank` folder
3. IntelliJ will detect it as a Maven project and import dependencies automatically
4. Wait for the Maven sync to finish (progress bar at bottom)
5. Open `src/main/java/com/kidbank/ui/KidBankApp.java`
6. Click the **green ▶ Run** button next to `public static void main`
7. The KidBank window will open

### Option B – Command Line

```bash
# Navigate to the KidBank folder
cd KidBank

# Download dependencies and compile
mvn clean compile

# Run the application
mvn javafx:run

# Run tests
mvn test

# Build a runnable JAR
mvn package
java -jar target/kidbank-1.0.0.jar
```

---

## First-Time Use (Walkthrough)

1. **Register as Parent** – Click "Register as Parent" on the welcome screen
2. **Create a Child Account** – From the parent dashboard, click "Add Child Account"
3. **Login as Child** – Log out, then click "Login as Child" using the child's username and PIN
4. **Explore features** – Check balance, view tasks, set savings goals, check history

---

## Features

| Feature | Who |
|---------|-----|
| Register / Login (Parent & Child) | Both |
| Create child accounts with PIN | Parent |
| Deposit & withdraw virtual money | Both |
| Create chore tasks with rewards | Parent |
| Mark tasks complete | Child |
| Approve / reject tasks (auto-pay reward) | Parent |
| View transaction history | Both |
| Create savings goals | Child |
| Contribute to savings goals | Child |
| Persistent JSON data storage | Automatic |

---

## Running Tests

```bash
mvn test
```

JUnit 5 tests are in `src/test/java/com/kidbank/` and cover:
- `AccountTest` – credit, debit, overdraft prevention, JSON
- `TaskTest` – state machine (pending → complete → approve/reject)
- `SavingsGoalTest` – contribution, progress, goal completion
- `SecurityUtilTest` – password/PIN hashing and validation

---

## Data Storage

All data is saved automatically in the `data/` folder as JSON files:
- `users.json` – all parent and child accounts
- `transactions.json` – all transaction records
- `tasks.json` – all task records
- `goals.json` – all savings goals

No database or internet connection is required.
