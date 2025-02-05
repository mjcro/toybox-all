package io.github.mjcro.toybox.swing.factories;

import io.github.mjcro.toybox.swing.Hint;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ButtonsFactory {
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

    private ButtonsFactory() {
    }
}
