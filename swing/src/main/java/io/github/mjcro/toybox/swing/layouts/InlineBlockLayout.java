package io.github.mjcro.toybox.swing.layouts;

import org.jspecify.annotations.NonNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A layout manager that arranges components in an inline-block fashion,
 * wrapping to the next row when the available width is exceeded.
 */
public class InlineBlockLayout implements LayoutManager {
    private final int hGap = 5;
    private final int vGap = 5;

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
            final Component[] components = parent.getComponents();
            final int parentWidth = parent.getWidth();
            final int contentWidth = parentWidth > 0
                    ? parentWidth - insets.left - insets.right
                    : naturalLineWidth(components);
            final int[] rows = rowHeights(components, contentWidth);
            int height = insets.top + insets.bottom;
            for (int i = 0; i < rows.length; i++) {
                height += rows[i];
                if (i > 0) {
                    height += vGap;
                }
            }
            return new Dimension(contentWidth + insets.left + insets.right, height);
        }
    }

    @Override
    public @NonNull Dimension minimumLayoutSize(@NonNull Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            int w = 0;
            int h = 0;
            for (final Component c : parent.getComponents()) {
                if (!c.isVisible()) {
                    continue;
                }
                final Dimension d = getDimensions(c);
                w = Math.max(w, d.width);
                h = Math.max(h, d.height);
            }
            return new Dimension(w + insets.left + insets.right, h + insets.top + insets.bottom);
        }
    }

    @Override
    public void layoutContainer(@NonNull Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            final int width = parent.getWidth() - insets.right - insets.left;

            final Component[] components = parent.getComponents();
            final int[] rows = rowHeights(components, width);

            int row = 0;
            int offsetX = insets.left;
            int offsetY = insets.top;
            boolean first = true;
            for (final Component c : components) {
                if (!c.isVisible()) {
                    continue;
                }
                final Dimension d = getDimensions(c);
                if (!first && offsetX + d.width - insets.left > width) {
                    offsetY += rows[row];
                    offsetY += vGap;
                    row++;
                    offsetX = insets.left;
                }

                if (d.height < rows[row]) {
                    final int delta = (rows[row] - d.height) / 2;
                    c.setBounds(offsetX, offsetY + delta, d.width, d.height);
                } else {
                    c.setBounds(offsetX, offsetY, d.width, d.height);
                }

                offsetX += hGap;
                offsetX += d.width;

                first = false;
            }
        }
    }

    private int naturalLineWidth(@NonNull Component @NonNull [] components) {
        int total = 0;
        boolean first = true;
        for (final Component c : components) {
            if (!c.isVisible()) {
                continue;
            }
            if (!first) {
                total += hGap;
            }
            total += getDimensions(c).width;
            first = false;
        }
        return total;
    }

    private int @NonNull [] rowHeights(@NonNull Component @NonNull [] components, int width) {
        int[] heights = new int[Math.max(1, components.length)];
        int row = 0;
        int left = 0;
        boolean first = true;
        for (final Component c : components) {
            if (!c.isVisible()) {
                continue;
            }
            final Dimension d = getDimensions(c);
            if (first) {
                left = d.width;
            } else if (left + hGap + d.width > width) {
                row++;
                left = d.width;
            } else {
                left += hGap + d.width;
            }
            heights[row] = Math.max(heights[row], d.height);
            first = false;
        }
        if (row + 1 < heights.length) {
            final int[] trimmed = new int[row + 1];
            System.arraycopy(heights, 0, trimmed, 0, row + 1);
            return trimmed;
        }
        return heights;
    }

    private @NonNull Dimension getDimensions(@NonNull Component c) {
        final Dimension p = c.getPreferredSize();
        final Dimension m = c.getMinimumSize();

        return new Dimension(Math.max(p.width, m.width), Math.max(p.height, m.height));
    }
}
