package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Table-like Swing component for ordered, nested key-value data.
 * Primitive values are displayed directly. Collections and nested maps can be expanded or collapsed.
 */
public class KeyValueJPanel extends JPanel {
    private static final int INDENT_SIZE = 16;
    private static final @NonNull String CONTENT_AVAILABLE_PROPERTY =
            KeyValueJPanel.class.getName() + ".contentAvailable";
    private static final @NonNull Insets CELL_INSETS = new Insets(4, 8, 4, 8);

    private final @NonNull ChangeListener modelListener = event -> refreshFromModel();
    private final @NonNull AtomicBoolean refreshScheduled = new AtomicBoolean();
    private @NonNull OrderedKeyValueModel model;

    /**
     * Creates an empty key-value panel.
     */
    public KeyValueJPanel() {
        this(new OrderedKeyValueModel());
    }

    /**
     * Creates a panel that displays the supplied non-null model.
     *
     * @param model non-null model to display.
     */
    public KeyValueJPanel(@NonNull OrderedKeyValueModel model) {
        super(new BorderLayout());
        this.model = Objects.requireNonNull(model, "model");
        this.model.addChangeListener(modelListener);
        refresh();
    }

    /**
     * Returns the non-null model currently displayed by this panel.
     *
     * @return current model.
     */
    public @NonNull OrderedKeyValueModel getModel() {
        return model;
    }

    /**
     * Replaces the displayed model and immediately refreshes the panel.
     *
     * @param model non-null replacement model.
     */
    public void setModel(@NonNull OrderedKeyValueModel model) {
        final OrderedKeyValueModel replacement = Objects.requireNonNull(model, "model");
        if (this.model == replacement) {
            return;
        }
        this.model.removeChangeListener(modelListener);
        this.model = replacement;
        this.model.addChangeListener(modelListener);
        refreshFromModel();
    }

    private void refreshFromModel() {
        if (SwingUtilities.isEventDispatchThread()) {
            refreshScheduled.set(false);
            refresh();
            return;
        }
        if (refreshScheduled.compareAndSet(false, true)) {
            SwingUtilities.invokeLater(() -> {
                if (refreshScheduled.compareAndSet(true, false)) {
                    refresh();
                }
            });
        }
    }

    private void refresh() {
        removeAll();
        final JPanel table = createTable();
        renderMapEntries(table, model.asMap(), 0);
        add(table, BorderLayout.PAGE_START);
        setEnabledRecursively(table, isEnabled());
        revalidate();
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (final Component component : getComponents()) {
            setEnabledRecursively(component, enabled);
        }
    }

    private void renderMapEntries(
            @NonNull JPanel table,
            @NonNull Map<@NonNull String, @Nullable Object> entries,
            int depth
    ) {
        int row = 0;
        for (final Map.Entry<@NonNull String, @Nullable Object> entry : entries.entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                addMapSection(table, entry.getKey(), castMap(value), depth, row);
            } else {
                addKeyValueRow(table, entry.getKey(), value, depth, row);
            }
            row++;
        }
    }

    private void addMapSection(
            @NonNull JPanel table,
            @NonNull String key,
            @NonNull Map<@NonNull String, @Nullable Object> entries,
            int depth,
            int row
    ) {
        final JPanel nestedTable = createTable();
        renderMapEntries(nestedTable, entries, depth + 1);
        nestedTable.setVisible(false);

        final JButton toggle = createToggleButton(nestedTable, "map");
        final JLabel keyLabel = new JLabel(key);
        keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));
        final JLabel countLabel = new JLabel(itemCount(entries.size(), "entry", "entries"));
        countLabel.setHorizontalAlignment(SwingConstants.TRAILING);

        final JPanel header = new JPanel(new BorderLayout(4, 0));
        header.setBackground(keyBackground());
        header.setForeground(keyForeground());
        header.setBorder(new EmptyBorder(2, CELL_INSETS.left + depth * INDENT_SIZE, 2, CELL_INSETS.right));
        header.add(toggle, BorderLayout.LINE_START);
        header.add(keyLabel, BorderLayout.CENTER);
        header.add(countLabel, BorderLayout.LINE_END);
        applyForeground(header, keyForeground());

        final JPanel section = new JPanel(new BorderLayout());
        section.setBackground(tableBackground());
        section.add(header, BorderLayout.PAGE_START);
        section.add(nestedTable, BorderLayout.CENTER);
        addFullWidth(table, section, row);
    }

    private void addKeyValueRow(
            @NonNull JPanel table,
            @NonNull String key,
            @Nullable Object value,
            int depth,
            int row
    ) {
        final JLabel keyLabel = new JLabel(key);
        keyLabel.setOpaque(true);
        keyLabel.setBackground(keyBackground());
        keyLabel.setForeground(keyForeground());
        keyLabel.setBorder(new EmptyBorder(
                CELL_INSETS.top,
                CELL_INSETS.left + depth * INDENT_SIZE,
                CELL_INSETS.bottom,
                CELL_INSETS.right
        ));

        final Component valueComponent;
        if (value instanceof Collection<?>) {
            valueComponent = createCollectionValue((Collection<?>) value, depth);
        } else {
            valueComponent = createPrimitiveValue(value);
        }

        final GridBagConstraints keyConstraints = constraints(0, row);
        keyConstraints.fill = GridBagConstraints.BOTH;
        table.add(keyLabel, keyConstraints);

        final GridBagConstraints valueConstraints = constraints(1, row);
        valueConstraints.weightx = 1.0;
        valueConstraints.fill = GridBagConstraints.BOTH;
        table.add(valueComponent, valueConstraints);
    }

    private @NonNull Component createCollectionValue(@NonNull Collection<?> values, int depth) {
        final JPanel itemTable = createTable();
        int row = 0;
        for (final Object item : values) {
            addKeyValueRow(itemTable, "[" + row + "]", item, depth + 1, row);
            row++;
        }
        itemTable.setVisible(false);

        final JButton toggle = createToggleButton(itemTable, "collection");
        final JLabel count = new JLabel(itemCount(values.size(), "item", "items"));
        final JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        control.setOpaque(false);
        control.add(toggle);
        control.add(count);

        final JPanel value = new JPanel(new BorderLayout());
        value.setBackground(tableBackground());
        value.setBorder(valueBorder());
        value.add(control, BorderLayout.PAGE_START);
        value.add(itemTable, BorderLayout.CENTER);
        return value;
    }

    private @NonNull Component createPrimitiveValue(@Nullable Object value) {
        final JLabel label = new JLabel(String.valueOf(value));
        label.putClientProperty("html.disable", Boolean.TRUE);
        label.setOpaque(true);
        label.setBackground(tableBackground());
        label.setBorder(BorderFactory.createCompoundBorder(valueBorder(), new EmptyBorder(CELL_INSETS)));
        return label;
    }

    private @NonNull JButton createToggleButton(@NonNull Component content, @NonNull String type) {
        final JButton button = new JButton();
        setToggleState(button, type, false);
        button.setBorder(new EmptyBorder(2, 4, 2, 6));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        final boolean contentAvailable = content.getPreferredSize().height > 0;
        button.putClientProperty(CONTENT_AVAILABLE_PROPERTY, contentAvailable);
        button.setEnabled(contentAvailable);
        button.addActionListener(event -> {
            final boolean expanded = !content.isVisible();
            content.setVisible(expanded);
            setToggleState(button, type, expanded);
            revalidate();
            repaint();
        });
        return button;
    }

    private static void setToggleState(@NonNull JButton button, @NonNull String type, boolean expanded) {
        final String action = (expanded ? "Collapse " : "Expand ") + type;
        button.setText(expanded ? "\u25be" : "\u25b8");
        button.setToolTipText(action);
        button.getAccessibleContext().setAccessibleName(action);
    }

    private static @NonNull JPanel createTable() {
        final JPanel table = new JPanel(new GridBagLayout());
        table.setBackground(tableBackground());
        return table;
    }

    private static void addFullWidth(@NonNull JPanel table, @NonNull Component component, int row) {
        final GridBagConstraints constraints = constraints(0, row);
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        table.add(component, constraints);
    }

    private static @NonNull GridBagConstraints constraints(int column, int row) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        return constraints;
    }

    private static @NonNull Border valueBorder() {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, gridColor());
    }

    private static @NonNull Color keyBackground() {
        final Color panelColor = UIManager.getColor("Panel.background");
        if (panelColor != null) {
            return panelColor;
        }
        final Color controlColor = UIManager.getColor("control");
        return controlColor == null ? new Color(224, 224, 224) : controlColor;
    }

    private static @NonNull Color keyForeground() {
        final Color color = UIManager.getColor("TableHeader.foreground");
        return color == null ? Color.DARK_GRAY : color;
    }

    private static @NonNull Color tableBackground() {
        final Color color = UIManager.getColor("Table.background");
        return color == null ? Color.WHITE : color;
    }

    private static @NonNull Color gridColor() {
        final Color color = UIManager.getColor("Table.gridColor");
        return color == null ? new Color(192, 192, 192) : color;
    }

    private static void applyForeground(@NonNull JPanel panel, @NonNull Color color) {
        for (final Component component : panel.getComponents()) {
            component.setForeground(color);
        }
    }

    private static void setEnabledRecursively(@NonNull Component component, boolean enabled) {
        if (component instanceof JButton) {
            final Object contentAvailable = ((JButton) component).getClientProperty(CONTENT_AVAILABLE_PROPERTY);
            component.setEnabled(enabled && !Boolean.FALSE.equals(contentAvailable));
        } else {
            component.setEnabled(enabled);
        }
        if (component instanceof Container) {
            for (final Component child : ((Container) component).getComponents()) {
                setEnabledRecursively(child, enabled);
            }
        }
    }

    private static @NonNull String itemCount(int size, @NonNull String singular, @NonNull String plural) {
        return size + " " + (size == 1 ? singular : plural);
    }

    @SuppressWarnings("unchecked")
    private static @NonNull Map<@NonNull String, @Nullable Object> castMap(@NonNull Object value) {
        return (Map<@NonNull String, @Nullable Object>) value;
    }

    /**
     * Opens a demo window containing primitive, collection, array, and nested map values.
     *
     * @param args non-null command-line arguments; values are ignored.
     */
    public static void main(@NonNull String @NonNull [] args) {
        SwingUtilities.invokeLater(() -> {
            ToyBoxLaF.initialize(false);

            final Map<@NonNull String, @Nullable Object> address = new LinkedHashMap<>();
            address.put("city", "Kyiv");
            address.put("postal code", 1001);

            final Map<@NonNull String, @Nullable Object> profile = new LinkedHashMap<>();
            profile.put("name", "Ada Lovelace");
            profile.put("active", true);
            profile.put("score", 98.5f);
            profile.put("roles", List.of("admin", "author"));
            profile.put("lucky numbers", new int[]{3, 7, 11});
            profile.put("address", address);

            final KeyValueJPanel panel = new KeyValueJPanel(new OrderedKeyValueModel(profile));
            Components.show(new JScrollPane(panel), "Ordered key-value component", true);
        });
    }
}
