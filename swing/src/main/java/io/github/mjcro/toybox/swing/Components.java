package io.github.mjcro.toybox.swing;

import io.github.mjcro.toybox.swing.hint.Hints;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Collection of Swing component utility methods for display,
 * styling, layout, and manipulation.
 */
public class Components {
    /**
     * Displays the given component in a default example frame.
     *
     * @param component component to display
     */
    public static void show(@NonNull Component component) {
        show(component, "Example", true);
    }

    /**
     * Displays the given component aligned to the top of a panel in a default frame.
     *
     * @param component component to display
     */
    public static void showLine(@NonNull Component component) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JPanel(), BorderLayout.CENTER);
        panel.add(component, BorderLayout.PAGE_START);
        show(panel);
    }

    /**
     * Displays the given component in a frame with the specified title and close behavior.
     *
     * @param component   component to display
     * @param title       frame title
     * @param exitOnClose whether to exit the application on frame close
     */
    public static void show(@NonNull Component component, @NonNull String title, boolean exitOnClose) {
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(
                exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE
        );
        frame.getContentPane().add(component);
        frame.pack();
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    /**
     * Creates a color from HSV with a fixed saturation and brightness, varying by hue.
     *
     * @param hue hue value
     * @return computed color
     */
    public static @NonNull Color hsv(float hue) {
        // TODO apply dark mode
        return Color.getHSBColor(hue, .8f, .5f);
    }

    /**
     * Derives a display color for the given value.
     * Enum values produce distinct hue-rotated colors; other types return {@code null}.
     *
     * @param value value to derive color from, may be {@code null}
     * @return derived color, or {@code null} for non-enum values
     */
    public static @Nullable Color deriveColor(@Nullable Object value) {
        if (value instanceof Enum<?>) {
            final Enum<?> e = (Enum<?>) value;
            return Components.hsv(0.09f - (e.ordinal() * 0.15f));
        }
        return null;
    }

    /**
     * Registers an action to run when the Enter key is pressed in the given text field.
     *
     * @param field  text field to listen on
     * @param action action to execute on Enter key press
     */
    public static void onPressEnter(@NonNull JTextField field, @NonNull Runnable action) {
        Hints.onEnterKeyPress(action).apply(field);
    }

    /**
     * Sets the enabled state on all provided components.
     *
     * @param enabled    whether components should be enabled
     * @param components components to update
     */
    public static void setEnabled(boolean enabled, @NonNull JComponent @NonNull ... components) {
        for (final JComponent c : components) {
            c.setEnabled(enabled);
        }
    }

    /**
     * Applies a boolean value to all consumers and returns a {@link Runnable}
     * that applies the inverse value.
     *
     * @param value     boolean value to apply
     * @param consumers consumers to receive the value
     * @return runnable that applies the inverse value
     */
    @SafeVarargs
    public static @NonNull Runnable setBoolean(boolean value, @NonNull Consumer<@NonNull Boolean> @NonNull ... consumers) {
        for (final Consumer<Boolean> consumer : consumers) {
            consumer.accept(value);
        }

        return () -> {
            for (final Consumer<Boolean> consumer : consumers) {
                consumer.accept(!value);
            }
        };
    }

    /**
     * Wraps the given component in a panel with normal padding.
     *
     * @param other component to wrap
     * @return padded panel containing the component
     */
    public static @NonNull JComponent padding(@NonNull Component other) {
        return padding(other, false);
    }

    /**
     * Wraps the given component in a panel with normal padding, optionally transparent.
     *
     * @param other       component to wrap
     * @param transparent whether the padding panel should be transparent
     * @return padded panel containing the component
     */
    public static @NonNull JComponent padding(@NonNull Component other, boolean transparent) {
        final JPanel padding = new JPanel();
        Hints.PADDING_NORMAL.apply(padding);
        padding.setLayout(new BorderLayout());
        padding.add(other);
        if (transparent) {
            padding.setOpaque(false);
        }
        return padding;
    }

    /**
     * Constrains the maximum and preferred height of the given component.
     *
     * @param c      component to constrain
     * @param height maximum height in pixels
     */
    public static void setMaxHeight(@NonNull Component c, int height) {
        Dimension d;
        d = c.getMaximumSize();
        d.height = height;
        c.setMaximumSize(d);
        d = c.getPreferredSize();
        d.height = height;
        c.setPreferredSize(d);
    }

    /**
     * Calculates the pixel width of a string rendered with the component's font.
     *
     * @param component component whose font is used
     * @param s         string to measure
     * @return width in pixels
     */
    public static int getStringWidth(@NonNull Component component, @NonNull String s) {
        final AffineTransform affinetransform = new AffineTransform();
        final FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        final Font font = component.getFont();
        return (int) (font.getStringBounds(s, frc).getWidth());
    }

    /**
     * Passes the given object to a consumer and returns it.
     * If either argument is {@code null}, the consumer is skipped.
     *
     * @param t        object to process, may be {@code null}
     * @param consumer consumer to apply, may be {@code null}
     * @param <T>      type of the object
     * @return the original object
     */
    public static <T> @Nullable T with(@Nullable T t, @Nullable Consumer<@NonNull T> consumer) {
        if (t != null && consumer != null) {
            consumer.accept(t);
        }
        return t;
    }

    /**
     * Recursively sets {@code inheritsPopupMenu} to {@code true} on the given
     * component and all its nested children.
     *
     * @param c component to process, may be {@code null}
     */
    public static void setInheritedPopupRecursively(@Nullable Component c) {
        if (c == null /*|| c instanceof JButton || c instanceof JComboBox<?>*/) {
            return;
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setInheritsPopupMenu(true);
        }
        if (c instanceof java.awt.Container) {
            final java.awt.Container cont = (java.awt.Container) c;
            final Component[] nested = cont.getComponents();
            if (nested != null) {
                for (final Component n : nested) {
                    setInheritedPopupRecursively(n);
                }
            }
        }
    }

    /**
     * Utility methods for manipulating component fonts.
     */
    public static class Fonts {
        /**
         * Applies a font transformation function to the given component.
         *
         * @param in   component whose font will be transformed
         * @param func font transformation function, may be {@code null}
         * @param <T>  component type
         * @return the input component
         */
        public static <T extends Component> @Nullable T with(@Nullable T in, @Nullable Function<@NonNull Font, @NonNull Font> func) {
            return Components.with(in, t -> {
                if (func != null) {
                    t.setFont(func.apply(t.getFont()));
                }
            });
        }

        /**
         * Reduces the font size of the given component by the specified delta.
         *
         * @param in    component whose font will be reduced
         * @param delta number of points to subtract from the font size
         * @param <T>   component type
         * @return the input component
         */
        public static <T extends Component> @Nullable T withSmaller(@Nullable T in, int delta) {
            return with(in, f -> f.deriveFont((float) (f.getSize() - delta)));
        }
    }
}
