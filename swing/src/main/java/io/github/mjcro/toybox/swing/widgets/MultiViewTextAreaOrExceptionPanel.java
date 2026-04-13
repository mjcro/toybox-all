package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Multi-view panel that switches between a text area view, an empty view,
 * and an exception details view using a card layout.
 */
public class MultiViewTextAreaOrExceptionPanel extends MultiViewBasicPanel {
    /** Card identifier for the text area view. */
    public static final @NonNull String
            CARD_TEXT_AREA = "textArea";

    private final @NonNull JTextArea textArea = ToyBoxTextComponents.createJTextArea();

    /**
     * Creates a new panel with an empty text area.
     */
    public MultiViewTextAreaOrExceptionPanel() {
        add(new JScrollPane(textArea), CARD_TEXT_AREA);
    }

    /**
     * Creates a new panel and immediately displays the given text.
     *
     * @param cs the character sequence to display
     */
    public MultiViewTextAreaOrExceptionPanel(@NonNull CharSequence cs) {
        this();
        setViewText(cs);
    }

    /**
     * Returns the underlying text area component.
     *
     * @return the text area
     */
    public @NonNull JTextArea getTextArea() {
        return textArea;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textArea.setEnabled(enabled);
    }

    /**
     * Switches the view to the text area card with the given text,
     * or to the empty card if the text is {@code null}.
     *
     * @param cs the character sequence to display, or {@code null} to show the empty view
     */
    public void setViewText(@Nullable CharSequence cs) {
        if (cs == null) {
            setViewEmpty();
        } else {
            textArea.setText(cs.toString());
            setSelectedCard(CARD_TEXT_AREA);
        }
    }

    /**
     * Returns the current text from the text area.
     *
     * @return the text area content
     */
    public @NonNull String getViewText() {
        return textArea.getText();
    }
}
