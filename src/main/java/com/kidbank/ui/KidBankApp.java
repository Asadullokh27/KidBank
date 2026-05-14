package com.kidbank.ui;

import com.kidbank.ui.screens.WelcomeScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * PocketPal – JavaFX application entry point.
 * Launches the main window and shows the welcome screen.
 */
public class KidBankApp extends Application {

    /** Application window width. */
    public static final double APP_WIDTH  = 1100;

    /** Application window height. */
    public static final double APP_HEIGHT = 720;

    /** Shared primary stage for scene navigation. */
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("PocketPal – Smart Money for Kids");
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setWidth(APP_WIDTH);
        stage.setHeight(APP_HEIGHT);
        stage.setResizable(true);
        stage.setScene(WelcomeScreen.build());
        stage.show();
    }

    /**
     * Switches the current scene.
     *
     * @param scene the scene to display
     */
    public static void navigate(Scene scene) {
        primaryStage.setScene(scene);
    }

    /** Exposes the primary stage for dialogs that need an owner window. */
    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}
