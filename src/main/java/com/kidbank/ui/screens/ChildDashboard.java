package com.kidbank.ui.screens;

import com.kidbank.model.Child;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.screens.FinanceCarousel;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ChildDashboard {

    public static Scene build(Child child) {
        child = BankService.getInstance().getChild(child.getUsername());
        final Child c = child;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        // Header
        HBox header = new HBox(12);
        header.setPadding(new Insets(14, 22, 14, 22));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #111A2C, #1E293B);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label brand = new Label("PocketPal");
        brand.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label userLbl = new Label("⭐  " + c.getFullName() + "  ·  Child");
        userLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        Button logoutBtn = StyleUtil.ghostBtn("Sign Out");
        logoutBtn.setOnAction(e -> KidBankApp.navigate(WelcomeScreen.build()));

        header.getChildren().addAll(brand, sp, userLbl, logoutBtn);
        root.setTop(header);

        VBox content = new VBox(18);
        content.setPadding(new Insets(22));

        Label title = new Label("My Money Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label subtitle = new Label("Track your spending, savings, and rewards.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        HBox balRow = new HBox(16);
        balRow.getChildren().addAll(
                StyleUtil.statCard("Spending Account",
                        "£" + String.format("%.2f", c.getCurrentAccount().getBalance()), "#4F8CFF", "#2563EB"),
                StyleUtil.statCard("Savings Account",
                        "£" + String.format("%.2f", c.getSavingsAccount().getBalance()), "#10B981", "#059669"),
                StyleUtil.statCard("Goal Progress",
                        goalText(c), "#7C5CFF", "#5B21B6")
        );

        FinanceCarousel carousel = new FinanceCarousel(List.of(
                new FinanceCarousel.CarouselSlide(
                        "Your money",
                        "Balance updates instantly",
                        "Watch your spending and savings accounts change as you earn or spend.",
                        "Balance → Control → Growth",
                        "#4F8CFF",
                        "#2563EB"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Earn rewards",
                        "Complete tasks and level up",
                        "Finish chores to unlock rewards and build positive habits.",
                        "Task streaks win",
                        "#10B981",
                        "#059669"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Savings goals",
                        "Work toward something special",
                        "Create a savings goal and add money any time you want.",
                        "Dreams in progress",
                        "#7C5CFF",
                        "#5B21B6"
                )
        ));
        carousel.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        Button tasksBtn = StyleUtil.actionTile("📋", "My Tasks", "Complete chores & earn money", "#0F766E", "#115E59");
        Button moneyBtn = StyleUtil.actionTile("💸", "Deposit / Withdraw", "Move money between accounts", "#4F8CFF", "#2563EB");
        Button goalsBtn = StyleUtil.actionTile("🎯", "My Goals", "Save toward something special", "#22C55E", "#16A34A");
        Button historyBtn = StyleUtil.actionTile("📜", "History", "View all your transactions", "#64748B", "#475569");
        Button analyticsBtn = StyleUtil.actionTile("📊", "Analytics", "Charts & spending insights", "#7C5CFF", "#6D28D9");
        Button rewardBtn = StyleUtil.actionTile("⭐", "Rewards", "See what you can unlock next", "#F59E0B", "#D97706");

        tasksBtn.setOnAction(e -> KidBankApp.navigate(ChildTaskScreen.build(c)));
        moneyBtn.setOnAction(e -> KidBankApp.navigate(DepositWithdrawScreen.build(c, null, "Current")));
        goalsBtn.setOnAction(e -> KidBankApp.navigate(SavingsGoalScreen.build(c)));
        historyBtn.setOnAction(e -> KidBankApp.navigate(TransactionHistoryScreen.build(c, null)));
        analyticsBtn.setOnAction(e -> KidBankApp.navigate(AnalyticsScreen.buildForChild(c)));
        rewardBtn.setOnAction(e -> KidBankApp.navigate(ChildTaskScreen.build(c)));

        grid.add(tasksBtn, 0, 0);
        grid.add(moneyBtn, 1, 0);
        grid.add(goalsBtn, 0, 1);
        grid.add(historyBtn, 1, 1);
        grid.add(analyticsBtn, 0, 2);
        grid.add(rewardBtn, 1, 2);

        content.getChildren().addAll(title, subtitle, balRow, carousel, grid);
        root.setCenter(content);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static String goalText(Child c) {
        double total = c.getSavingsAccount().getBalance();
        return total > 0 ? "£" + String.format("%.2f", total) : "Start saving";
    }
}