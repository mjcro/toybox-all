package io.github.mjcro.toybox.eve.plaf;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JToolBar.Separator}: 1px line along the separator's long axis.
 *
 * <p>The default size follows the current orientation, which {@link javax.swing.JToolBar} assigns only
 * after the delegate is installed; sizes set explicitly by application code are kept as is.
 */
public class EveToolBarSeparatorUI extends BasicToolBarSeparatorUI {
    private static final int INSET = 3;
    private static final int THICKNESS = 9;
    private static final int LENGTH = 18;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveToolBarSeparatorUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveToolBarSeparatorUI();
    }

    @Override
    protected void installDefaults(JSeparator s) {
        final Dimension size = ((JToolBar.Separator) s).getSeparatorSize();
        if (size == null || size instanceof UIResource) {
            ((JToolBar.Separator) s).setSeparatorSize(new DimensionUIResource(THICKNESS, LENGTH));
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        final Dimension size = ((JToolBar.Separator) c).getSeparatorSize();
        if (size != null && !(size instanceof UIResource)) {
            return size.getSize();
        }
        if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL) {
            return new Dimension(THICKNESS, LENGTH);
        }
        return new Dimension(LENGTH, THICKNESS);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(EvePalette.BORDER);
        final int w = c.getWidth();
        final int h = c.getHeight();
        if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL) {
            g.fillRect(w / 2, INSET, 1, h - INSET * 2);
        } else {
            g.fillRect(INSET, h / 2, w - INSET * 2, 1);
        }
    }
}
