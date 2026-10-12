package io.github.mjcro.toybox.eve.plaf;

import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

/**
 * Eve UI delegate for {@link javax.swing.JEditorPane}.
 *
 * <p>Unless the application set {@link JEditorPane#HONOR_DISPLAY_PROPERTIES} itself, the
 * delegate enables it, so HTML and styled content use the Eve font and text color
 * instead of black text on the dark background. HTML links get the accent color.
 */
public class EveEditorPaneUI extends BasicEditorPaneUI {
    private static final String LINK_RULE = "a { color: #4fc3d9; }";

    private boolean honorInstalled = false;

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveEditorPaneUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveEditorPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        if (c.getClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES) == null) {
            c.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            honorInstalled = true;
        }
        super.installUI(c);
        styleLinks(((JEditorPane) c).getDocument());
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        if (honorInstalled) {
            c.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, null);
            honorInstalled = false;
        }
    }

    @Override
    protected void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if ("document".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Document doc) {
            styleLinks(doc);
        }
    }

    private static void styleLinks(Document doc) {
        if (doc instanceof HTMLDocument html) {
            html.getStyleSheet().addRule(LINK_RULE);
        }
    }
}
