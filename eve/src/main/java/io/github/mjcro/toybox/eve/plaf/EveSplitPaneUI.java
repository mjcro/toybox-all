package io.github.mjcro.toybox.eve.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * Eve UI delegate for {@link javax.swing.JSplitPane}.
 *
 * <p>Frameless split pane with a slim flat divider, see {@link RangesSplitPaneDivider}.
 */
public class EveSplitPaneUI extends BasicSplitPaneUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveSplitPaneUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveSplitPaneUI();
    }

    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        return new RangesSplitPaneDivider(this);
    }
}
