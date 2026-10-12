package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EvePaint;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JToolBar}.
 *
 * <p>In rollover mode (the Eve default, {@code ToolBar.isRollover}) toolbar buttons are flat:
 * their own content area is switched off and this delegate paints hover, pressed and selected
 * fills behind them, while {@link MenusBorders#toolBarButton()} paints the matching frame.
 * The original button properties are restored when the button leaves the toolbar.
 */
public class EveToolBarUI extends BasicToolBarUI {
    private static final String FLATTENED = "Eve.toolBar.flattened";
    private final Border rolloverBorder = MenusBorders.toolBarButton();
    private final Border nonRolloverBorder = EveBorders.button();

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveToolBarUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveToolBarUI();
    }

    @Override
    protected Border getRolloverBorder(AbstractButton b) {
        return rolloverBorder;
    }

    @Override
    protected Border getNonRolloverBorder(AbstractButton b) {
        return nonRolloverBorder;
    }

    @Override
    protected void setBorderToRollover(Component c) {
        super.setBorderToRollover(c);
        if (c instanceof AbstractButton b && b.getBorder() == rolloverBorder
                && b.getClientProperty(FLATTENED) == null) {
            b.putClientProperty(FLATTENED, new boolean[]{b.isContentAreaFilled(), b.isOpaque()});
            b.setContentAreaFilled(false);
            b.setOpaque(false);
        }
    }

    @Override
    protected void setBorderToNonRollover(Component c) {
        restore(c);
        super.setBorderToNonRollover(c);
    }

    @Override
    protected void setBorderToNormal(Component c) {
        restore(c);
        super.setBorderToNormal(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        for (Component child : c.getComponents()) {
            if (child instanceof AbstractButton b && b.isVisible() && b.isEnabled()
                    && b.getClientProperty(FLATTENED) != null) {
                final @Nullable Color fill = fill(b.getModel());
                if (fill != null) {
                    EvePaint.fillCut(g, fill, b.getX(), b.getY(), b.getWidth(), b.getHeight(), EvePaint.DIAGONAL);
                }
            }
        }
    }

    private static @Nullable Color fill(ButtonModel model) {
        if (model.isPressed() && model.isArmed()) {
            return EvePalette.ACCENT_DARK;
        }
        if (model.isSelected()) {
            return EvePalette.SELECTION;
        }
        if (model.isRollover()) {
            return EvePalette.RAISED_HOVER;
        }
        return null;
    }

    private static void restore(Component c) {
        if (c instanceof AbstractButton b && b.getClientProperty(FLATTENED) instanceof boolean[] original) {
            b.putClientProperty(FLATTENED, null);
            b.setContentAreaFilled(original[0]);
            b.setOpaque(original[1]);
        }
    }
}
