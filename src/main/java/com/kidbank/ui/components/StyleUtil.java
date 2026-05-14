package com.kidbank.ui.components;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Central palette, widget factory and animation helpers for PocketPal.
 * Upgraded: richer gradients, smooth hover animations, polished inputs.
 */
public final class StyleUtil {

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final String NAVY        = "#0D2137";
    public static final String NAVY_LIGHT  = "#162E47";
    public static final String BLUE        = "#1565C0";
    public static final String BLUE_HOVER  = "#0D47A1";
    public static final String BLUE_LIGHT  = "#E3F2FD";
    public static final String GREEN       = "#2E7D32";
    public static final String GREEN_HOVER = "#1B5E20";
    public static final String GREEN_LIGHT = "#E8F5E9";
    public static final String RED         = "#C62828";
    public static final String RED_HOVER   = "#8B0000";
    public static final String YELLOW      = "#F9A825";
    public static final String WHITE       = "#FFFFFF";
    public static final String LIGHT_GRAY  = "#F0F4F8";
    public static final String MED_GRAY    = "#B0BEC5";
    public static final String DARK_GRAY   = "#546E7A";
    public static final String TEXT_DARK   = "#1A2733";
    public static final String TEAL        = "#00695C";
    public static final String TEAL_HOVER  = "#004D40";
    public static final String PURPLE      = "#4527A0";
    public static final String PURPLE_HOVER= "#311B92";
    public static final String ORANGE      = "#E65100";

    // ── Card style ────────────────────────────────────────────────────────────
    public static final String STYLE_CARD =
        "-fx-background-color: white; -fx-background-radius: 14; " +
        "-fx-effect: dropshadow(gaussian, rgba(13,33,55,0.10), 16, 0, 0, 4); " +
        "-fx-padding: 20;";

    public static final String STYLE_CARD_HOVER =
        "-fx-background-color: white; -fx-background-radius: 14; " +
        "-fx-effect: dropshadow(gaussian, rgba(13,33,55,0.18), 22, 0, 0, 6); " +
        "-fx-padding: 20;";

    // ── Input style ───────────────────────────────────────────────────────────
    public static final String STYLE_INPUT =
        "-fx-background-color: #FAFBFC; -fx-background-radius: 9; " +
        "-fx-border-radius: 9; -fx-border-color: #CFD8DC; -fx-border-width: 1.5; " +
        "-fx-padding: 9 13 9 13; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";";

    public static final String STYLE_INPUT_FOCUS =
        "-fx-background-color: white; -fx-background-radius: 9; " +
        "-fx-border-radius: 9; -fx-border-color: " + BLUE + "; -fx-border-width: 2; " +
        "-fx-padding: 9 13 9 13; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";";

    private StyleUtil() {}

    // ── Buttons ───────────────────────────────────────────────────────────────

    public static Button primaryBtn(String text) {
        Button b = new Button(text);
        String base = "-fx-background-color: linear-gradient(to bottom, #1E76D3, " + BLUE + "); " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 24 10 24; " +
            "-fx-effect: dropshadow(gaussian, rgba(21,101,192,0.35), 8, 0, 0, 3);";
        String hover = "-fx-background-color: linear-gradient(to bottom, #1565C0, " + BLUE_HOVER + "); " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 24 10 24; " +
            "-fx-effect: dropshadow(gaussian, rgba(21,101,192,0.50), 12, 0, 0, 4);";
        b.setStyle(base); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        addPressEffect(b);
        return b;
    }

    public static Button successBtn(String text) {
        Button b = new Button(text);
        String base = "-fx-background-color: linear-gradient(to bottom, #388E3C, " + GREEN + "); " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 24 10 24; " +
            "-fx-effect: dropshadow(gaussian, rgba(46,125,50,0.35), 8, 0, 0, 3);";
        String hover = "-fx-background-color: linear-gradient(to bottom, #2E7D32, " + GREEN_HOVER + "); " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 24 10 24; " +
            "-fx-effect: dropshadow(gaussian, rgba(46,125,50,0.50), 12, 0, 0, 4);";
        b.setStyle(base); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        addPressEffect(b);
        return b;
    }

    public static Button dangerBtn(String text) {
        Button b = new Button(text);
        String base = "-fx-background-color: linear-gradient(to bottom, #D32F2F, " + RED + "); " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 24 10 24; " +
            "-fx-effect: dropshadow(gaussian, rgba(198,40,40,0.35), 8, 0, 0, 3);";
        String hover = "-fx-background-color: linear-gradient(to bottom, #C62828, " + RED_HOVER + "); " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 9; -fx-cursor: hand; -fx-padding: 10 24 10 24; " +
            "-fx-effect: dropshadow(gaussian, rgba(198,40,40,0.50), 12, 0, 0, 4);";
        b.setStyle(base); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        addPressEffect(b);
        return b;
    }

    public static Button secondaryBtn(String text) {
        Button b = new Button(text);
        String base = "-fx-background-color: white; -fx-text-fill: " + TEXT_DARK + "; " +
            "-fx-font-size: 12px; -fx-background-radius: 9; -fx-cursor: hand; " +
            "-fx-padding: 9 20 9 20; -fx-border-color: #CFD8DC; -fx-border-width: 1.5; " +
            "-fx-border-radius: 9;";
        String hover = "-fx-background-color: " + LIGHT_GRAY + "; -fx-text-fill: " + TEXT_DARK + "; " +
            "-fx-font-size: 12px; -fx-background-radius: 9; -fx-cursor: hand; " +
            "-fx-padding: 9 20 9 20; -fx-border-color: " + BLUE + "; -fx-border-width: 1.5; " +
            "-fx-border-radius: 9;";
        b.setStyle(base); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        return b;
    }

    public static Button linkBtn(String text) {
        Button b = new Button(text);
        String base = "-fx-background-color: transparent; -fx-text-fill: " + BLUE + "; " +
            "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 2 4 2;";
        String hover = "-fx-background-color: transparent; -fx-text-fill: " + BLUE_HOVER + "; " +
            "-fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true; -fx-padding: 4 2 4 2;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        return b;
    }

    /** Ghost button for dark backgrounds (e.g. header bar). */
    public static Button ghostBtn(String text) {
        Button b = new Button(text);
        String base = "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; " +
            "-fx-background-radius: 7; -fx-cursor: hand; -fx-font-size: 12px; " +
            "-fx-padding: 7 16 7 16; -fx-border-color: rgba(255,255,255,0.22); " +
            "-fx-border-width: 1; -fx-border-radius: 7;";
        String hover = "-fx-background-color: rgba(255,255,255,0.22); -fx-text-fill: white; " +
            "-fx-background-radius: 7; -fx-cursor: hand; -fx-font-size: 12px; " +
            "-fx-padding: 7 16 7 16; -fx-border-color: rgba(255,255,255,0.40); " +
            "-fx-border-width: 1; -fx-border-radius: 7;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(base));
        return b;
    }

    // ── Inputs ────────────────────────────────────────────────────────────────

    public static TextField textField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(STYLE_INPUT);
        tf.focusedProperty().addListener((obs, old, focused) ->
            tf.setStyle(focused ? STYLE_INPUT_FOCUS : STYLE_INPUT));
        return tf;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle(STYLE_INPUT);
        pf.focusedProperty().addListener((obs, old, focused) ->
            pf.setStyle(focused ? STYLE_INPUT_FOCUS : STYLE_INPUT));
        return pf;
    }

    // ── Cards ─────────────────────────────────────────────────────────────────

    public static VBox card() {
        VBox v = new VBox(10);
        v.setStyle(STYLE_CARD);
        return v;
    }

    /** Card that lifts on hover. */
    public static VBox hoverCard() {
        VBox v = new VBox(10);
        v.setStyle(STYLE_CARD);
        v.setOnMouseEntered(e -> v.setStyle(STYLE_CARD_HOVER));
        v.setOnMouseExited(e  -> v.setStyle(STYLE_CARD));
        return v;
    }

    // ── Stat card (coloured metric) ───────────────────────────────────────────

    public static VBox statCard(String label, String value, String fromColor, String toColor) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + fromColor + ", " + toColor + "); " +
                      "-fx-background-radius: 14; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 12, 0, 0, 4);");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.78); " +
                     "-fx-font-weight: bold; -fx-letter-spacing: 1px;");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        card.getChildren().addAll(lbl, val);
        return card;
    }

    // ── Labels ────────────────────────────────────────────────────────────────

    public static Label sectionHeader(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
        return l;
    }

    public static Label subtitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + DARK_GRAY + ";");
        return l;
    }

    public static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        return l;
    }

    public static Label errorLabel() {
        Label l = new Label();
        l.setStyle("-fx-text-fill: " + RED + "; -fx-font-size: 11px; " +
                   "-fx-background-color: #FFEBEE; -fx-background-radius: 7; " +
                   "-fx-padding: 7 12 7 12;");
        l.setWrapText(true);
        l.setVisible(false); l.setManaged(false);
        return l;
    }

    public static void showError(Label l, String msg) { l.setText(msg); l.setVisible(true); l.setManaged(true); }
    public static void clearError(Label l)            { l.setText(""); l.setVisible(false); l.setManaged(false); }

    // ── Separator ─────────────────────────────────────────────────────────────

    public static Separator separator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #E8ECEF; -fx-opacity: 0.9;");
        return sep;
    }

    // ── Currency label ────────────────────────────────────────────────────────

    public static Label currencyLabel(double amount, boolean large) {
        Label l = new Label(String.format("£%.2f", amount));
        String size  = large ? "30px" : "14px";
        String color = amount >= 0 ? GREEN : RED;
        l.setStyle("-fx-font-size: " + size + "; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        return l;
    }

    // ── Action tile (dashboard button) ────────────────────────────────────────

    /**
     * Polished action tile with gradient background, scale animation on hover.
     */
    public static Button actionTile(String emoji, String title, String sub,
                                    String fromColor, String toColor) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 6, 10, 6));

        Label e = new Label(emoji); e.setStyle("-fx-font-size: 28px;");
        Label t = new Label(title);
        t.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-text-alignment: center;");
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        t.setWrapText(true);
        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.75); -fx-text-alignment: center;");
        s.setWrapText(true); s.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        box.getChildren().addAll(e, t, s);

        Button btn = new Button(); btn.setGraphic(box);
        String base = "-fx-background-color: linear-gradient(to bottom right, " + fromColor + ", " + toColor + "); " +
                      "-fx-background-radius: 14; -fx-cursor: hand; " +
                      "-fx-pref-width: 210px; -fx-pref-height: 105px; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 4);";
        String hover = "-fx-background-color: linear-gradient(to bottom right, " + fromColor + ", " + toColor + "); " +
                       "-fx-background-radius: 14; -fx-cursor: hand; " +
                       "-fx-pref-width: 210px; -fx-pref-height: 105px; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.30), 16, 0, 0, 6);";
        btn.setStyle(base);

        ScaleTransition scaleUp   = new ScaleTransition(Duration.millis(130), btn);
        scaleUp.setToX(1.04); scaleUp.setToY(1.04);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(130), btn);
        scaleDown.setToX(1.0); scaleDown.setToY(1.0);

        btn.setOnMouseEntered(ev -> { btn.setStyle(hover); scaleUp.playFromStart(); });
        btn.setOnMouseExited(ev  -> { btn.setStyle(base);  scaleDown.playFromStart(); });
        addPressEffect(btn);
        return btn;
    }

    // ── Badge circle ──────────────────────────────────────────────────────────

    public static StackPane badgeCircle(String emoji, String hexColor) {
        StackPane sp = new StackPane();
        Circle c = new Circle(24); c.setFill(Color.web(hexColor));
        Label l = new Label(emoji); l.setStyle("-fx-font-size: 18px;");
        sp.getChildren().addAll(c, l);
        return sp;
    }

    // ── Accent bar ────────────────────────────────────────────────────────────

    public static Region accentBar(String color) {
        Region bar = new Region(); bar.setPrefHeight(4); bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12 12 0 0;");
        return bar;
    }

    // ── Legacy hover (kept for compatibility) ─────────────────────────────────

    public static void addHoverEffect(Button btn, String normalHex, String hoverHex) {
        String base = btn.getStyle();
        btn.setOnMouseEntered(e -> btn.setStyle(base.replace(normalHex, hoverHex)));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
    }

    // ── Press scale animation ─────────────────────────────────────────────────

    private static void addPressEffect(Button btn) {
        ScaleTransition press   = new ScaleTransition(Duration.millis(80), btn);
        press.setToX(0.96); press.setToY(0.96);
        ScaleTransition release = new ScaleTransition(Duration.millis(80), btn);
        release.setToX(1.0); release.setToY(1.0);
        btn.setOnMousePressed(e  -> press.playFromStart());
        btn.setOnMouseReleased(e -> release.playFromStart());
    }
}
