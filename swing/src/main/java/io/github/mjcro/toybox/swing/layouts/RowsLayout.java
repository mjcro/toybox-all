package io.github.mjcro.toybox.swing.layouts;

import java.awt.*;

public class RowsLayout implements LayoutManager {
    private final int gap = 3;

    @Override
    public void addLayoutComponent(final String name, final Component comp) {
    }

    @Override
    public void removeLayoutComponent(final Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int left = insets.left;
            int right = parent.getWidth() - insets.right;
            int width = right - left;

            Dimension dimension = new Dimension(width, 0);

            boolean first = true;
            for (Component c : parent.getComponents()) {
                if (first) {
                    first = false;
                } else {
                    dimension.height += gap;
                }
                Dimension d = getDimensions(c, width);
                dimension.height += d.height;
            }
            return dimension;
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
            int top = insets.top;
            int left = insets.left;
            int right = parent.getWidth() - insets.right;
            int width = right - left;

            int offset = top;
            boolean first = true;
            for (Component c : parent.getComponents()) {
                if (first) {
                    first = false;
                } else {
                    offset += gap;
                }
                Dimension d = getDimensions(c, width);
                c.setSize(d);
                c.setBounds(left, offset, d.width, d.height);
                offset += d.height;
            }
        }
    }

    private Dimension getDimensions(Component c, int width) {
        Dimension p = c.getPreferredSize();
        Dimension m = c.getMinimumSize();

        return new Dimension(width, Math.max(p.height, m.height));
    }
}
