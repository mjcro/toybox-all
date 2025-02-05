package io.github.mjcro.toybox.swing.factories;

import io.github.mjcro.toybox.swing.Hint;

import javax.swing.*;

public class LabelsFactory {
    private static JLabel create() {
        return new JLabel();
    }

    @SafeVarargs
    public static JLabel create(Hint<? super JLabel>... hints) {
        JLabel label = create();
        Hint.applyAll(label, hints);
        return label;
    }

    @SafeVarargs
    public static JLabel create(String text, Hint<? super JLabel>... hints) {
        JLabel label = create();
        label.setText(text);
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

    private LabelsFactory() {
    }
}
