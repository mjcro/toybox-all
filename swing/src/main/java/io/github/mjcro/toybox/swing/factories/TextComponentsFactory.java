package io.github.mjcro.toybox.swing.factories;

import io.github.mjcro.toybox.swing.Hint;

import javax.swing.*;

public class TextComponentsFactory {
    private static JTextField createJTextField() {
        return new JTextField();
    }

    @SafeVarargs
    public static JTextField createJTextField(Hint<? super JTextField>... hints) {
        JTextField label = createJTextField();
        Hint.applyAll(label, hints);
        return label;
    }
}
