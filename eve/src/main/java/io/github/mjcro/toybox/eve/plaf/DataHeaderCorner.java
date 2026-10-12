package io.github.mjcro.toybox.eve.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.UIResource;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Filler for the scroll pane corner above the vertical scroll bar of a table,
 * continuing the table header strip to the right edge.
 */
public class DataHeaderCorner extends JComponent implements UIResource {
    /**
     * Creates the corner component.
     */
    public DataHeaderCorner() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(EvePalette.RAISED);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(EvePalette.BORDER_BRIGHT);
        g.fillRect(0, getHeight() - 1, getWidth(), 1);
    }
}
