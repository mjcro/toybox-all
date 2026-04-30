package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;
import org.fife.ui.rtextarea.RTextArea;
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
    public static @NonNull JTextField createTextField() {
        return new JTextField();
    }

    /**
     * Creates a text field with the given initial text.
     *
     * @param s The initial text, or null.
     * @return A new text field.
     */
    public static @NonNull JTextField createTextField(@Nullable String s) {
        final JTextField field = createTextField();
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
    public static @NonNull JTextField createTextField(@NonNull Hint<? super JTextField>... hints) {
        final JTextField field = createTextField();
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
    public static @NonNull JTextField createTextField(@Nullable String s, @NonNull Hint<? super JTextField>... hints) {
        final JTextField field = createTextField(hints);
        field.setText(s);
        return field;
    }

    /**
     * Creates a new empty text area.
     *
     * @return A new text area.
     */
    public static @NonNull JTextArea createTextArea() {
        return new JTextArea();
    }

    /**
     * Creates a text area with the given initial text.
     *
     * @param s The initial text, or null.
     * @return A new text area.
     */
    public static @NonNull JTextArea createTextArea(@Nullable String s) {
        final JTextArea area = createTextArea();
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
    public static @NonNull JTextArea createTextArea(@NonNull Hint<? super JTextArea>... hints) {
        final JTextArea area = createTextArea();
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
    public static @NonNull JTextArea createTextArea(@Nullable String s, @NonNull Hint<? super JTextArea>... hints) {
        final JTextArea area = createTextArea(hints);
        area.setText(s);
        return area;
    }

    /**
     * Creates a monospaced text area.
     *
     * @return A new monospaced text area.
     */
    public static @NonNull JTextArea createTextAreaMonospaced() {
        return createTextArea(Hints.TEXT_MONOSPACED);
    }

    /**
     * Creates a text area optimized for displaying large text content
     * (in the megabyte range).
     * <p>
     * Backed by {@link RTextArea} from RSyntaxTextArea, which uses a faster
     * document model and rendering pipeline than the standard {@link JTextArea}.
     * No syntax highlighting is enabled.
     *
     * @return A new big-text-capable text area.
     */
    public static @NonNull RTextArea createBigTextArea() {
        final RTextArea area = new RTextArea();
        area.setHighlightCurrentLine(false);
        return area;
    }

    /**
     * Creates a big-text-capable text area with the given initial text.
     *
     * @param s The initial text, or null.
     * @return A new big-text-capable text area.
     */
    public static @NonNull RTextArea createBigTextArea(@Nullable String s) {
        final RTextArea area = createBigTextArea();
        area.setText(s);
        return area;
    }

    /**
     * Creates a big-text-capable text area configured with the given hints.
     *
     * @param hints Hints to apply to the text area.
     * @return A new big-text-capable text area.
     */
    @SafeVarargs
    public static @NonNull RTextArea createBigTextArea(@NonNull Hint<? super RTextArea>... hints) {
        final RTextArea area = createBigTextArea();
        Hint.applyAll(area, hints);
        return area;
    }

    /**
     * Creates a big-text-capable text area with the given initial text and hints.
     *
     * @param s     The initial text, or null.
     * @param hints Hints to apply to the text area.
     * @return A new big-text-capable text area.
     */
    @SafeVarargs
    public static @NonNull RTextArea createBigTextArea(@Nullable String s, @NonNull Hint<? super RTextArea>... hints) {
        final RTextArea area = createBigTextArea(hints);
        area.setText(s);
        return area;
    }
}
