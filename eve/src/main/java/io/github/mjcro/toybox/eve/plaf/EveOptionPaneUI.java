package io.github.mjcro.toybox.eve.plaf;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

/**
 * Eve UI delegate for {@link javax.swing.JOptionPane}.
 *
 * <p>Icons, paddings and button sizing come from {@code OptionPane.*} defaults; this
 * delegate keeps the icon and message anchored to the top when the pane is larger
 * than its preferred size (e.g. when embedded into a layout instead of a dialog).
 */
public class EveOptionPaneUI extends BasicOptionPaneUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveOptionPaneUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveOptionPaneUI();
    }

    @Override
    protected Container createMessageArea() {
        final JPanel anchor = new JPanel(new BorderLayout());
        anchor.setOpaque(false);
        anchor.add(super.createMessageArea(), BorderLayout.NORTH);
        return anchor;
    }
}
