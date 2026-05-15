package com.kidbank.ui.screens;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;


//Karusel qismi bu FinanceCarousel, unda bolalar va ota-onalar
// uchun moliyaviy maslahatlar va yangiliklar ko'rsatiladi.
//Angular loyihalarimda ham kop ishlatganman, javafxda ham
// shunga o'xshash tarzda amalga oshirdim


public class FinanceCarousel extends StackPane {

    private int index = 0;

    public FinanceCarousel(List<CarouselSlide> slides) {
        getStyleClass().add("surface");
        setPadding(new Insets(14));
        setMinHeight(220);
        setMaxWidth(Double.MAX_VALUE);

        if (slides.isEmpty()) return;

        Node[] slideNodes = slides.stream().map(this::buildSlide).toArray(Node[]::new);
        getChildren().add(slideNodes[0]);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            index = (index + 1) % slideNodes.length;
            switchTo(slideNodes[index]);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private Node buildSlide(CarouselSlide slide) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(18));
        box.setMinHeight(190);
        box.setStyle("-fx-background-color: linear-gradient(to bottom right, " + slide.fromColor + ", " + slide.toColor + ");"
                + "-fx-background-radius: 16;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 16, 0, 0, 6);");

        Label badge = new Label(slide.badge);
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white; "
                + "-fx-padding: 5 10 5 10; -fx-background-radius: 999; -fx-font-size: 11px; -fx-font-weight: 700;");

        Label title = new Label(slide.title);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: white;");
        title.setWrapText(true);

        Label subtitle = new Label(slide.subtitle);
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.82);");
        subtitle.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label footer = new Label(slide.footer);
        footer.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.88); -fx-font-weight: 700;");

        box.getChildren().addAll(badge, title, subtitle, spacer, footer);
        return box;
    }

    private void switchTo(Node next) {
        Node current = getChildren().isEmpty() ? null : getChildren().get(0);
        if (current == null) {
            getChildren().add(next);
            return;
        }

        FadeTransition out = new FadeTransition(Duration.millis(220), current);
        out.setToValue(0.0);
        out.setOnFinished(e -> {
            getChildren().setAll(next);
            next.setOpacity(0.0);
            FadeTransition in = new FadeTransition(Duration.millis(260), next);
            in.setToValue(1.0);
            in.play();
        });
        out.play();
    }

    public static class CarouselSlide {
        public final String badge;
        public final String title;
        public final String subtitle;
        public final String footer;
        public final String fromColor;
        public final String toColor;

        public CarouselSlide(String badge, String title, String subtitle, String footer,
                             String fromColor, String toColor) {
            this.badge = badge;
            this.title = title;
            this.subtitle = subtitle;
            this.footer = footer;
            this.fromColor = fromColor;
            this.toColor = toColor;
        }
    }
}