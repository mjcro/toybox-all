package io.github.mjcro.toybox.eve.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * Eve UI delegate for {@link javax.swing.JPanel}.
 *
 * <p>Panels are flat; colors come from the {@code Panel.*} defaults.
 */
public class EvePanelUI extends BasicPanelUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EvePanelUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EvePanelUI();
    }
}
