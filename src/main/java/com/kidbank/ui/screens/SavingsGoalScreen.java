package com.kidbank.ui.screens;

import com.kidbank.model.*;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.screens.FinanceCarousel;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;



//bu SavingsGoalScreen, bolalar o'z tejash maqsadlarini yaratishi va boshqarishi mumkin bo'lgan ekran.

public class SavingsGoalScreen {

    public static Scene build(Child child) {
        // This screen is for savings goals.
        // It lets the child create goals and add money to them over time.

        child = BankService.getInstance().getChild(child.getUsername());
        final Child c = child;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        HBox header = new HBox(12);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #111A2C, #1E293B);"
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");

        Label t = new Label("🎯  My Savings Goals");
        t.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label balLbl = new Label("Spending balance: £" + String.format("%.2f", c.getCurrentAccount().getBalance()));
        balLbl.setStyle("-fx-text-fill: #9AA7C7; -fx-font-size: 12px;");

        Button back = new Button("← Dashboard");
        back.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #EAF0FF; "
                + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 8 14;");
        back.setOnAction(e -> KidBankApp.navigate(ChildDashboard.build(c)));

        header.getChildren().addAll(t, sp, balLbl, back);
        root.setTop(header);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label pageTitle = new Label("Your Savings Goals");
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label pageSub = new Label("Create milestones and track your progress.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        // This carousel gives the savings page a more fun and motivating feel.
        FinanceCarousel goalCarousel = new FinanceCarousel(List.of(
                new FinanceCarousel.CarouselSlide(
                        "Goal power",
                        "Save toward something exciting",
                        "Create milestones and build momentum with every contribution.",
                        "Dream → Save → Reach",
                        "#10B981",
                        "#059669"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Progress",
                        "Watch your ring grow",
                        "Each deposit moves you closer to your next achievement.",
                        "Small steps win",
                        "#4F8CFF",
                        "#2563EB"
                ),
                new FinanceCarousel.CarouselSlide(
                        "Motivation",
                        "You're closer than yesterday",
                        "A little savings today can become a big goal tomorrow.",
                        "Keep going!",
                        "#7C5CFF",
                        "#5B21B6"
                )
        ));

        content.getChildren().addAll(pageTitle, pageSub, goalCarousel);

        content.getChildren().add(StyleUtil.sectionHeader("➕  New Goal"));
        content.getChildren().add(buildCreateForm(c));

        List<SavingsGoal> goals = BankService.getInstance().getGoalsForChild(c.getUsername());
        content.getChildren().add(StyleUtil.sectionHeader("🎯  Your Goals (" + goals.size() + ")"));

        if (goals.isEmpty()) {
            VBox empty = new VBox();
            empty.setPadding(new Insets(14));
            empty.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 12;");
            Label txt = new Label("No goals yet. Create one above to get started!");
            txt.setStyle("-fx-text-fill: #9AA7C7;");
            empty.getChildren().add(txt);
            content.getChildren().add(empty);
        } else {
            for (SavingsGoal g : goals) {
                content.getChildren().add(buildGoalCard(g, c));
            }
        }

        scroll.setContent(content);
        root.setCenter(scroll);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static VBox buildCreateForm(Child child) {
        VBox card = new VBox();
        card.setPadding(new Insets(16));
        card.setSpacing(12);
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;");

        Label nameLbl = new Label("Goal Name");
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #EAF0FF;");

        TextField nameField = StyleUtil.textField("e.g. New bicycle");

        Label targetLbl = new Label("Target Amount (£)");
        targetLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #EAF0FF;");

        TextField targetField = StyleUtil.textField("e.g. 50.00");

        Label initLbl = new Label("Starting Contribution (£)");
        initLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #EAF0FF;");

        TextField initField = StyleUtil.textField("0.00 – taken from Spending account");

        Label errLbl = StyleUtil.errorLabel();

        Button createBtn = StyleUtil.primaryBtn("🎯  Create Goal");
        createBtn.setMaxWidth(220);
        createBtn.setOnAction(e -> {
            StyleUtil.clearError(errLbl);
            String name = nameField.getText().trim();
            String target = targetField.getText().trim();
            String init = initField.getText().trim();
            if (name.isEmpty() || target.isEmpty()) {
                StyleUtil.showError(errLbl, "Goal name and target are required.");
                return;
            }
            double targetAmt, initAmt = 0;
            try {
                targetAmt = Double.parseDouble(target);
                if (targetAmt <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                StyleUtil.showError(errLbl, "Target must be a positive number.");
                return;
            }
            try {
                if (!init.isEmpty()) initAmt = Double.parseDouble(init);
                if (initAmt < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                StyleUtil.showError(errLbl, "Contribution must be a valid positive number.");
                return;
            }
            try {
                BankService.getInstance().createGoal(child.getUsername(), name, targetAmt, initAmt);
                KidBankApp.navigate(build(child));
            } catch (IllegalArgumentException ex) {
                StyleUtil.showError(errLbl, ex.getMessage());
            }
        });

        card.getChildren().addAll(nameLbl, nameField, targetLbl, targetField, initLbl, initField, errLbl, createBtn);
        return card;
    }

    private static VBox buildGoalCard(SavingsGoal goal, Child child) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14;");

        boolean done = goal.isCompleted();

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label((done ? "🏆  " : "🎯  ") + goal.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #EAF0FF;");

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Label pct = new Label(String.format("%.0f%%", goal.getProgressPercent()));
        pct.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: "
                + (done ? "#10B981" : "#4F8CFF") + ";");

        header.getChildren().addAll(name, sp2, pct);

        ProgressBar bar = new ProgressBar(goal.getProgressPercent() / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(10);
        bar.setStyle(done
                ? "-fx-accent: #10B981; -fx-control-inner-background: #10B98122;"
                : "-fx-accent: #4F8CFF; -fx-control-inner-background: #4F8CFF22;");

        Label amounts = new Label(String.format(
                "Saved: £%.2f  /  Target: £%.2f  (£%.2f remaining)",
                goal.getSavedAmount(), goal.getTargetAmount(), goal.getRemaining()));
        amounts.setStyle("-fx-font-size: 12px; -fx-text-fill: #9AA7C7;");

        if (done) {
            Label congrats = new Label("🎉  Goal reached! Well done!");
            congrats.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
            card.getChildren().addAll(header, bar, amounts, congrats);
        } else {
            HBox contributeRow = new HBox(10);
            contributeRow.setAlignment(Pos.CENTER_LEFT);

            TextField contribField = StyleUtil.textField("Amount to add...");
            contribField.setPrefWidth(160);

            Label errInline = StyleUtil.errorLabel();

            Button addBtn = StyleUtil.successBtn("Add £");
            addBtn.setMaxWidth(Region.USE_PREF_SIZE);
            addBtn.setOnAction(e -> {
                StyleUtil.clearError(errInline);
                String val = contribField.getText().trim();
                if (val.isEmpty()) {
                    StyleUtil.showError(errInline, "Enter an amount.");
                    return;
                }
                double amt;
                try {
                    amt = Double.parseDouble(val);
                    if (amt <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    StyleUtil.showError(errInline, "Invalid amount.");
                    return;
                }
                try {
                    BankService.getInstance().contributeToGoal(goal.getGoalId(), amt);
                    KidBankApp.navigate(build(child));
                } catch (IllegalArgumentException ex) {
                    StyleUtil.showError(errInline, ex.getMessage());
                }
            });

            contributeRow.getChildren().addAll(contribField, addBtn);
            card.getChildren().addAll(header, bar, amounts, contributeRow, errInline);
        }

        return card;
    }
}