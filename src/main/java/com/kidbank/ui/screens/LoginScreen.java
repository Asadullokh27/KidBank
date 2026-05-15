package com.kidbank.ui.screens;

import com.kidbank.model.Child;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;


//bu LoginScreen, ota-ona va bola uchun umumiy login ekranidir.
//Ular o'z foydalanuvchi nomlari va parollarini kiritib tizimga kirishlari mumkin.


public class LoginScreen {

    public static Scene buildParentLogin() {
        return buildLogin(true);
    }

    public static Scene buildChildLogin() {
        return buildLogin(false);
    }

    private static Scene buildLogin(boolean isParent) {
        // This screen handles both parent and child login.
        // I used one method because the layout is almost the same for both.

        HBox root = new HBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        // Left side panel with a custom color based on the user type.
        VBox accent = new VBox(16);
        accent.setAlignment(Pos.CENTER_LEFT);
        accent.setPrefWidth(360);
        accent.setPadding(new Insets(50, 36, 50, 36));
        accent.setStyle(isParent
                ? "-fx-background-color: linear-gradient(to bottom right, #1E293B, #1D4ED8);"
                : "-fx-background-color: linear-gradient(to bottom right, #1E293B, #0F766E);");

        Label badge = new Label(isParent ? "PARENT ACCESS" : "CHILD ACCESS");
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.18); -fx-text-fill: #EAF0FF; "
                + "-fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 6 10; -fx-background-radius: 999;");

        Label title = new Label(isParent ? "Secure Family\nBanking Control" : "Smart Saving\nStarts Here");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");
        title.setTextAlignment(TextAlignment.LEFT);

        Label copy = new Label(isParent
                ? "Approve tasks, track balances,\nand guide healthy money habits."
                : "Complete tasks, track progress,\nand grow your savings goals.");
        copy.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(234,240,255,0.78);");
        copy.setWrapText(true);

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);

        Label mark = new Label("PocketPal");
        mark.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: rgba(234,240,255,0.92);");

        accent.getChildren().addAll(badge, title, copy, grow, mark);

        // Right side login form.
        VBox right = new VBox();
        right.setAlignment(Pos.CENTER);
        right.setPadding(new Insets(20));
        right.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(right, Priority.ALWAYS);

        VBox card = StyleUtil.card();
        card.setMaxWidth(390);
        card.setSpacing(14);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: #131C31; -fx-background-radius: 18; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 18;");

        Label heading = new Label(isParent ? "Welcome Back" : "Welcome, Champ");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label sub = new Label(isParent ? "Sign in with your parent credentials" : "Sign in with your username and PIN");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        // Username field for login.
        Label userLbl = new Label("Username");
        userLbl.setStyle("-fx-text-fill: #C9D5F0; -fx-font-size: 12px; -fx-font-weight: 600;");
        TextField userField = StyleUtil.textField("Your username");

        // Password or PIN field depending on user type.
        Label credLbl = new Label(isParent ? "Password" : "PIN (4 digits)");
        credLbl.setStyle("-fx-text-fill: #C9D5F0; -fx-font-size: 12px; -fx-font-weight: 600;");
        PasswordField credField = StyleUtil.passwordField(isParent ? "Enter password" : "4-digit PIN");

        Label errLbl = StyleUtil.errorLabel();

        // Main sign in button.
        Button signInBtn = new Button(isParent ? "Sign In as Parent" : "Sign In as Child");
        signInBtn.setOnAction(e -> handleLogin(userField.getText().trim(), credField.getText(), isParent, errLbl));

        // These buttons switch between parent and child login pages.
        Button switchBtn = StyleUtil.linkBtn(isParent ? "Sign in as Child instead ->" : "Sign in as Parent instead ->");
        switchBtn.setOnAction(e -> KidBankApp.navigate(isParent ? buildChildLogin() : buildParentLogin()));

        Button backBtn = StyleUtil.linkBtn("<- Back to welcome");
        backBtn.setOnAction(e -> KidBankApp.navigate(WelcomeScreen.build()));

        HBox links = new HBox(18, backBtn, switchBtn);
        links.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                heading, sub,
                userLbl, userField,
                credLbl, credField,
                errLbl, signInBtn, links
        );

        right.getChildren().add(card);
        root.getChildren().addAll(accent, right);
        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    private static void handleLogin(String username, String credential, boolean isParent, Label errLbl) {
        StyleUtil.clearError(errLbl);
        if (username.isEmpty() || credential.isEmpty()) {
            StyleUtil.showError(errLbl, "Please fill in all fields.");
            return;
        }

        try {
            BankService svc = BankService.getInstance();
            if (isParent) {
                Parent p = svc.authenticateParent(username, credential);
                KidBankApp.navigate(ParentDashboard.build(p));
            } else {
                Child c = svc.authenticateChild(username, credential);
                KidBankApp.navigate(ChildDashboard.build(c));
            }
        } catch (IllegalArgumentException ex) {
            StyleUtil.showError(errLbl, ex.getMessage());
        }
    }
}