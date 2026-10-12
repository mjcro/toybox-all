package io.github.mjcro.toybox.eve.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;

/**
 * Eve UI delegate for {@link javax.swing.JFormattedTextField}.
 *
 * <p>Repaints on focus change (the frame turns accent-colored while focused) and
 * paints the {@link TextSupport#PLACEHOLDER} text while empty and unfocused.
 */
public class EveFormattedTextFieldUI extends BasicFormattedTextFieldUI {
    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveFormattedTextFieldUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveFormattedTextFieldUI();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        TextSupport.install(getComponent());
    }

    @Override
    protected void uninstallListeners() {
        TextSupport.uninstall(getComponent());
        super.uninstallListeners();
    }

    @Override
    protected void paintSafely(Graphics g) {
        super.paintSafely(g);
        TextSupport.paintPlaceholder(g, getComponent());
    }
}
