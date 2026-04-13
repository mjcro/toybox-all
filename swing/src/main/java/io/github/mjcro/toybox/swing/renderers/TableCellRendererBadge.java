package io.github.mjcro.toybox.swing.renderers;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.function.Function;

/**
 * Table cell renderer that displays values as styled badge labels.
 * Supports {@link Labeled}, {@link WithName}, {@link CharSequence}, and {@link Enum} value types.
 */
public class TableCellRendererBadge extends AbstractTableCellRenderer {
    private final @NonNull JPanel panel;
    private final @NonNull JLabel label;
    private final @NonNull Function<@Nullable Object, @NonNull Color> backgroundColorResolver;

    /**
     * Creates a badge renderer that derives background color from enum values.
     *
     * @return a new badge renderer for colored enums
     */
    public static @NonNull TableCellRendererBadge forColoredEnum() {
        return new TableCellRendererBadge(Components::deriveColor);
    }

    /**
     * Creates a badge renderer with the default background color.
     */
    public TableCellRendererBadge() {
        this(null);
    }

    /**
     * Creates a badge renderer with a custom background color resolver.
     *
     * @param backgroundColorResolver function to determine badge background color from the cell value,
     *                                or {@code null} to use the default selected background
     */
    public TableCellRendererBadge(@Nullable Function<@Nullable Object, @NonNull Color> backgroundColorResolver) {
        this.panel = new JPanel(new GridBagLayout());
        this.label = ToyBoxLabels.create();
        this.label.setFont(this.label.getFont().deriveFont(11.2f));
        label.putClientProperty(FlatClientProperties.STYLE, "arc: 10; border: 2,4,2,4,#667766; background: #f87171; foreground: #ffffff");

        this.panel.setOpaque(true);
        this.panel.add(this.label);

        this.backgroundColorResolver = backgroundColorResolver == null ? $ -> selectedBg : backgroundColorResolver;
    }

    @Override
    public @NonNull Component getTableCellRendererComponent(
            @NonNull JTable table,
            @Nullable Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        label.setText(null);
        label.putClientProperty(FlatClientProperties.STYLE_CLASS, null);
        label.setBackground(backgroundColorResolver.apply(value));

        @Nullable String s = null;
        if (value instanceof CharSequence) {
            s = value.toString();
        } else if (value instanceof Labeled) {
            s = ((Labeled) value).getName();
        } else if (value instanceof WithName) {
            s = ((WithName) value).getName();
        } else if (value instanceof Enum) {
            s = ((Enum<?>) value).name().toUpperCase(Locale.ROOT);
        }

        if (s != null) {
            label.setText(s);
        }

        withSelection(panel, isSelected, hasFocus);
        return panel;
    }
}
