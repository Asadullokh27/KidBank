package com.kidbank.ui.screens;

import com.kidbank.model.*;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DepositWithdrawScreen {

    public static Scene build(Child child, Parent parent, String defaultAcct) {
        // This screen moves money in or out of the child's accounts.
        // It works for both parent and child depending on who opened it.

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        VBox card = StyleUtil.card();
        card.setMaxWidth(520);
        card.setSpacing(14);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: #131C31; -fx-background-radius: 18; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 18;");

        Label title = new Label("💸  Money Transfer");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label sub = new Label("Move money between spending and savings.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        // Show current balances.
        HBox balRow = new HBox(24);
        Label curBal = new Label("Spending: £" + String.format("%.2f", child.getCurrentAccount().getBalance()));
        curBal.setStyle("-fx-font-size: 13px; -fx-text-fill: #4F8CFF; -fx-font-weight: bold;");
        Label savBal = new Label("Savings: £" + String.format("%.2f", child.getSavingsAccount().getBalance()));
        savBal.setStyle("-fx-font-size: 13px; -fx-text-fill: #1BC47D; -fx-font-weight: bold;");
        balRow.getChildren().addAll(curBal, savBal);

        // Choose deposit or withdraw.
        ToggleGroup actionGrp = new ToggleGroup();
        RadioButton depositRb = new RadioButton("Deposit");
        RadioButton withdrawRb = new RadioButton("Withdraw");
        depositRb.setToggleGroup(actionGrp);
        withdrawRb.setToggleGroup(actionGrp);
        depositRb.setSelected(true);

        HBox actionRow = new HBox(20, depositRb, withdrawRb);
        if (parent == null) {
            // Child users should only withdraw when the app allows it.
            depositRb.setDisable(true);
            withdrawRb.setSelected(true);
        }

        // Choose the account type.
        Label acctLbl = StyleUtil.fieldLabel("Account");
        ToggleGroup acctGrp = new ToggleGroup();
        RadioButton currentRb = new RadioButton("Spending (Current)");
        RadioButton savingsRb = new RadioButton("Savings");
        currentRb.setToggleGroup(acctGrp);
        savingsRb.setToggleGroup(acctGrp);
        if ("Savings".equals(defaultAcct)) savingsRb.setSelected(true);
        else currentRb.setSelected(true);

        HBox acctRow = new HBox(20, currentRb, savingsRb);

        // Amount input.
        Label amtLbl = StyleUtil.fieldLabel("Amount (£)");
        TextField amtField = StyleUtil.textField("Enter amount, e.g. 5.00");

        // Optional note input.
        Label noteLbl = StyleUtil.fieldLabel("Note (optional)");
        TextField noteField = StyleUtil.textField("e.g. Birthday money");

        Label errLbl = StyleUtil.errorLabel();

        Button confirmBtn = StyleUtil.primaryBtn("✅  Confirm");
        confirmBtn.setOnAction(e -> {
            boolean isDeposit = depositRb.isSelected();
            String acctType = currentRb.isSelected() ? "Current" : "Savings";
            handleTransaction(child, parent, isDeposit, acctType,
                    amtField.getText().trim(), noteField.getText().trim(), errLbl);
        });

        Button backBtn = StyleUtil.linkBtn(parent != null ? "← Back to Dashboard" : "← Back");
        backBtn.setOnAction(e -> {
            if (parent != null) KidBankApp.navigate(ParentDashboard.build(parent));
            else KidBankApp.navigate(ChildDashboard.build(child));
        });

        card.getChildren().addAll(
                title, sub, StyleUtil.separator(),
                StyleUtil.fieldLabel("Child: " + child.getFullName()), balRow,
                StyleUtil.fieldLabel("Action"), actionRow,
                acctLbl, acctRow,
                amtLbl, amtField,
                noteLbl, noteField,
                errLbl, confirmBtn, backBtn
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(50));
        root.setCenter(center);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static void handleTransaction(Child child, Parent parent,
                                          boolean isDeposit, String acctType,
                                          String amtStr, String note, Label errLbl) {
        StyleUtil.clearError(errLbl);
        if (amtStr.isEmpty()) {
            StyleUtil.showError(errLbl, "Please enter an amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            StyleUtil.showError(errLbl, "Amount must be a positive number.");
            return;
        }
        try {
            BankService svc = BankService.getInstance();
            if (isDeposit) svc.deposit(child.getUsername(), acctType, amount, note);
            else svc.withdraw(child.getUsername(), acctType, amount, note);

            Child fresh = svc.getChild(child.getUsername());
            if (parent != null) KidBankApp.navigate(ParentDashboard.build(parent));
            else KidBankApp.navigate(ChildDashboard.build(fresh));
        } catch (IllegalArgumentException ex) {
            StyleUtil.showError(errLbl, ex.getMessage());
        }
    }
}