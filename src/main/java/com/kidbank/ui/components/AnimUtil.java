package com.kidbank.ui.components;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public final class AnimUtil {
    private AnimUtil() {}

    public static void fadeInUp(Node node, double fromY, int ms) {
        node.setOpacity(0);
        node.setTranslateY(fromY);

        FadeTransition fade = new FadeTransition(Duration.millis(ms), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition move = new TranslateTransition(Duration.millis(ms), node);
        move.setFromY(fromY);
        move.setToY(0);

        new ParallelTransition(fade, move).play();
    }

    public static void hoverScale(Node node) {
        ScaleTransition up = new ScaleTransition(Duration.millis(140), node);
        up.setToX(1.03);
        up.setToY(1.03);

        ScaleTransition down = new ScaleTransition(Duration.millis(140), node);
        down.setToX(1.0);
        down.setToY(1.0);

        node.setOnMouseEntered(e -> up.playFromStart());
        node.setOnMouseExited(e -> down.playFromStart());
    }
}