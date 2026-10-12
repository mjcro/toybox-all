package io.github.mjcro.toybox.eve.plaf;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

/**
 * Eve UI delegate for {@link javax.swing.JFileChooser}.
 */
public class EveFileChooserUI extends MetalFileChooserUI {
    /**
     * Creates the delegate for the given chooser.
     *
     * @param chooser file chooser, not null.
     */
    public EveFileChooserUI(JFileChooser chooser) {
        super(chooser);
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveFileChooserUI((JFileChooser) c);
    }
}
