package io.github.mjcro.toybox.swing.layouts;

import org.jspecify.annotations.NonNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A layout manager that arranges components in vertically stacked rows,
 * each spanning the full container width with a fixed gap between rows.
 */
public class RowsLayout implements LayoutManager {
    private final int gap = 3;

    @Override
    public void addLayoutComponent(@NonNull String name, @NonNull Component comp) {
    }

    @Override
    public void removeLayoutComponent(@NonNull Component comp) {
    }

    @Override
    public @NonNull Dimension preferredLayoutSize(@NonNull Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            final int left = insets.left;
            final int right = parent.getWidth() - insets.right;
            final int width = right - left;

            final Dimension dimension = new Dimension(width, 0);

            boolean first = true;
            for (final Component c : parent.getComponents()) {
                if (first) {
                    first = false;
                } else {
                    dimension.height += gap;
                }
                final Dimension d = getDimensions(c, width);
                dimension.height += d.height;
            }
            return dimension;
        }
    }

    @Override
    public @NonNull Dimension minimumLayoutSize(@NonNull Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(@NonNull Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            final int top = insets.top;
            final int left = insets.left;
            final int right = parent.getWidth() - insets.right;
            final int width = right - left;

            int offset = top;
            boolean first = true;
            for (final Component c : parent.getComponents()) {
                if (first) {
                    first = false;
                } else {
                    offset += gap;
                }
                final Dimension d = getDimensions(c, width);
                c.setSize(d);
                c.setBounds(left, offset, d.width, d.height);
                offset += d.height;
            }
        }
    }

    private @NonNull Dimension getDimensions(@NonNull Component c, int width) {
        final Dimension p = c.getPreferredSize();
        final Dimension m = c.getMinimumSize();

        return new Dimension(width, Math.max(p.height, m.height));
    }
}
