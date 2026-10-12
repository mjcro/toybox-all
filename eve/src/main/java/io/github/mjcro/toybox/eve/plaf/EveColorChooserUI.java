package io.github.mjcro.toybox.eve.plaf;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeListener;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicColorChooserUI;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JColorChooser}.
 *
 * <p>Swatch grids of the default chooser panels hardcode a white background (visible
 * as grid lines) and black/white frames; this delegate restyles them dark, also for
 * panels installed later.
 */
public class EveColorChooserUI extends BasicColorChooserUI {
    private final PropertyChangeListener panelsListener = e -> restylePanels();

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveColorChooserUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveColorChooserUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        restylePanels();
        c.addPropertyChangeListener(JColorChooser.CHOOSER_PANELS_PROPERTY, panelsListener);
    }

    @Override
    public void uninstallUI(JComponent c) {
        c.removePropertyChangeListener(JColorChooser.CHOOSER_PANELS_PROPERTY, panelsListener);
        super.uninstallUI(c);
    }

    private void restylePanels() {
        for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
            restyle(panel);
        }
    }

    private static void restyle(Component c) {
        // Swatch panels are package-private JDK classes; match them by name.
        if (c.getClass().getSimpleName().endsWith("SwatchPanel")) {
            c.setBackground(EvePalette.BACKGROUND);
            // Holders wrap swatches in hardcoded black/white line borders.
            if (c.getParent() instanceof JComponent holder) {
                holder.setBorder(EveBorders.frame());
            }
        }
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                restyle(child);
            }
        }
    }
}
