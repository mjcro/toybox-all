package io.github.mjcro.toybox.swing.renderers;

import com.formdev.flatlaf.FlatClientProperties;
import io.github.mjcro.interfaces.strings.WithName;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.swing.Components;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.function.Function;

public class TableCellRendererBadge extends AbstractTableCellRenderer {
    private final JPanel panel;
    private final JLabel label;
    private final Function<Object, Color> backgroundColorResolver;

    public static TableCellRendererBadge forColoredEnum() {
        return new TableCellRendererBadge(Components::deriveColor);
    }

    public TableCellRendererBadge() {
        this(null);
    }

    public TableCellRendererBadge(Function<Object, Color> backgroundColorResolver) {
        this.panel = new JPanel(new GridBagLayout());
        this.label = new JLabel();
        this.label.setFont(this.label.getFont().deriveFont(11.2f));
        label.putClientProperty(FlatClientProperties.STYLE, "arc: 10; border: 2,4,2,4,#667766; background: #f87171; foreground: #ffffff");
//        label.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 2,4,2,4; background: #f87171; foreground: #ffffff");

        this.panel.setOpaque(true);
        this.panel.add(this.label);

        this.backgroundColorResolver = backgroundColorResolver == null ? $ -> selectedBg : backgroundColorResolver;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        label.setText(null);
        label.putClientProperty(FlatClientProperties.STYLE_CLASS, null);
        label.setBackground(backgroundColorResolver.apply(value));

        String s = null;
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
