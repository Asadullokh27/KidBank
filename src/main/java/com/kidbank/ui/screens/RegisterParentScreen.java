package com.kidbank.ui.screens;

import com.kidbank.model.Parent;
import com.kidbank.service.BankService;
import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;


//bu RegisterParentScreen, ota-ona yangi hisob yaratishi mumkin bo'lgan ekran.

public class RegisterParentScreen {

    public static Scene build() {
        // This screen is for creating a new parent account.
        // It asks for the parent name, username, password, and password confirmation.

        HBox root = new HBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        VBox accent = new VBox(16);
        accent.setAlignment(Pos.CENTER_LEFT);
        accent.setPrefWidth(360);
        accent.setPadding(new Insets(48));
        accent.setStyle("-fx-background-color: linear-gradient(to bottom right, #0F172A, #0F766E);");

        Label badge = new Label("PARENT SETUP");
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: #EAF0FF; "
                + "-fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 6 10; -fx-background-radius: 999;");

        Label msg = new Label("Create your\nParent Account");
        msg.setStyle("-fx-font-size: 30px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");
        msg.setTextAlignment(TextAlignment.LEFT);

        Label tip = new Label("Register once, then add and manage child accounts from your dashboard.");
        tip.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(234,240,255,0.78);");
        tip.setWrapText(true);

        VBox perks = new VBox(10);
        perks.getChildren().addAll(
                perk("Secure parent sign-in"),
                perk("Approve chores & rewards"),
                perk("Track spending insights")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        accent.getChildren().addAll(badge, msg, tip, perks, spacer, miniFooter());

        // Form card on the right side.
        VBox right = new VBox();
        right.setAlignment(Pos.CENTER);
        right.setPadding(new Insets(24));
        HBox.setHgrow(right, Priority.ALWAYS);

        VBox card = StyleUtil.card();
        card.setMaxWidth(420);
        card.setSpacing(12);
        card.setStyle("-fx-background-color: #131C31; -fx-background-radius: 18; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 18; -fx-padding: 24;");

        Label title = new Label("New Parent Account");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label sub = new Label("Create your family banking profile.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        // Full name input.
        Label nameLbl = StyleUtil.fieldLabel("Full Name");
        TextField nameField = StyleUtil.textField("Your full name");

        // Username input.
        Label userLbl = StyleUtil.fieldLabel("Username");
        TextField userField = StyleUtil.textField("Min 3 characters");

        // Password input.
        Label passLbl = StyleUtil.fieldLabel("Password");
        PasswordField passField = StyleUtil.passwordField("Min 6 characters");

        // Confirm password input.
        Label confirmLbl = StyleUtil.fieldLabel("Confirm Password");
        PasswordField confirmField = StyleUtil.passwordField("Re-enter password");

        Label errLbl = StyleUtil.errorLabel();

        Button registerBtn = StyleUtil.primaryBtn("✅  Create Account");
        registerBtn.setOnAction(e -> handleRegister(
                nameField.getText().trim(),
                userField.getText().trim(),
                passField.getText(),
                confirmField.getText(),
                errLbl
        ));

        Button backBtn = StyleUtil.linkBtn("← Back to welcome");
        backBtn.setOnAction(e -> KidBankApp.navigate(WelcomeScreen.build()));

        card.getChildren().addAll(
                title, sub, StyleUtil.separator(),
                nameLbl, nameField,
                userLbl, userField,
                passLbl, passField,
                confirmLbl, confirmField,
                errLbl, registerBtn, backBtn
        );

        right.getChildren().add(card);
        root.getChildren().addAll(accent, right);
        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static VBox perk(String text) {
        VBox box = new VBox();
        Label l = new Label("•  " + text);
        l.setStyle("-fx-text-fill: rgba(234,240,255,0.82); -fx-font-size: 13px;");
        box.getChildren().add(l);
        return box;
    }

    private static VBox miniFooter() {
        VBox box = new VBox(4);
        Label l1 = new Label("Modern fintech feel");
        l1.setStyle("-fx-text-fill: rgba(234,240,255,0.9); -fx-font-size: 12px; -fx-font-weight: 700;");
        Label l2 = new Label("Rounded cards • Hover motion • Clean spacing");
        l2.setStyle("-fx-text-fill: rgba(234,240,255,0.6); -fx-font-size: 11px;");
        box.getChildren().addAll(l1, l2);
        return box;
    }

    private static void handleRegister(String fullName, String username,
                                       String password, String confirm, Label errLbl) {
        StyleUtil.clearError(errLbl);
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            StyleUtil.showError(errLbl, "All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            StyleUtil.showError(errLbl, "Passwords do not match.");
            return;
        }
        try {
            Parent p = BankService.getInstance().registerParent(username, fullName, password);
            KidBankApp.navigate(ParentDashboard.build(p));
        } catch (IllegalArgumentException ex) {
            StyleUtil.showError(errLbl, ex.getMessage());
        }
    }
}