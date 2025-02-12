package io.github.mjcro.toybox.swing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Slf4j
public class Styles {
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

    public static final LafStyle
            TEXT_MINI = new LafStyle("mini"),
            TEXT_SMALL = new LafStyle("small"),
            TEXT_LIGHT = new LafStyle("light"),
            TEXT_SEMIBOLD = new LafStyle("semibold"),
            TEXT_MONOSPACED = new LafStyle("monospaced"),
            TEXT_BIGGEST = new LafStyle("h00"),
            BUTTON_PRIMARY = new LafStyle("buttonPrimary"),
            BUTTON_SUCCESS = new LafStyle("buttonSuccess"),
            TABLE_CELL_INDEX = new LafStyle("tableCellIndex"),
            TABLE_CELL_INSTANT = new LafStyle("tableCellInstant"),

    LAF_STUB = new LafStyle("");

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

    public static Hint<JLabel> derivedColor(Object value) {
        Color color = Components.deriveColor(value);
        return l -> {
            if (color != null) {
                l.setForeground(color);
            }
        };
    }

    @RequiredArgsConstructor
    public static class LafStyle implements Hint<JComponent> {
        @Getter
        private final String value;

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
