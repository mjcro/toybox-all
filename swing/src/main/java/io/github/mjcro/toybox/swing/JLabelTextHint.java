package io.github.mjcro.toybox.swing;

import io.github.mjcro.interfaces.strings.WithText;

import javax.swing.*;

public class JLabelTextHint implements WithText, WithHint<JLabel> {
    private final String text;
    private final Hint<? super JLabel> hint;

    public JLabelTextHint(String text, Hint<? super JLabel> hint) {
        this.text = text;
        this.hint = hint;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Hint<? super JLabel> getHint() {
        return hint;
    }
}
