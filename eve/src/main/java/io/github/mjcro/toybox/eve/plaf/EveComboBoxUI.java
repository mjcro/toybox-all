package io.github.mjcro.toybox.eve.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Eve UI delegate for {@link javax.swing.JComboBox}.
 *
 * <p>Flat field-like body framed by {@link TextFrameBorder}, flat chevron arrow button,
 * compact {@link TextComboRenderer} rows and a popup framed by {@link EveBorders#popup()}.
 * The current value is never painted with the selection highlight; focus is signaled by the frame.
 */
public class EveComboBoxUI extends BasicComboBoxUI {
    private final MouseListener rollover = new RolloverListener();

    /**
     * Creates the delegate; prefer {@link #createUI(javax.swing.JComponent)}.
     */
    public EveComboBoxUI() {
    }

    /**
     * Creates the UI delegate; called reflectively by {@link javax.swing.UIManager}.
     *
     * @param c component to create the delegate for, not null.
     * @return new non-null delegate.
     */
    public static ComponentUI createUI(JComponent c) {
        return new EveComboBoxUI();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        comboBox.addMouseListener(rollover);
    }

    @Override
    protected void uninstallListeners() {
        comboBox.removeMouseListener(rollover);
        comboBox.putClientProperty(TextFrameBorder.ROLLOVER, null);
        super.uninstallListeners();
    }

    @Override
    protected JButton createArrowButton() {
        final JButton button = new TextArrowButton(SwingConstants.SOUTH, 10, 18, false);
        button.setName("ComboBox.arrowButton");
        button.addMouseListener(rollover);
        return button;
    }

    @Override
    protected ComboPopup createPopup() {
        return new Popup(comboBox);
    }

    @Override
    protected ListCellRenderer<Object> createRenderer() {
        return new TextComboRenderer();
    }

    @Override
    protected ComboBoxEditor createEditor() {
        return new Editor();
    }

    @Override
    protected void configureEditor() {
        super.configureEditor();
        editor.addMouseListener(rollover);
    }

    @Override
    protected void unconfigureEditor() {
        editor.removeMouseListener(rollover);
        super.unconfigureEditor();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(background());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        hasFocus = comboBox.hasFocus();
        if (!comboBox.isEditable()) {
            paintCurrentValue(g, rectangleForCurrentValue(), false);
        }
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        g.setColor(background());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        final ListCellRenderer<Object> renderer = comboBox.getRenderer();
        final Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
        c.setFont(comboBox.getFont());
        c.setForeground(comboBox.isEnabled() ? comboBox.getForeground() : EvePalette.TEXT_DIM);
        c.setBackground(background());
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        if (padding != null) {
            x += padding.left;
            y += padding.top;
            w -= padding.left + padding.right;
            h -= padding.top + padding.bottom;
        }
        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, c instanceof JPanel);
    }

    private Color background() {
        if (comboBox.isEnabled()) {
            return comboBox.getBackground();
        }
        final @Nullable Color disabled = UIManager.getColor("ComboBox.disabledBackground");
        return disabled != null ? disabled : EvePalette.PANEL;
    }

    /**
     * Tracks the pointer over the combo box and its children to highlight the frame.
     */
    private final class RolloverListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            setRollover(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            final Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), comboBox);
            setRollover(comboBox.contains(p));
        }

        private void setRollover(boolean value) {
            comboBox.putClientProperty(TextFrameBorder.ROLLOVER, value);
            comboBox.repaint();
        }
    }

    /**
     * Popup with the Eve popup frame and a borderless list.
     */
    private static final class Popup extends BasicComboPopup {
        Popup(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();
            setBorder(EveBorders.popup());
        }
    }

    /**
     * Editor with a padded borderless text field that shows the combo body through.
     */
    private static final class Editor extends BasicComboBoxEditor implements UIResource {
        @Override
        protected JTextField createEditorComponent() {
            final JTextField field = new EditorField();
            field.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            field.setOpaque(false);
            return field;
        }
    }

    /**
     * Text field ignoring Look and Feel borders, so the padding survives UI updates.
     */
    private static final class EditorField extends JTextField {
        EditorField() {
            super("", 9);
        }

        @Override
        public void setText(@Nullable String text) {
            if (getText().equals(text)) {
                return;
            }
            super.setText(text);
        }

        @Override
        public void setBorder(@Nullable Border border) {
            if (!(border instanceof UIResource)) {
                super.setBorder(border);
            }
        }
    }
}
