package io.github.mjcro.toybox.swing.hint;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Slf4j
public class Hints {
    public static final Hint<JComponent>
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

    public static final Hint<JLabel>
            CENTER = c -> {
        c.setHorizontalAlignment(SwingConstants.CENTER);
        c.setVerticalAlignment(SwingConstants.CENTER);
    },
            RIGHT = c -> c.setHorizontalAlignment(SwingConstants.RIGHT);

    public static final LaFStyle
            TEXT_MINI = new LaFStyle("mini"),
            TEXT_SMALL = new LaFStyle("small"),
            TEXT_LIGHT = new LaFStyle("light"),
            TEXT_SEMIBOLD = new LaFStyle("semibold"),
            TEXT_MONOSPACED = new LaFStyle("monospaced"),
            TEXT_BIGGEST = new LaFStyle("h00"),
            BUTTON_PRIMARY = new LaFStyle("buttonPrimary"),
            BUTTON_SUCCESS = new LaFStyle("buttonSuccess"),
            TABLE_CELL_INDEX = new LaFStyle("tableCellIndex"),
            TABLE_CELL_INSTANT = new LaFStyle("tableCellInstant"),

    LAF_STUB = new LaFStyle("");

    public static Hint<JComponent> titledBorder(String title) {
        return c -> c.setBorder(new TitledBorder(new EtchedBorder(), title));
    }

    public static Hint<JComponent> setPreferredWidth(int width) {
        return c -> c.setPreferredSize(new Dimension(width, c.getPreferredSize().height));
    }

    public static Hint<JTextComponent> setReadOnlyText(String s) {
        return c -> {
            c.setText(s);
            c.setEditable(false);
        };
    }

    public static Hint<JComponent> setToolTipText(String s) {
        return c -> c.setToolTipText(s);
    }

    public static Hint<JTextComponent> onEnterKeyPress(Runnable r) {
        return c -> c.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
            }

            @Override
            public void keyPressed(final KeyEvent e) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && r != null) {
                    r.run();
                }
            }
        });
    }

    public static Hint<JTextComponent> onEnterKeyPress(ActionListener listener) {
        return onEnterKeyPress(() -> listener.actionPerformed(null));
    }

    public static Hint<AbstractButton> onAction(Runnable r) {
        return c -> c.addActionListener(e -> r.run());
    }

    public static Hint<JLabel> labelIcon(String uri) {
        return c -> ToyBoxIcons.get(uri).ifPresent(c::setIcon);
    }

    public static Hint<DefaultTreeCellRenderer> treeIcon(String uri) {
        return c -> {
            ToyBoxIcons.get(uri).ifPresent(i -> {
                c.setOpenIcon(i);
                c.setClosedIcon(i);
                c.setLeafIcon(i);
            });
        };
    }

    public static Hint<JLabel> derivedColor(Object value) {
        Color color = Components.deriveColor(value);
        return l -> {
            if (color != null) {
                l.setForeground(color);
            }
        };
    }

    public static class LaFStyle implements Hint<JComponent> {
        private final String value;

        public LaFStyle(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public void apply(JComponent component) {
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
        public String toString() {
            return "[LaFStyle +" + value + "]";
        }
    }
}
