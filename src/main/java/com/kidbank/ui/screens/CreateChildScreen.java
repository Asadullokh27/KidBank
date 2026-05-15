package com.kidbank.ui.screens;

import com.kidbank.model.Parent;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


//bu CreateChildScreen, ota-ona yangi bola hisobini yaratishi mumkin bo'lgan ekran.

public class CreateChildScreen {

    public static Scene build(Parent parent) {
        // This screen lets the parent add a child account.
        // It collects the child's details, PIN, and starting balance.

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        VBox card = StyleUtil.card();
        card.setMaxWidth(460);
        card.setSpacing(12);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: #131C31; -fx-background-radius: 18; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 18;");

        Label title = new Label("➕  Add Child Account");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label sub = new Label("Create a new child profile and set up a PIN.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        // Child full name input.
        Label nameLbl = StyleUtil.fieldLabel("Child's Full Name");
        TextField nameField = StyleUtil.textField("e.g. Tommy");

        // Username input.
        Label userLbl = StyleUtil.fieldLabel("Username");
        TextField userField = StyleUtil.textField("Unique login name");

        // PIN input.
        Label pinLbl = StyleUtil.fieldLabel("4-Digit PIN");
        PasswordField pinField = StyleUtil.passwordField("Exactly 4 digits");

        // Starting balance input.
        Label balLbl = StyleUtil.fieldLabel("Starting Balance (£)");
        TextField balField = StyleUtil.textField("0.00");

        Label errLbl = StyleUtil.errorLabel();

        Button createBtn = StyleUtil.primaryBtn("✅  Create Account");
        createBtn.setOnAction(e -> handleCreate(
                parent,
                nameField.getText().trim(),
                userField.getText().trim(),
                pinField.getText(),
                balField.getText().trim(),
                errLbl
        ));

        Button backBtn = StyleUtil.linkBtn("← Back to Dashboard");
        backBtn.setOnAction(e -> KidBankApp.navigate(ParentDashboard.build(parent)));

        card.getChildren().addAll(
                title, sub, StyleUtil.separator(),
                nameLbl, nameField,
                userLbl, userField,
                pinLbl, pinField,
                balLbl, balField,
                errLbl, createBtn, backBtn
        );

        StackPane center = new StackPane(card);
        center.setPadding(new Insets(60));
        root.setCenter(center);

        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static void handleCreate(Parent parent, String fullName, String username,
                                     String pin, String balStr, Label errLbl) {
        StyleUtil.clearError(errLbl);
        if (fullName.isEmpty() || username.isEmpty() || pin.isEmpty()) {
            StyleUtil.showError(errLbl, "Name, username and PIN are required.");
            return;
        }
        double balance = 0.0;

        try {
            if (!balStr.isEmpty()) balance = Double.parseDouble(balStr);
            if (balance < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            StyleUtil.showError(errLbl, "Starting balance must be a valid positive number.");
            return;
        }
        try {
            BankService.getInstance().createChild(parent.getUsername(), username, fullName, pin, balance);
            KidBankApp.navigate(ParentDashboard.build(parent));
        } catch (IllegalArgumentException ex) {
            StyleUtil.showError(errLbl, ex.getMessage());
        }
    }
}