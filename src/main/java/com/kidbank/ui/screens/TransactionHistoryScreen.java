package com.kidbank.ui.screens;

import com.kidbank.model.*;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class TransactionHistoryScreen {

    public static Scene build(Child child, Parent parent) {
        // This screen works like a bank statement.
        // It shows all the transactions for both the spending and savings accounts.

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        HBox header = new HBox();
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #111A2C;");

        Label t = new Label("📜  Transaction History – " + child.getFullName());
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: #EAF0FF; "
                + "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 8 14;");
        back.setOnAction(e -> {
            if (parent != null) KidBankApp.navigate(ParentDashboard.build(parent));
            else KidBankApp.navigate(ChildDashboard.build(child));
        });

        header.getChildren().addAll(t, sp, back);
        root.setTop(header);

        // Tabs let the user switch between spending and savings history.
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab spendingTab = new Tab("💳  Spending Account");
        spendingTab.setContent(buildList(child.getCurrentAccount().getAccountId()));

        Tab savingsTab = new Tab("🏦  Savings Account");
        savingsTab.setContent(buildList(child.getSavingsAccount().getAccountId()));

        tabs.getTabs().addAll(spendingTab, savingsTab);
        root.setCenter(tabs);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static ScrollPane buildList(String accountId) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(16));

        List<Transaction> txs = BankService.getInstance().getTransactions(accountId);
        if (txs.isEmpty()) {
            box.getChildren().add(StyleUtil.subtitle("No transactions yet."));
        } else {
            for (Transaction tx : txs) box.getChildren().add(buildRow(tx));
        }

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }

    private static HBox buildRow(Transaction tx) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #1A2338; -fx-background-radius: 12; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-padding: 12;");

        String icon = switch (tx.getType()) {
            case DEPOSIT -> "⬆️";
            case WITHDRAWAL -> "⬇️";
            case TASK_REWARD -> "⭐";
            case GOAL_TRANSFER -> "🎯";
            case ALLOWANCE -> "📅";
        };

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 20px;");

        VBox info = new VBox(2);
        Label typeLbl = new Label(tx.getType().toString().replace("_", " "));
        typeLbl.setStyle("-fx-font-weight: 800; -fx-font-size: 12px; -fx-text-fill: #EAF0FF;");
        Label noteLbl = StyleUtil.subtitle(tx.getNote().isEmpty() ? "–" : tx.getNote());
        Label dateLbl = StyleUtil.subtitle(tx.getFormattedDate());
        info.getChildren().addAll(typeLbl, noteLbl, dateLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String sign = tx.isCredit() ? "+" : "-";
        String color = tx.isCredit() ? StyleUtil.GREEN : StyleUtil.RED;
        Label amtLbl = new Label(sign + "£" + String.format("%.2f", tx.getAmount()));
        amtLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + color + ";");
        Label balLbl = StyleUtil.subtitle("Balance: £" + String.format("%.2f", tx.getBalanceAfter()));

        VBox right = new VBox(2);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.getChildren().addAll(amtLbl, balLbl);

        row.getChildren().addAll(iconLbl, info, spacer, right);
        return row;
    }
}