package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Factory methods for creating pre-configured Swing text components.
 */
public class ToyBoxTextComponents {
    /**
     * Creates a new empty text field.
     *
     * @return A new text field.
     */
    public static @NonNull JTextField createJTextField() {
        return new JTextField();
    }

    /**
     * Creates a text field with the given initial text.
     *
     * @param s The initial text, or null.
     * @return A new text field.
     */
    public static @NonNull JTextField createJTextField(@Nullable String s) {
        final JTextField field = createJTextField();
        field.setText(s);
        return field;
    }

    /**
     * Creates a text field configured with the given hints.
     *
     * @param hints Hints to apply to the text field.
     * @return A new text field.
     */
    @SafeVarargs
    public static @NonNull JTextField createJTextField(@NonNull Hint<? super JTextField>... hints) {
        final JTextField field = createJTextField();
        Hint.applyAll(field, hints);
        return field;
    }

    /**
     * Creates a text field with the given initial text and hints.
     *
     * @param s     The initial text, or null.
     * @param hints Hints to apply to the text field.
     * @return A new text field.
     */
    @SafeVarargs
    public static @NonNull JTextField createJTextField(@Nullable String s, @NonNull Hint<? super JTextField>... hints) {
        final JTextField field = createJTextField(hints);
        field.setText(s);
        return field;
    }

    /**
     * Creates a new empty text area.
     *
     * @return A new text area.
     */
    public static @NonNull JTextArea createJTextArea() {
        return new JTextArea();
    }

    /**
     * Creates a text area with the given initial text.
     *
     * @param s The initial text, or null.
     * @return A new text area.
     */
    public static @NonNull JTextArea createJTextArea(@Nullable String s) {
        final JTextArea area = createJTextArea();
        area.setText(s);
        return area;
    }

    /**
     * Creates a text area configured with the given hints.
     *
     * @param hints Hints to apply to the text area.
     * @return A new text area.
     */
    @SafeVarargs
    public static @NonNull JTextArea createJTextArea(@NonNull Hint<? super JTextArea>... hints) {
        final JTextArea area = createJTextArea();
        Hint.applyAll(area, hints);
        return area;
    }

    /**
     * Creates a text area with the given initial text and hints.
     *
     * @param s     The initial text, or null.
     * @param hints Hints to apply to the text area.
     * @return A new text area.
     */
    @SafeVarargs
    public static @NonNull JTextArea createJTextArea(@Nullable String s, @NonNull Hint<? super JTextArea>... hints) {
        final JTextArea area = createJTextArea(hints);
        area.setText(s);
        return area;
    }

    /**
     * Creates a monospaced text area.
     *
     * @return A new monospaced text area.
     */
    public static @NonNull JTextArea createJTextAreaMonospaced() {
        return createJTextArea(Hints.TEXT_MONOSPACED);
    }
}
