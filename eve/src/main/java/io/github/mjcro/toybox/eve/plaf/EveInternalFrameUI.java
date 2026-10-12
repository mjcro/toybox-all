package io.github.mjcro.toybox.eve.plaf;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * Eve UI delegate for {@link javax.swing.JInternalFrame}.
 *
 * <p>Uses {@link WindowsInternalFrameTitlePane} as the north pane; the chamfered
 * frame comes from the {@code InternalFrame.border} default ({@link WindowsFrameBorder}).
 */
public class EveInternalFrameUI extends BasicInternalFrameUI {
    /**
     * Creates the delegate for the given component.
     *
     * @param frame target component, not null.
     */
    public EveInternalFrameUI(JInternalFrame frame) {
        super(frame);
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveInternalFrameUI((JInternalFrame) c);
    }

    @Override
    protected JComponent createNorthPane(JInternalFrame w) {
        return new WindowsInternalFrameTitlePane(w);
    }
}
