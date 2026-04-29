package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Abstract panel using a {@link CardLayout} to switch between an empty view,
 * a text label view, and an exception details view.
 */
public abstract class MultiViewBasicPanel extends JPanel {
    /** Card name for the empty placeholder view. */
    public static final @NonNull String CARD_EMPTY = "empty";
    /** Card name for the single-label view. */
    public static final @NonNull String CARD_LABEL = "label";
    /** Card name for the exception details view. */
    public static final @NonNull String CARD_EXCEPTION = "exception";

    private final @NonNull LabelOnlyJPanel labelOnlyJPanel = new LabelOnlyJPanel();
    private final @NonNull ExceptionDetailsJPanel exceptionDetailsJPanel = new ExceptionDetailsJPanel();

    /**
     * Creates a new multi-view panel with empty, label, and exception cards.
     */
    public MultiViewBasicPanel() {
        super(new CardLayout());

        add(new JPanel(), CARD_EMPTY);
        add(labelOnlyJPanel, CARD_LABEL);
        add(exceptionDetailsJPanel, CARD_EXCEPTION);
    }

    /**
     * Switches to the card with the given name.
     *
     * @param card the card identifier to show
     */
    public void setSelectedCard(@NonNull String card) {
        ((CardLayout) getLayout()).show(this, card);
    }

    /**
     * Switches to the empty placeholder view.
     */
    public void setViewEmpty() {
        setSelectedCard(CARD_EMPTY);
    }

    /**
     * Switches to the label view displaying the given text.
     *
     * @param value the text to display
     */
    public void setViewLabel(@Nullable String value) {
        labelOnlyJPanel.label.setText(value);
        setSelectedCard(CARD_LABEL);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        labelOnlyJPanel.setEnabled(enabled);
        exceptionDetailsJPanel.setEnabled(enabled);
    }

    /**
     * Switches to the exception view displaying the given throwable,
     * or to the empty view if {@code null}.
     *
     * @param t the throwable to display, or {@code null} to show empty
     */
    public void setViewException(@Nullable Throwable t) {
        if (t == null) {
            setViewEmpty();
        } else {
            exceptionDetailsJPanel.setException(t);
            setSelectedCard(CARD_EXCEPTION);
        }
    }

    /**
     * Simple panel containing a single centered label.
     */
    private static final class LabelOnlyJPanel extends JPanel {
        private final @NonNull JLabel label = ToyBoxLabels.create();

        LabelOnlyJPanel() {
            add(label);
        }
    }
}
