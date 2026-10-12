package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Flat, non-focusable title bar button (minimize, maximize, close) of root pane
 * and internal frame title panes.
 *
 * <p>Paints only a hover/pressed background and its centered icon; it keeps a
 * {@link BasicButtonUI} regardless of the installed button delegate so that
 * title bars look the same whatever the button group renders.
 */
final class WindowsControlButton extends JButton {
    private static final int WIDTH = 22;
    private static final int HEIGHT = 18;
    private final boolean danger;

    /**
     * Creates the button.
     *
     * @param danger {@code true} for the close button: hover background becomes {@link EvePalette#DANGER}.
     */
    WindowsControlButton(boolean danger) {
        this.danger = danger;
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFocusable(false);
        setRequestFocusEnabled(false);
        setRolloverEnabled(true);
        setOpaque(false);
    }

    @Override
    public void updateUI() {
        setUI((ButtonUI) BasicButtonUI.createUI(this));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final ButtonModel model = getModel();
        if (model.isEnabled() && (model.isRollover() || model.isPressed())) {
            final Color fill;
            if (danger) {
                fill = model.isPressed() ? EvePalette.mix(EvePalette.DANGER, EvePalette.BACKGROUND, 0.3f) : EvePalette.DANGER;
            } else {
                fill = model.isPressed() ? EvePalette.ACCENT_DARK : EvePalette.RAISED_HOVER;
            }
            g.setColor(fill);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        final Icon icon = getIcon();
        if (icon != null) {
            icon.paintIcon(this, g, (getWidth() - icon.getIconWidth()) / 2, (getHeight() - icon.getIconHeight()) / 2);
        }
    }
}
