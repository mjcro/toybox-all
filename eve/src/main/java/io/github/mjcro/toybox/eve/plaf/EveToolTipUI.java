package io.github.mjcro.toybox.eve.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * Eve UI delegate for {@link javax.swing.JToolTip}.
 *
 * <p>The look comes from defaults: raised background, small font and the
 * {@link ButtonsBorders#toolTip()} frame with an accent marker on the left.
 */
public class EveToolTipUI extends BasicToolTipUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveToolTipUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveToolTipUI();
    }
}
