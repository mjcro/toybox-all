package io.github.mjcro.toybox.swing.widgets.panels;

import io.github.mjcro.toybox.swing.layouts.RowsLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VerticalRowsPanel extends JPanel {
    public VerticalRowsPanel() {
        this(new EmptyBorder(10, 10, 10, 10));
    }

    public VerticalRowsPanel(Border border) {
        setLayout(new RowsLayout());
        if (border != null) {
            setBorder(border);
        }
    }

    @Override
    public Component add(final Component comp) {
        return super.add(comp);
    }
}
