package io.github.mjcro.toybox.swing.prefab;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Static utility for Java Look and Feel.
 */
public class ToyBoxLaF {
    /**
     * Initializes standard ToyBox Look and Feel (LaF).
     *
     * @param darkMode True if dark mode should be used, false otherwise.
     */
    public static void initialize(boolean darkMode) {
        // Installing look and feel
        FlatLightLaf.registerCustomDefaultsSource("toybox.laf.themes");
        if (darkMode) {
            FlatLaf.setup(new FlatDarkLaf());
        } else {
            FlatLaf.setup(new FlatLightLaf());
        }
        UIManager.put("TabbedPane.tabLayoutPolicy", "scroll");
    }

    private ToyBoxLaF() {
    }
}
