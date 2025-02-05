package io.github.mjcro.toybox.swing;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ToyboxLaF {
    public static void initialize(boolean darkMode) {
        // Installing look and feel
        FlatLightLaf.registerCustomDefaultsSource("toybox.laf.themes");
        FlatLaf.setup(new FlatLightLaf());
        UIManager.put("TabbedPane.tabLayoutPolicy", "scroll");
    }

    private ToyboxLaF() {
    }
}
