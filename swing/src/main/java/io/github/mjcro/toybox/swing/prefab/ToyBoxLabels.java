package io.github.mjcro.toybox.swing.prefab;

import io.github.mjcro.toybox.swing.hint.Hint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;

/**
 * Factory methods for creating pre-configured {@link JLabel} instances.
 */
public class ToyBoxLabels {
    /**
     * Creates a new empty label.
     *
     * @return A new label.
     */
    private static @NonNull JLabel create() {
        return new JLabel();
    }

    /**
     * Creates a label with the given text.
     *
     * @param text The label text.
     * @return A new label.
     */
    public static @NonNull JLabel create(@Nullable String text) {
        final JLabel label = create();
        label.setText(text);
        return label;
    }

    /**
     * Creates a label with the given text and tooltip.
     *
     * @param text    The label text.
     * @param tooltip The tooltip text.
     * @return A new label.
     */
    public static @NonNull JLabel create(@Nullable String text, @Nullable String tooltip) {
        final JLabel label = create();
        label.setText(text);
        label.setToolTipText(tooltip);
        return label;
    }

    /**
     * Creates a label configured with the given hints.
     *
     * @param hints Hints to apply to the label.
     * @return A new label.
     */
    @SafeVarargs
    public static @NonNull JLabel create(@NonNull Hint<? super JLabel>... hints) {
        final JLabel label = create();
        Hint.applyAll(label, hints);
        return label;
    }

    /**
     * Creates a label with the given text and hints.
     *
     * @param text  The label text.
     * @param hints Hints to apply to the label.
     * @return A new label.
     */
    @SafeVarargs
    public static @NonNull JLabel create(@Nullable String text, @NonNull Hint<? super JLabel>... hints) {
        final JLabel label = create(text);
        Hint.applyAll(label, hints);
        return label;
    }

    /**
     * Creates a shallow clone of the given label, copying text, opacity, and colors.
     *
     * @param l The label to clone.
     * @return A new label with copied properties.
     */
    public static @NonNull JLabel clone(@NonNull JLabel l) {
        final JLabel label = new JLabel();
        label.setText(l.getText());
        label.setOpaque(l.isOpaque());
        label.setForeground(l.getForeground());
        label.setBackground(l.getBackground());
        return label;
    }

    private ToyBoxLabels() {
    }
}
