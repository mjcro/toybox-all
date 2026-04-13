package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JButton;
import java.awt.event.ActionListener;

/**
 * Factory methods for creating pre-configured {@link JButton} instances
 * with standard ToyBox styling.
 */
public class ToyBoxButtons {
    /**
     * Creates a plain button with the given text.
     *
     * @param text The button label, or null for no label.
     * @return A new button.
     */
    private static @NonNull JButton create(@Nullable String text) {
        return new JButton(text);
    }

    /**
     * Creates a button configured with the given hints.
     *
     * @param hints Hints to apply to the button.
     * @return A new button.
     */
    @SafeVarargs
    public static @NonNull JButton create(@NonNull Hint<? super JButton>... hints) {
        final JButton button = create((String) null);
        Hint.applyAll(button, hints);
        return button;
    }

    /**
     * Creates a button with the given text and hints.
     *
     * @param text  The button label, or null for no label.
     * @param hints Hints to apply to the button.
     * @return A new button.
     */
    @SafeVarargs
    public static @NonNull JButton create(@Nullable String text, @NonNull Hint<? super JButton>... hints) {
        final JButton button = create(text);
        Hint.applyAll(button, hints);
        return button;
    }

    /**
     * Creates a button with the given text, action listener, and hints.
     *
     * @param text     The button label, or null for no label.
     * @param listener The action listener to attach.
     * @param hints    Hints to apply to the button.
     * @return A new button.
     */
    @SafeVarargs
    public static @NonNull JButton create(@Nullable String text, @NonNull ActionListener listener, @NonNull Hint<? super JButton>... hints) {
        final JButton button = create(text, hints);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a primary-styled button.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new primary button.
     */
    public static @NonNull JButton createPrimary(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_PRIMARY);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a success-styled button.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new success button.
     */
    public static @NonNull JButton createSuccess(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_SUCCESS);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a warning-styled button.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new warning button.
     */
    public static @NonNull JButton createWarning(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_WARNING);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a danger-styled button.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new danger button.
     */
    public static @NonNull JButton createDanger(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_DANGER);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a confirm button with a tick icon and success styling.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new confirm button.
     */
    public static @NonNull JButton createConfirm(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_SUCCESS, Hints.buttonIcon("fam://tick"));
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates an add button with a plus icon and success styling.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new add button.
     */
    public static @NonNull JButton createAdd(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_SUCCESS, Hints.buttonIcon("fam://add"));
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a delete button with a bin icon and danger styling.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new delete button.
     */
    public static @NonNull JButton createDelete(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_DANGER, Hints.buttonIcon("fam://bin_closed"));
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a cancel button with a cross icon and danger styling.
     *
     * @param text     The button label.
     * @param listener The action listener to attach.
     * @return A new cancel button.
     */
    public static @NonNull JButton createCancel(@NonNull String text, @NonNull ActionListener listener) {
        final JButton button = create(text, Hints.BUTTON_DANGER, Hints.buttonIcon("fam://cross"));
        button.addActionListener(listener);
        return button;
    }

    private ToyBoxButtons() {
    }
}
