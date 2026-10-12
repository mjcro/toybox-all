package io.github.mjcro.toybox.eve.plaf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

/**
 * Eve UI delegate for {@link javax.swing.JInternalFrame.JDesktopIcon}.
 *
 * <p>An iconified frame is shown as a compact chip of fixed width
 * ({@code DesktopIcon.width}) holding a {@link WindowsInternalFrameTitlePane}.
 */
public class EveDesktopIconUI extends BasicDesktopIconUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveDesktopIconUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveDesktopIconUI();
    }

    @Override
    protected void installComponents() {
        iconPane = new WindowsInternalFrameTitlePane(frame);
        desktopIcon.setLayout(new BorderLayout());
        desktopIcon.add(iconPane, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        final Insets i = c.getInsets();
        final int width = UIManager.getInt("DesktopIcon.width");
        return new Dimension(width > 0 ? width : 160, iconPane.getPreferredSize().height + i.top + i.bottom);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}
