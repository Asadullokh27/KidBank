package com.kidbank.ui.screens;

import com.kidbank.model.*;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.screens.FinanceCarousel;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;



//bu faylda child va parent uchun analytics screen lar bor. Bu ekranlarda charts va insights lar boladi.
// Charts lar child ning spending, earning, saving trendlarini korsatadi.

public class AnalyticsScreen {


    //buildForChild va buildForParent methodlari orqali child va parent
    // uchun alohida analytics screen lar quriladi

    public static Scene buildForChild(Child child) {
        return buildScene(child, null);
    }

    public static Scene buildForParent(Parent parent, Child child) {
        return buildScene(child, parent);
    }

    private static Scene buildScene(Child child, Parent parent) {
        // This screen shows charts and finance insights.
        // I used summary cards and graphs to make the data easier to understand.

        child = BankService.getInstance().getChild(child.getUsername());
        final Child c = child;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        HBox header = new HBox(12);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #111A2C, #1E293B);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label t = new Label("📊  Analytics – " + c.getFullName());
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #EAF0FF; "
                + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 8 14;");
        back.setOnAction(e -> {
            if (parent != null) KidBankApp.navigate(ParentDashboard.build(parent));
            else KidBankApp.navigate(ChildDashboard.build(c));
        });

        header.getChildren().addAll(t, sp, back);
        root.setTop(header);

        List<Transaction> currentTxs = BankService.getInstance()
                .getTransactions(c.getCurrentAccount().getAccountId());
        List<Transaction> savingsTxs = BankService.getInstance()
                .getTransactions(c.getSavingsAccount().getAccountId());

        List<Transaction> allTxs = new ArrayList<>();
        allTxs.addAll(currentTxs);
        allTxs.addAll(savingsTxs);

        double totalEarned = allTxs.stream().filter(Transaction::isCredit)
                .mapToDouble(Transaction::getAmount).sum();
        double totalSpent = allTxs.stream()
                .filter(tx -> tx.getType() == Transaction.Type.WITHDRAWAL)
                .mapToDouble(Transaction::getAmount).sum();
        long tasksDone = BankService.getInstance().getTasksForChild(c.getUsername())
                .stream().filter(tk -> tk.getStatus() == Task.Status.APPROVED).count();
        double totalSaved = c.getSavingsAccount().getBalance();

        // Summary cards at the top.
        HBox summaryRow = new HBox(14);
        summaryRow.setPadding(new Insets(18, 20, 4, 20));
        summaryRow.getChildren().addAll(
                statCard("💰", "Total Earned", String.format("£%.2f", totalEarned), "#10B981"),
                statCard("💸", "Total Spent", String.format("£%.2f", totalSpent), "#EF4444"),
                statCard("✅", "Tasks Done", String.valueOf(tasksDone), "#4F8CFF"),
                statCard("🏦", "Total Savings", String.format("£%.2f", totalSaved), "#7C5CFF")
        );

        FinanceCarousel insightCarousel = new FinanceCarousel(List.of(
                new FinanceCarousel.CarouselSlide(
                        "Insights",
                        "Understand spending trends",
                        "See how much is earned, spent, and saved over time.",
                        "Data made simple",
                        "#4F8CFF",
                        "#7C5CFF"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Performance",
                        "Track task completion",
                        "Monitor how many approved tasks are contributing to savings.",
                        "Habit-building view",
                        "#10B981",
                        "#14B8A6"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Balance",
                        "Watch money move",
                        "Balance charts help kids and parents understand the flow of funds.",
                        "Banking clarity",
                        "#F59E0B",
                        "#EF4444"
                )
        ));

        // Charts go inside a scroll pane so the page stays neat.
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox chartsBox = new VBox(20);
        chartsBox.setPadding(new Insets(12, 20, 20, 20));

        HBox chartRow1 = new HBox(16);
        chartRow1.getChildren().addAll(
                buildWeeklyBarChart(currentTxs),
                buildSpendingPieChart(allTxs)
        );

        VBox lineCard = StyleUtil.card();
        lineCard.getChildren().addAll(
                StyleUtil.sectionHeader("📈  Balance Trend"),
                buildBalanceTrendChart(currentTxs)
        );

        chartsBox.getChildren().addAll(insightCarousel, chartRow1, lineCard);
        scroll.setContent(chartsBox);

        VBox mainContent = new VBox(0, summaryRow, scroll);
        root.setCenter(mainContent);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static VBox statCard(String emoji, String label, String value, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + color + "22; -fx-background-radius: 12; "
                + "-fx-border-color: " + color + "66; -fx-border-radius: 12;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label em = new Label(emoji);
        em.setStyle("-fx-font-size: 22px;");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #9AA7C7; -fx-font-weight: bold;");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(em, lbl, val);
        return card;
    }

    private static VBox buildWeeklyBarChart(List<Transaction> txs) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("£ Amount");

        BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
        bar.setTitle("Weekly Activity");
        bar.setPrefWidth(420);
        bar.setPrefHeight(240);
        bar.setLegendVisible(true);

        XYChart.Series<String, Number> earnedSeries = new XYChart.Series<>();
        earnedSeries.setName("Earned");
        XYChart.Series<String, Number> spentSeries = new XYChart.Series<>();
        spentSeries.setName("Spent");

        LocalDate today = LocalDate.now();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            String dayLabel = days[day.getDayOfWeek().getValue() - 1];
            final LocalDate fd = day;
            double earned = txs.stream()
                    .filter(tx -> tx.isCredit() && tx.getTimestamp().toLocalDate().equals(fd))
                    .mapToDouble(Transaction::getAmount).sum();
            double spent = txs.stream()
                    .filter(tx -> tx.getType() == Transaction.Type.WITHDRAWAL && tx.getTimestamp().toLocalDate().equals(fd))
                    .mapToDouble(Transaction::getAmount).sum();
            earnedSeries.getData().add(new XYChart.Data<>(dayLabel, earned));
            spentSeries.getData().add(new XYChart.Data<>(dayLabel, spent));
        }

        bar.getData().addAll(earnedSeries, spentSeries);

        VBox card = StyleUtil.card();
        card.getChildren().addAll(StyleUtil.sectionHeader("📊  Last 7 Days"), bar);
        return card;
    }

    private static VBox buildSpendingPieChart(List<Transaction> txs) {
        PieChart pie = new PieChart();
        pie.setTitle("Spending Breakdown");
        pie.setPrefWidth(340);
        pie.setPrefHeight(240);
        pie.setLegendVisible(true);

        Map<String, Double> breakdown = new LinkedHashMap<>();
        for (Transaction tx : txs) {
            if (!tx.isCredit()) continue;
            String category = switch (tx.getType()) {
                case TASK_REWARD -> "Task Rewards";
                case DEPOSIT -> "Deposits";
                case ALLOWANCE -> "Allowance";
                default -> "Other";
            };
            breakdown.merge(category, tx.getAmount(), Double::sum);
        }

        if (breakdown.isEmpty()) {
            breakdown.put("No data yet", 1.0);
        }
        breakdown.forEach((k, v) -> pie.getData().add(new PieChart.Data(k, v)));

        VBox card = StyleUtil.card();
        card.getChildren().addAll(StyleUtil.sectionHeader("🥧  Income Sources"), pie);
        return card;
    }

    private static LineChart<String, Number> buildBalanceTrendChart(List<Transaction> txs) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Balance (£)");

        LineChart<String, Number> line = new LineChart<>(xAxis, yAxis);
        line.setTitle("Spending Account Balance Over Time");
        line.setPrefHeight(220);
        line.setCreateSymbols(false);
        line.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Balance");

        List<Transaction> sorted = txs.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());
        int start = Math.max(0, sorted.size() - 10);
        for (int i = start; i < sorted.size(); i++) {
            Transaction tx = sorted.get(i);
            series.getData().add(new XYChart.Data<>(tx.getFormattedDate(), tx.getBalanceAfter()));
        }
        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("Now", 0.0));
        }

        line.getData().add(series);
        return line;
    }
}