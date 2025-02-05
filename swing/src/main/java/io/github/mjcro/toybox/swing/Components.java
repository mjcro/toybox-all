package io.github.mjcro.toybox.swing;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Components {
    public static void show(Component component) {
        show(component, "Example", true);
    }

    public static void showLine(Component component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JPanel(), BorderLayout.CENTER);
        panel.add(component, BorderLayout.PAGE_START);
        show(panel);
    }

    public static void show(Component component, String title, boolean exitOnClose) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(
                exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE
        );
        frame.getContentPane().add(component);
        frame.pack();
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    public static Color hsv(float hue) {
        // TODO apply dark mode
        return Color.getHSBColor(hue, .8f, .5f);
    }

    public static Color deriveColor(Object value) {
        if (value instanceof Enum<?>) {
            Enum<?> e = (Enum<?>) value;
            return Components.hsv(0.09f - (e.ordinal() * 0.15f));
        }
        return null;
    }

    public static void onPressEnter(JTextField field, Runnable action) {
        Styles.onEnterKeyPress(action).apply(field);
    }

    public static void setEnabled(boolean enabled, JComponent... components) {
        for (JComponent c : components) {
            c.setEnabled(enabled);
        }
    }

    @SafeVarargs
    public static Runnable setBoolean(boolean value, Consumer<Boolean>... consumers) {
        for (Consumer<Boolean> consumer : consumers) {
            consumer.accept(value);
        }

        return () -> {
            for (Consumer<Boolean> consumer : consumers) {
                consumer.accept(!value);
            }
        };
    }

    public static JComponent padding(Component other) {
        return padding(other, false);
    }

    public static JComponent padding(Component other, boolean transparent) {
        JPanel padding = new JPanel();
        Styles.PADDING_NORMAL.apply(padding);
        padding.setLayout(new BorderLayout());
        padding.add(other);
        if (transparent) {
            padding.setOpaque(false);
        }
        return padding;
    }

    public static <T> T with(T t, Consumer<T> consumer) {
        if (t != null && consumer != null) {
            consumer.accept(t);
        }
        return t;
    }

    public static void setInheritedPopupRecursively(Component c) {
        if (c == null /*|| c instanceof JButton || c instanceof JComboBox<?>*/) {
            return;
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setInheritsPopupMenu(true);
        }
        if (c instanceof Container) {
            Container cont = (Container) c;
            Component[] nested = cont.getComponents();
            if (nested != null) {
                for (Component n : nested) {
                    setInheritedPopupRecursively(n);
                }
            }
        }
    }

    public static class Fonts {
        public static <T extends Component> T with(T in, Function<Font, Font> func) {
            return Components.with(in, t -> {
                if (func != null) {
                    in.setFont(func.apply(in.getFont()));
                }
            });
        }

        public static <T extends Component> T withSmaller(T in, int delta) {
            return with(in, f -> f.deriveFont((float) (f.getSize() - delta)));
        }
    }
}
