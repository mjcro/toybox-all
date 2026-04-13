package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.layouts.RowsLayout;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Component;

/**
 * A panel that stacks child components vertically in rows using
 * {@link RowsLayout}, with a configurable border.
 */
public class VerticalRowsPanel extends JPanel {
    /**
     * Creates a new panel with a default 10-pixel empty border.
     */
    public VerticalRowsPanel() {
        this(new EmptyBorder(10, 10, 10, 10));
    }

    /**
     * Creates a new panel with the given border.
     *
     * @param border the border to apply, or {@code null} for no border
     */
    public VerticalRowsPanel(@Nullable Border border) {
        setLayout(new RowsLayout());
        if (border != null) {
            setBorder(border);
        }
    }

    @Override
    public @NonNull Component add(final @NonNull Component comp) {
        return super.add(comp);
    }
}
