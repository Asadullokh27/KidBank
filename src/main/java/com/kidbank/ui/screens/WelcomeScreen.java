package com.kidbank.ui.screens;

import com.kidbank.ui.KidBankApp;
import com.kidbank.ui.screens.FinanceCarousel;
import com.kidbank.ui.components.StyleUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;


//WelcomeScreen, foydalanuvchi dasturga kirganda ko'radigan ekran.


public class WelcomeScreen {

    public static Scene build() {
        // This is the first screen the user sees.
        // It gives the app a modern welcome page with login options.

        HBox root = new HBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");

        // Left side branding panel.
        // I used this to show the app name, icon, and a few features.
        VBox left = new VBox(18);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPadding(new Insets(56));
        left.setPrefWidth(420);
        left.setStyle("-fx-background-color: linear-gradient(to bottom right, #111A2C, #1E293B);");

        StackPane logo = buildIcon();

        Label appName = new Label("PocketPal");
        appName.setStyle("-fx-font-size: 42px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label tagline = new Label("Modern banking habits for families and kids.");
        tagline.setStyle("-fx-font-size: 14px; -fx-text-fill: #9AA7C7;");
        tagline.setWrapText(true);

        VBox featureBox = new VBox(12);
        featureBox.getChildren().addAll(
                featureRow("💳", "Current & savings accounts"),
                featureRow("📋", "Task-based rewards"),
                featureRow("🎯", "Savings goals tracking"),
                featureRow("📊", "Analytics & insights")
        );

        // Small note at the bottom so the welcome page feels finished.
        Label note = new Label("Built for portfolio-ready fintech demos.");
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(234,240,255,0.55);");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        left.getChildren().addAll(logo, appName, tagline, featureBox, spacer, note);

        // Right side action area.
        // This is where the user chooses how to log in or register.
        VBox right = new VBox(20);
        right.setAlignment(Pos.CENTER);
        right.setPadding(new Insets(28));
        right.setStyle("-fx-background-color: linear-gradient(to bottom right, #0B1020, #121A2D);");
        HBox.setHgrow(right, Priority.ALWAYS);

        VBox card = StyleUtil.card();
        card.setMaxWidth(420);
        card.setSpacing(14);
        card.setStyle("-fx-background-color: #131C31; -fx-background-radius: 18; "
                + "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 18; -fx-padding: 24;");

        Label heading = new Label("Get Started");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #EAF0FF;");

        Label sub = new Label("Choose how you'd like to sign in.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #9AA7C7;");

        // Parent login button.
        Button parentBtn = StyleUtil.primaryBtn("🔑  Parent Login");
        parentBtn.setOnAction(e -> KidBankApp.navigate(LoginScreen.buildParentLogin()));

        // Child login button.
        Button childBtn = StyleUtil.successBtn("⭐  Child Login");
        childBtn.setOnAction(e -> KidBankApp.navigate(LoginScreen.buildChildLogin()));

        // Button for new parent registration.
        Button registerBtn = StyleUtil.secondaryBtn("📝  Create Parent Account");
        registerBtn.setOnAction(e -> KidBankApp.navigate(RegisterParentScreen.build()));

        Label hint = StyleUtil.subtitle("Parents register first, then add child accounts.");

        // These are the small style tip labels to make the page feel more polished.
        VBox quickTips = new VBox(8);
        quickTips.getChildren().addAll(
                tipPill("Smooth hover effects"),
                tipPill("Animated cards"),
                tipPill("Carousel-ready layout")
        );

        card.getChildren().addAll(
                heading, sub, StyleUtil.separator(),
                StyleUtil.fieldLabel("Existing account"),
                parentBtn, childBtn,
                StyleUtil.separator(),
                StyleUtil.fieldLabel("New to PocketPal?"),
                registerBtn, hint,
                StyleUtil.separator(),
                StyleUtil.fieldLabel("Design highlights"),
                quickTips
        );

        right.getChildren().add(card);
        root.getChildren().addAll(left, right);
        return new Scene(root, KidBankApp.APP_WIDTH, KidBankApp.APP_HEIGHT);
    }

    // This builds the round coin-style app icon.
    private static StackPane buildIcon() {
        StackPane p = new StackPane();
        Circle outer = new Circle(58);
        outer.setFill(Color.web("#4F8CFF"));
        Circle inner = new Circle(46);
        inner.setFill(Color.web("#7C5CFF"));
        Label sym = new Label("💰");
        sym.setStyle("-fx-font-size: 42px;");
        p.getChildren().addAll(outer, inner, sym);
        return p;
    }

    // This makes each feature row on the left panel.
    private static HBox featureRow(String emoji, String text) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(StyleUtil.badgeCircle(emoji, "#1E293B"), whiteLabel(text));
        return row;
    }

    private static Label whiteLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: rgba(255,255,255,0.84); -fx-font-size: 13px;");
        return l;
    }

    private static Label tipPill(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-background-color: rgba(79,140,255,0.14); -fx-text-fill: #DCE8FF; "
                + "-fx-background-radius: 999; -fx-padding: 8 12 8 12; -fx-font-size: 11px;");
        return l;
    }
}