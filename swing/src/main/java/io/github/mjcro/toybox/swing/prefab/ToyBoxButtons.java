package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ToyBoxButtons {
    private static JButton create(String text) {
        return new JButton(text);
    }

    @SafeVarargs
    public static JButton create(Hint<? super JButton>... hints) {
        JButton button = create((String) null);
        Hint.applyAll(button, hints);
        return button;
    }

    @SafeVarargs
    public static JButton create(String text, Hint<? super JButton>... hints) {
        JButton button = create(text);
        Hint.applyAll(button, hints);
        return button;
    }

    @SafeVarargs
    public static JButton create(String text, ActionListener listener, Hint<? super JButton>... hints) {
        JButton button = create(text, hints);
        button.addActionListener(listener);
        return button;
    }

    public static JButton createPrimary(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_PRIMARY);
        button.addActionListener(listener);
        return button;
    }

    public static JButton createSuccess(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_SUCCESS);
        button.addActionListener(listener);
        return button;
    }

    private ToyBoxButtons() {
    }
}
