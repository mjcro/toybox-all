package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;

import javax.swing.*;

public class ToyBoxTextComponents {
    public static JTextField createJTextField() {
        return new JTextField();
    }

    public static JTextField createJTextField(String s) {
        JTextField field = createJTextField();
        field.setText(s);
        return field;
    }


    @SafeVarargs
    public static JTextField createJTextField(Hint<? super JTextField>... hints) {
        JTextField field = createJTextField();
        Hint.applyAll(field, hints);
        return field;
    }

    @SafeVarargs
    public static JTextField createJTextField(String s, Hint<? super JTextField>... hints) {
        JTextField field = createJTextField(hints);
        field.setText(s);
        return field;
    }

    public static JTextArea createJTextArea() {
        return new JTextArea();
    }

    public static JTextArea createJTextArea(String s) {
        JTextArea area = createJTextArea();
        area.setText(s);
        return area;
    }

    @SafeVarargs
    public static JTextArea createJTextArea(Hint<? super JTextArea>... hints) {
        JTextArea area = createJTextArea();
        Hint.applyAll(area, hints);
        return area;
    }

    @SafeVarargs
    public static JTextArea createJTextArea(String s, Hint<? super JTextArea>... hints) {
        JTextArea area = createJTextArea(hints);
        area.setText(s);
        return area;
    }

    public static JTextArea createJTextAreaMonospaced() {
        return createJTextArea(Hints.TEXT_MONOSPACED);
    }
}
