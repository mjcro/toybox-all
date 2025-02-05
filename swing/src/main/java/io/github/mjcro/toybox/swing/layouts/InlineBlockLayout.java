package io.github.mjcro.toybox.swing.layouts;

import java.awt.*;

public class InlineBlockLayout implements LayoutManager {
    private final int hGap = 5;
    private final int vGap = 5;

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int parentWidth = parent.getWidth();
            parentWidth = parentWidth == 0 ? Short.MAX_VALUE : parentWidth;
            int width = parentWidth - insets.left - insets.right;
            int height = insets.top + insets.bottom;
            int[] rows = rowHeights(parent.getComponents(), width);
            for (int i = 0; i < rows.length; i++) {
                height += rows[i];
                if (i > 0) {
                    height += vGap;
                }
            }
            return new Dimension(width, height);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = parent.getWidth() - insets.right - insets.left;

            Component[] components = parent.getComponents();
            int[] rows = rowHeights(parent.getComponents(), width);

            int row = 0;
            int offsetX = insets.left;
            int offsetY = insets.top;
            boolean first = true;
            for (Component c : components) {
                Dimension d = getDimensions(c);
                int currentRow = row;
                if (!first) {
                    if (offsetX + d.width - insets.left > width) {
                        // Component is out of bounds
                        offsetY += rows[row];
                        offsetY += vGap;
                        row++;
                        offsetX = insets.left;
                        first = true;
                    }
                }

                c.setSize(d);
                if (d.height < rows[currentRow] - 1) {
                    int delta = (rows[currentRow] - d.height) / 2;
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

    private int[] rowHeights(Component[] components, int width) {
        int[] heights = new int[1];
        int row = 0;
        int left = 0;
        boolean first = true;
        for (Component c : components) {
            Dimension d = getDimensions(c);
            // Checking if component is out of bounds
            if (!first) {
                left += hGap;
                left += d.width;
                if (left > width) {
                    // Component is out of bounds
                    row++;
                    left = 0;
                    first = true;

                    // Scaling array
                    int[] larger = new int[heights.length + 1];
                    System.arraycopy(heights, 0, larger, 0, heights.length);
                    heights = larger;
                } else {
                    left -= hGap;
                    left -= d.width;
                }
            }

            if (!first) {
                left += hGap;
            }

            left += d.width;
            heights[row] = Math.max(heights[row], d.height);

            first = false;
        }
        return heights;
    }

    private Dimension getDimensions(Component c) {
        Dimension p = c.getPreferredSize();
        Dimension m = c.getMinimumSize();

        return new Dimension(Math.max(p.width, m.width), Math.max(p.height, m.height));
    }
}
