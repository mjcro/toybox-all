package io.github.mjcro.toybox.swing.hint;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Collection of reusable UI hints for configuring Swing components.
 *
 * <p>Hints are functional decorators that apply visual or behavioral
 * modifications to Swing components in a composable manner.
 */
public class Hints {
    private static final @NonNull Logger log = LoggerFactory.getLogger(Hints.class);

    /** Padding hint with 1px on each side. */
    public static final @NonNull Hint<@NonNull JComponent>
            PADDING_NANO = c -> c.setBorder(new EmptyBorder(1, 1, 1, 1)),
            PADDING_MINI = c -> c.setBorder(new EmptyBorder(2, 2, 2, 2)),
            PADDING_NORMAL = c -> c.setBorder(new EmptyBorder(4, 4, 4, 4)),
            PADDING_EXTRA_LARGE = c -> c.setBorder(new EmptyBorder(15, 15, 15, 15)),
            BORDER_LOWERED_BEVEL = c -> c.setBorder(BorderFactory.createLoweredSoftBevelBorder()),
            COLOR_FG_SUCCESS = c -> c.setForeground(new Color(14, 109, 56)),
            COLOR_FG_ERROR = c -> c.setForeground(new Color(109, 14, 25)),
            BOLD = c -> c.setFont(c.getFont().deriveFont(Font.BOLD)),
            ITALIC = c -> c.setFont(c.getFont().deriveFont(Font.ITALIC)),
            FONT_SMALLER_1 = c -> c.setFont(c.getFont().deriveFont(c.getFont().getSize() - 1f)),
            FONT_SMALLER_2 = c -> c.setFont(c.getFont().deriveFont(c.getFont().getSize() - 2f)),
            RENDERER_JTREE_PADDING = c -> c.setBorder(new EmptyBorder(2, 2, 2, 2)),
            NONE = $ -> {
            };

    /** Center-aligned label hint. */
    public static final @NonNull Hint<@NonNull JLabel>
            CENTER = c -> {
        c.setHorizontalAlignment(SwingConstants.CENTER);
        c.setVerticalAlignment(SwingConstants.CENTER);
    },
            RIGHT = c -> c.setHorizontalAlignment(SwingConstants.RIGHT);

    /** FlatLaf style hints for component styling. */
    public static final @NonNull LaFStyle
            TEXT_MINI = new LaFStyle("mini"),
            TEXT_SMALL = new LaFStyle("small"),
            TEXT_LIGHT = new LaFStyle("light"),
            TEXT_SEMIBOLD = new LaFStyle("semibold"),
            TEXT_MONOSPACED = new LaFStyle("monospaced") {
                @Override
                public void apply(@Nullable JComponent component) {
                    super.apply(component);
                    // FlatLaf's "monospaced" style class is honored by FlatLaf's
                    // text UI delegates. Components using a non-FlatLaf UI
                    // (e.g. RTextArea) need an explicit monospaced font.
                    if (component instanceof JTextComponent
                            && !component.getUI().getClass().getName().startsWith("com.formdev.flatlaf.")) {
                        final Font cur = component.getFont();
                        final int size = cur != null ? cur.getSize() : 16;
                        Font themed = UIManager.getFont("monospaced.font");
                        Font font = themed != null
                                ? themed
                                : new Font(Font.MONOSPACED, Font.PLAIN, size);
                        component.setFont(font);
                    }
                }
            },
            TEXT_BIGGEST = new LaFStyle("h00"),
            BUTTON_PRIMARY = new LaFStyle("buttonPrimary"),
            BUTTON_SUCCESS = new LaFStyle("buttonSuccess"),
            BUTTON_WARNING = new LaFStyle("buttonWarning"),
            BUTTON_DANGER = new LaFStyle("buttonDanger"),
            TABLE_CELL_INDEX = new LaFStyle("tableCellIndex"),
            TABLE_CELL_INSTANT = new LaFStyle("tableCellInstant"),

    LAF_STUB = new LaFStyle("");

    /** Hint that makes a text component non-editable. */
    public static final @NonNull Hint<@NonNull JTextComponent>
            NOT_EDITABLE_TEXT = c -> c.setEditable(false);


    /**
     * Creates a hint that wraps the component with a titled border.
     *
     * @param title the border title
     * @return a hint applying a titled border
     */
    public static @NonNull Hint<@NonNull JComponent> titledBorder(@NonNull String title) {
        return c -> c.setBorder(new TitledBorder(new EtchedBorder(), title));
    }

    /**
     * Creates a hint that sets the preferred width of a component.
     *
     * @param width the preferred width in pixels
     * @return a hint applying the preferred width
     */
    public static @NonNull Hint<@NonNull JComponent> setPreferredWidth(int width) {
        return c -> c.setPreferredSize(new Dimension(width, c.getPreferredSize().height));
    }

    /**
     * Creates a hint that sets read-only text on a text component.
     *
     * @param s the text to set
     * @return a hint applying read-only text
     */
    public static @NonNull Hint<@NonNull JTextComponent> setReadOnlyText(@NonNull String s) {
        return c -> {
            c.setText(s);
            c.setEditable(false);
        };
    }

    /**
     * Creates a hint that sets the tooltip text.
     *
     * @param s the tooltip text
     * @return a hint applying the tooltip
     */
    public static @NonNull Hint<@NonNull JComponent> setToolTipText(@NonNull String s) {
        return c -> c.setToolTipText(s);
    }

    /**
     * Creates a hint that triggers a runnable when the Enter key is released.
     *
     * @param r the runnable to invoke on Enter key press
     * @return a hint adding an Enter key listener
     */
    public static @NonNull Hint<@NonNull JTextComponent> onEnterKeyPress(@Nullable Runnable r) {
        return c -> c.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final @NonNull KeyEvent e) {
            }

            @Override
            public void keyPressed(final @NonNull KeyEvent e) {
            }

            @Override
            public void keyReleased(final @NonNull KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && r != null) {
                    r.run();
                }
            }
        });
    }

    /**
     * Creates a hint that triggers an action listener when the Enter key is released.
     *
     * @param listener the action listener to invoke
     * @return a hint adding an Enter key listener
     */
    public static @NonNull Hint<@NonNull JTextComponent> onEnterKeyPress(@NonNull ActionListener listener) {
        return onEnterKeyPress(() -> listener.actionPerformed(null));
    }

    /**
     * Creates a hint that runs a given runnable when the button is clicked.
     *
     * @param r the runnable to invoke on action
     * @return a hint adding an action listener
     */
    public static @NonNull Hint<@NonNull AbstractButton> onAction(@NonNull Runnable r) {
        return c -> c.addActionListener(e -> r.run());
    }

    /**
     * Creates a hint that sets an icon on a label from the given URI.
     *
     * @param uri the icon URI
     * @return a hint applying the icon
     */
    public static @NonNull Hint<@NonNull JLabel> labelIcon(@NonNull String uri) {
        return c -> ToyBoxIcons.get(uri).ifPresent(c::setIcon);
    }

    /**
     * Creates a hint that sets an icon on a button from the given URI.
     *
     * @param uri the icon URI
     * @return a hint applying the icon
     */
    public static @NonNull Hint<@NonNull AbstractButton> buttonIcon(@NonNull String uri) {
        return c -> ToyBoxIcons.get(uri).ifPresent(c::setIcon);
    }

    /**
     * Creates a hint that sets tree cell renderer icons from the given URI.
     *
     * @param uri the icon URI
     * @return a hint applying tree icons
     */
    public static @NonNull Hint<@NonNull DefaultTreeCellRenderer> treeIcon(@NonNull String uri) {
        return c -> {
            ToyBoxIcons.get(uri).ifPresent(i -> {
                c.setOpenIcon(i);
                c.setClosedIcon(i);
                c.setLeafIcon(i);
            });
        };
    }

    /**
     * Creates a hint that sets a foreground color derived from the given value.
     *
     * @param value the value to derive a color from
     * @return a hint applying the derived foreground color
     */
    public static @NonNull Hint<@NonNull JLabel> derivedColor(@Nullable Object value) {
        Color color = Components.deriveColor(value);
        return l -> {
            if (color != null) {
                l.setForeground(color);
            }
        };
    }

    /**
     * A hint that applies a FlatLaf style class to a component.
     */
    public static class LaFStyle implements Hint<@NonNull JComponent> {
        private final @NonNull String value;

        /**
         * Creates a new FlatLaf style hint.
         *
         * @param value the FlatLaf style class name
         */
        public LaFStyle(@NonNull String value) {
            this.value = value;
        }

        /**
         * Returns the FlatLaf style class value.
         *
         * @return the style class name
         */
        public @NonNull String getValue() {
            return value;
        }

        @Override
        public void apply(@Nullable JComponent component) {
            if (component != null) {
                Object prev = component.getClientProperty("FlatLaf.styleClass");
                String style = getValue();
                if (prev instanceof String) {
                    style = prev + " " + style;
                }
                component.putClientProperty("FlatLaf.styleClass", style);
//                log.debug("Applied LaF style \"{}\" on {}", style, component.getClass().getSimpleName());
            }
        }

        @Override
        public @NonNull String toString() {
            return "[LaFStyle +" + value + "]";
        }
    }
}
