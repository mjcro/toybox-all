package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;

import javax.swing.*;

public class ToyBoxTextComponents {
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
