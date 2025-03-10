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

    public static JButton createWarning(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_WARNING);
        button.addActionListener(listener);
        return button;
    }

    public static JButton createDanger(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_DANGER);
        button.addActionListener(listener);
        return button;
    }

    public static JButton createConfirm(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_SUCCESS, Hints.buttonIcon("fam://tick"));
        button.addActionListener(listener);
        return button;
    }

    public static JButton createAdd(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_SUCCESS, Hints.buttonIcon("fam://add"));
        button.addActionListener(listener);
        return button;
    }

    public static JButton createDelete(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_DANGER, Hints.buttonIcon("fam://bin_closed"));
        button.addActionListener(listener);
        return button;
    }

    public static JButton createCancel(String text, ActionListener listener) {
        JButton button = create(text, Hints.BUTTON_DANGER, Hints.buttonIcon("fam://cross"));
        button.addActionListener(listener);
        return button;
    }

    private ToyBoxButtons() {
    }
}
