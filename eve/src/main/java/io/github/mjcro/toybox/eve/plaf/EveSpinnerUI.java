package io.github.mjcro.toybox.eve.plaf;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JSpinner}.
 *
 * <p>Field-like body framed by {@link TextFrameBorder} with compact stacked flat chevron
 * buttons. The editor is transparent and borderless, so the spinner body shows through;
 * the frame turns accent-colored while the editor text field owns focus.
 */
public class EveSpinnerUI extends BasicSpinnerUI {
    private final FocusListener focusRepaint = new FocusRepaint();

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveSpinnerUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveSpinnerUI();
    }

    @Override
    public void uninstallUI(JComponent c) {
        unconfigureEditor(((JSpinner) c).getEditor());
        super.uninstallUI(c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.isEnabled() ? c.getBackground() : EvePalette.PANEL);
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        paint(g, c);
    }

    @Override
    protected Component createNextButton() {
        final TextArrowButton button = new TextArrowButton(SwingConstants.NORTH, 8, 15, true);
        button.setName("Spinner.nextButton");
        installNextButtonListeners(button);
        return button;
    }

    @Override
    protected Component createPreviousButton() {
        final TextArrowButton button = new TextArrowButton(SwingConstants.SOUTH, 8, 15, false);
        button.setName("Spinner.previousButton");
        installPreviousButtonListeners(button);
        return button;
    }

    @Override
    protected JComponent createEditor() {
        final JComponent editor = super.createEditor();
        configureEditor(editor);
        return editor;
    }

    @Override
    protected void replaceEditor(JComponent oldEditor, JComponent newEditor) {
        unconfigureEditor(oldEditor);
        super.replaceEditor(oldEditor, newEditor);
        configureEditor(newEditor);
    }

    private void configureEditor(JComponent editor) {
        editor.setOpaque(false);
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setOpaque(false);
            defaultEditor.getTextField().addFocusListener(focusRepaint);
        }
    }

    private void unconfigureEditor(JComponent editor) {
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().removeFocusListener(focusRepaint);
        }
    }

    /**
     * Repaints the spinner frame when its editor gains or loses focus.
     */
    private final class FocusRepaint implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            spinner.repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            spinner.repaint();
        }
    }
}
