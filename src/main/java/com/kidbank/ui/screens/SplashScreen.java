package com.kidbank.ui.screens;

import javafx.scene.Scene;

/**
 * Compatibility alias – delegates to WelcomeScreen.
 */
public class SplashScreen {
    public static Scene build() {
        return WelcomeScreen.build();
    }
}
