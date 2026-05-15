package com.kidbank.ui.screens;

import com.kidbank.model.Child;
import com.kidbank.model.Parent;
import com.kidbank.model.Task;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.screens.FinanceCarousel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;


//bu ParentDashboard, ota-onalar uchun mo'ljallangan boshqaruv paneli bo'lib,
//unda ular o'z farzandlarini, vazifalarini, tasdiqlashlarni va tezkor bank amallarini boshqarishlari mumkin.


public class ParentDashboard {

    public static Scene build(Parent parent) {
        // This is the main dashboard for parents.
        // It shows children, tasks, pending approvals, and quick banking actions.

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");
        root.setTop(buildHeader(parent));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox content = new VBox(22);
        content.setPadding(new Insets(24));

        Label pageTitle = new Label("Financial Overview");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label pageSub = new Label("Welcome back, " + parent.getFullName() + " — manage your family's smart banking");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        content.getChildren().addAll(pageTitle, pageSub);

        // KPI cards show the important numbers first.
        List<Child> children = BankService.getInstance().getChildren(parent.getUsername());
        List<Task> allTasks = BankService.getInstance().getTasksForParent(parent.getUsername());
        long pending = allTasks.stream().filter(t -> t.getStatus() == Task.Status.COMPLETED_BY_CHILD).count();

        GridPane kpiGrid = new GridPane();
        kpiGrid.setHgap(14);
        kpiGrid.setVgap(14);
        kpiGrid.add(fintechCard("Children", String.valueOf(children.size()), "#4F8CFF"), 0, 0);
        kpiGrid.add(fintechCard("Total Tasks", String.valueOf(allTasks.size()), "#7C5CFF"), 1, 0);
        kpiGrid.add(fintechCard("Awaiting Approval", String.valueOf(pending), pending > 0 ? "#F3B63F" : "#1BC47D"), 2, 0);
        kpiGrid.add(fintechCard("Monthly Spending", estimateMonthlySpending(children), "#FF5D7A"), 3, 0);

        content.getChildren().add(kpiGrid);

        // This carousel is like a hero banner for the dashboard.
        FinanceCarousel hero = new FinanceCarousel(List.of(
                new FinanceCarousel.CarouselSlide(
                        "Family banking",
                        "One dashboard for every child",
                        "Review balances, tasks, approvals, and savings at a glance.",
                        "Control with clarity",
                        "#4F8CFF",
                        "#7C5CFF"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Tasks & rewards",
                        "Teach money habits through chores",
                        "Assign tasks and reward progress with instant feedback.",
                        "Earn → Save → Grow",
                        "#10B981",
                        "#14B8A6"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Insights",
                        "Spot spending patterns fast",
                        "Stay on top of savings trends and approval activity.",
                        "Simple, visual analytics",
                        "#F59E0B",
                        "#EF4444"
                )
        ));
        hero.setMaxWidth(Double.MAX_VALUE);
        content.getChildren().add(hero);

        content.getChildren().add(sectionTitle("Your Children"));
        content.getChildren().add(buildChildrenRow(parent, children));

        content.getChildren().add(sectionTitle("Quick Actions"));
        content.getChildren().add(buildActionGrid(parent));

        content.getChildren().add(sectionTitle("Pending Approvals"));
        content.getChildren().add(buildPendingApprovals(parent));

        scroll.setContent(content);
        root.setCenter(scroll);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static HBox buildHeader(Parent parent) {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(14, 22, 14, 22));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: linear-gradient(to right, #0F172A, #1E293B);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label brand = new Label("PocketPal");
        brand.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label welcome = new Label("Logged in as " + parent.getFullName() + " (Parent)");
        welcome.setStyle("-fx-font-size: 12px; -fx-text-fill: #9AA7C7;");

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #EAF0FF;"
                + "-fx-background-radius: 10; -fx-padding: 8 14; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> KidBankApp.navigate(WelcomeScreen.build()));

        bar.getChildren().addAll(brand, sp, welcome, logoutBtn);
        return bar;
    }

    private static VBox fintechCard(String title, String value, String accent) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setMinWidth(190);
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 16; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 16;");

        Label t = new Label(title.toUpperCase());
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: #9AA7C7; -fx-font-weight: 700;");

        Label v = new Label(value);
        v.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: " + accent + ";");

        card.getChildren().addAll(t, v);
        return card;
    }

    private static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 17px; -fx-font-weight: 700; -fx-text-fill: #EAF0FF;");
        return l;
    }

    private static String estimateMonthlySpending(List<Child> children) {
        double sum = children.stream()
                .mapToDouble(c -> Math.max(0, 500 - c.getCurrentAccount().getBalance()))
                .sum();
        return "£" + String.format("%.2f", sum);
    }

    private static HBox buildChildrenRow(Parent parent, List<Child> children) {
        HBox row = new HBox(14);

        if (children.isEmpty()) {
            VBox empty = new VBox(8);
            empty.setPadding(new Insets(16));
            empty.setMinWidth(260);
            empty.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14;");
            Label msg = new Label("No children yet.\nClick Add Child to begin.");
            msg.setStyle("-fx-text-fill: #9AA7C7; -fx-font-size: 12px;");
            msg.setWrapText(true);
            empty.getChildren().add(msg);
            row.getChildren().add(empty);
            return row;
        }

        for (Child c : children) row.getChildren().add(buildChildCard(c, parent));
        return row;
    }

    private static VBox buildChildCard(Child child, Parent parent) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setMinWidth(210);
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;");

        Label nameLbl = new Label(child.getFullName());
        nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #EAF0FF;");

        Label spend = new Label("Spending: £" + String.format("%.2f", child.getCurrentAccount().getBalance()));
        spend.setStyle("-fx-text-fill: #4F8CFF; -fx-font-weight: 700;");

        Label save = new Label("Savings: £" + String.format("%.2f", child.getSavingsAccount().getBalance()));
        save.setStyle("-fx-text-fill: #1BC47D; -fx-font-weight: 700;");

        Button depositBtn = new Button("Deposit");
        depositBtn.setStyle("-fx-background-color: linear-gradient(to right, #4F8CFF, #7C5CFF);"
                + "-fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 10; -fx-cursor: hand;");
        depositBtn.setOnAction(e -> KidBankApp.navigate(DepositWithdrawScreen.build(child, parent, "Current")));

        card.getChildren().addAll(nameLbl, spend, save, depositBtn);
        return card;
    }

    private static GridPane buildActionGrid(Parent parent) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        Button addChildBtn = actionButton("Add Child", "#4F8CFF");
        Button tasksBtn = actionButton("Manage Tasks", "#7C5CFF");
        Button historyBtn = actionButton("View History", "#3B82F6");
        Button analyticsBtn = actionButton("Analytics", "#14B8A6");
        Button interestBtn = actionButton("Apply Interest", "#22C55E");

        addChildBtn.setOnAction(e -> KidBankApp.navigate(CreateChildScreen.build(parent)));
        tasksBtn.setOnAction(e -> KidBankApp.navigate(ParentTaskScreen.build(parent)));
        historyBtn.setOnAction(e -> {
            List<Child> k = BankService.getInstance().getChildren(parent.getUsername());
            if (!k.isEmpty()) KidBankApp.navigate(TransactionHistoryScreen.build(k.get(0), parent));
            else showAlert("Add a child account first.");
        });
        analyticsBtn.setOnAction(e -> {
            List<Child> k = BankService.getInstance().getChildren(parent.getUsername());
            if (!k.isEmpty()) KidBankApp.navigate(AnalyticsScreen.buildForParent(parent, k.get(0)));
            else showAlert("Add a child account first.");
        });
        interestBtn.setOnAction(e -> applyInterest(parent));

        grid.add(addChildBtn, 0, 0);
        grid.add(tasksBtn, 1, 0);
        grid.add(historyBtn, 2, 0);
        grid.add(analyticsBtn, 0, 1);
        grid.add(interestBtn, 1, 1);

        return grid;
    }

    private static Button actionButton(String text, String color) {
        Button b = new Button(text);
        b.setMinSize(180, 70);
        b.setWrapText(true);
        b.setStyle("-fx-background-color: " + color + "22; -fx-text-fill: #EAF0FF; "
                + "-fx-font-weight: 700; -fx-background-radius: 14; "
                + "-fx-border-color: " + color + "66; -fx-border-radius: 14; -fx-cursor: hand;");
        return b;
    }

    private static VBox buildPendingApprovals(Parent parent) {
        VBox box = new VBox(10);
        List<Task> pending = BankService.getInstance().getTasksForParent(parent.getUsername())
                .stream().filter(t -> t.getStatus() == Task.Status.COMPLETED_BY_CHILD).toList();

        if (pending.isEmpty()) {
            VBox empty = new VBox();
            empty.setPadding(new Insets(14));
            empty.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 12;");
            Label txt = new Label("No tasks awaiting approval.");
            txt.setStyle("-fx-text-fill: #9AA7C7;");
            empty.getChildren().add(txt);
            box.getChildren().add(empty);
            return box;
        }

        for (Task task : pending) box.getChildren().add(buildApprovalRow(task, parent));
        return box;
    }

    private static HBox buildApprovalRow(Task task, Parent parent) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 12; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12;");

        VBox info = new VBox(3);
        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #EAF0FF;");
        Label sub = new Label("Child: " + task.getChildUsername() + " — Reward: £" + String.format("%.2f", task.getRewardAmount()));
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #9AA7C7;");
        info.getChildren().addAll(title, sub);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button approve = new Button("Approve");
        approve.setStyle("-fx-background-color: #1BC47D; -fx-text-fill: #04150E; -fx-font-weight: 700; -fx-background-radius: 10;");
        approve.setOnAction(e -> {
            BankService.getInstance().approveTask(task.getTaskId());
            KidBankApp.navigate(build(parent));
        });

        Button reject = new Button("Reject");
        reject.setStyle("-fx-background-color: #FF5D7A; -fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 10;");
        reject.setOnAction(e -> {
            BankService.getInstance().rejectTask(task.getTaskId());
            KidBankApp.navigate(build(parent));
        });

        row.getChildren().addAll(info, sp, approve, reject);
        return row;
    }

    private static void applyInterest(Parent parent) {
        List<Child> kids = BankService.getInstance().getChildren(parent.getUsername());
        if (kids.isEmpty()) {
            showAlert("No children to apply interest to.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("2.0");
        dialog.setTitle("Apply Savings Interest");
        dialog.setHeaderText("Interest Rate");
        dialog.setContentText("Enter annual interest rate (%):");
        dialog.initOwner(KidBankApp.getPrimaryStage());

        dialog.showAndWait().ifPresent(input -> {
            try {
                double rate = Double.parseDouble(input.trim());
                if (rate <= 0) throw new NumberFormatException();

                int count = 0;
                for (Child c : kids) {
                    double savBal = c.getSavingsAccount().getBalance();
                    if (savBal > 0) {
                        double interest = Math.round(savBal * rate / 100.0 * 100.0) / 100.0;
                        BankService.getInstance().deposit(c.getUsername(), "Savings", interest, "Interest (" + rate + "%)");
                        count++;
                    }
                }

                showAlert("Interest applied to " + count + " savings account(s).");
                KidBankApp.navigate(build(parent));
            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid positive rate.");
            } catch (IllegalArgumentException ex) {
                showAlert(ex.getMessage());
            }
        });
    }

    private static void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}