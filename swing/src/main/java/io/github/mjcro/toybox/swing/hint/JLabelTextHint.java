package io.github.mjcro.toybox.swing.hint;

import io.github.mjcro.interfaces.strings.WithText;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;

/**
 * Combines a text value with a {@link Hint} applicable to {@link JLabel} components.
 */
public class JLabelTextHint implements WithText, WithHint<JLabel> {
    private final @NonNull String text;
    private final @Nullable Hint<? super JLabel> hint;

    /**
     * Creates a new label text hint.
     *
     * @param text display text, must not be {@code null}
     * @param hint hint to apply to the label, may be {@code null}
     */
    public JLabelTextHint(@NonNull String text, @Nullable Hint<? super JLabel> hint) {
        this.text = text;
        this.hint = hint;
    }

    @Override
    public @NonNull String getText() {
        return text;
    }

    @Override
    public @Nullable Hint<? super JLabel> getHint() {
        return hint;
    }
}
