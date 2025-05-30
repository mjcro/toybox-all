package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;

import javax.swing.*;

public class ToyBoxLabels {
    private static JLabel create() {
        return new JLabel();
    }

    public static JLabel create(String text) {
        JLabel label = create();
        label.setText(text);
        return label;
    }

    public static JLabel create(String text, String tooltip) {
        JLabel label = create();
        label.setText(text);
        label.setToolTipText(tooltip);
        return label;
    }

    @SafeVarargs
    public static JLabel create(Hint<? super JLabel>... hints) {
        JLabel label = create();
        Hint.applyAll(label, hints);
        return label;
    }

    @SafeVarargs
    public static JLabel create(String text, Hint<? super JLabel>... hints) {
        JLabel label = create(text);
        Hint.applyAll(label, hints);
        return label;
    }

    public static JLabel clone(JLabel l) {
        JLabel label = new JLabel();
        label.setText(l.getText());
        label.setOpaque(l.isOpaque());
        label.setForeground(l.getForeground());
        label.setBackground(l.getBackground());
        return label;
    }

    private ToyBoxLabels() {
    }
}
