package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.renderers.AbstractTableCellRendererLabel;
import io.github.mjcro.toybox.swing.renderers.TableCellRendererString;
import io.github.mjcro.toybox.swing.renderers.ToyBoxTableCellRenderer;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class UIPropertiesToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://palette", "Swing UI properties");
    }

    @Override
    public JPanel build(Context context) {
        return new UIPropertiesPanel();
    }

    static class UIPropertiesPanel extends JPanel {
        private final JTable table = new JTable();
        private final JComboBox<Filter> filters = new JComboBox<>(Filter.values());

        public UIPropertiesPanel() {
            initComponents();
            updateData();
        }

        private void initComponents() {
            table.setPreferredScrollableViewportSize(new Dimension(100, 100));
            JScrollPane pane = new JScrollPane(table);

            super.setLayout(new BorderLayout());
            super.add(pane, BorderLayout.CENTER);

            JPanel header = new JPanel(new BorderLayout());
            Hints.PADDING_NORMAL.apply(header);
            header.add(ToyBoxLabels.create("Filter  "), BorderLayout.LINE_START);
            header.add(filters, BorderLayout.CENTER);
            super.add(header, BorderLayout.PAGE_START);

            filters.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    updateData();
                }
            });
        }

        private void updateData() {
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();
            ArrayList<Property> properties = new ArrayList<>();
            Filter filter = (Filter) filters.getSelectedItem();
            defaults.forEach((key, value) -> {
                Property p = new Property(key, value);
                if (filter.allow(p)) {
                    properties.add(p);
                }
            });
            properties.sort(Comparator.comparing(Property::getName));
            setData(properties);
        }

        private void setData(ArrayList<Property> properties) {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Property");
            model.addColumn("Type");
            model.addColumn("Value");
            for (Property p : properties) {
                model.addRow(new Object[]{p.getName(), p.getValueClass(), p.getValue()});
            }
            table.setModel(model);
            table.setDefaultEditor(Object.class, null);
            table.getColumnModel().getColumn(0).setCellRenderer(TableCellRendererString.bold());
            table.getColumnModel().getColumn(1).setCellRenderer(TableCellRendererString.monospaced());
            table.getColumnModel().getColumn(2).setCellRenderer(new Renderer());
        }

        private static class Renderer extends ToyBoxTableCellRenderer {
            private final SimpleLabelRenderer rendererIcon = new SimpleLabelRenderer();
            private final SimpleLabelRenderer rendererFont = new SimpleLabelRenderer();
            private final SimpleLabelRenderer rendererColor = new SimpleLabelRenderer();
            private final TableCellRendererString rendererMonospaced = TableCellRendererString.monospaced();

            public Renderer() {
                super();
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
                if (value instanceof Icon) {
                    Icon icon = (Icon) value;
                    JLabel label = rendererIcon.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setIcon(icon);
                    label.setText(String.format("[%d, %d]", icon.getIconWidth(), icon.getIconHeight()));
                    rendererIcon.resizeHeight(label, table, row);
                    return label;
                } else if (value instanceof Color) {
                    Color color = (Color) value;
                    JLabel label = rendererColor.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        label.setBackground(color);
                    }
                    label.setText(String.format(Locale.ROOT, "#%X", color.getRGB()));
                    return label;
                } else if (value instanceof Dimension) {
                    Dimension dim = (Dimension) value;
                    return rendererMonospaced.getTableCellRendererComponent(
                            table,
                            String.format(Locale.ROOT, "[%d, %d]", dim.width, dim.height),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );
                } else if (value instanceof Insets) {
                    Insets insets = (Insets) value;
                    return rendererMonospaced.getTableCellRendererComponent(
                            table,
                            String.format(Locale.ROOT, "[%d, %d, %d, %d]", insets.top, insets.right, insets.bottom, insets.left),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );
                } else if (value instanceof Border) {
                    Border border = (Border) value;
                    JLabel label = ToyBoxLabels.create();
                    var insets = border.getBorderInsets(label);
                    return rendererMonospaced.getTableCellRendererComponent(
                            table,
                            String.format(Locale.ROOT, "[%d, %d, %d, %d]", insets.top, insets.right, insets.bottom, insets.left),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );
                } else if (value instanceof Font) {
                    Font font = (Font) value;
                    JLabel label = rendererFont.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setText(font.getName());
                    label.setFont(font);
                    rendererFont.resizeHeight(label, table, row);
                    return label;
                }

                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }

        private static class SimpleLabelRenderer extends AbstractTableCellRendererLabel {
            @Override
            public JLabel getTableCellRendererComponent(
                    final JTable table,
                    final Object value,
                    final boolean isSelected,
                    final boolean hasFocus,
                    final int row,
                    final int column
            ) {
                return (JLabel) withSelection(label, isSelected, hasFocus);
            }
        }

        private enum Filter {
            ALL, COLORS, FONTS, ICONS, DIMENSIONS;

            public boolean allow(Property property) {
                switch (this) {
                    case COLORS:
                        return property.value instanceof Color;
                    case FONTS:
                        return property.value instanceof Font;
                    case ICONS:
                        return property.value instanceof Icon;
                    case DIMENSIONS:
                        return property.value instanceof Insets || property.value instanceof Dimension || property.value instanceof Border;
                    default:
                        return true;
                }
            }
        }

        @Getter
        private static class Property {
            private final String name;
            private final Object value;

            public Property(Object name, Object value) {
                this.name = name.toString();
                if (value instanceof UIDefaults.LazyValue) {
                    this.value = ((UIDefaults.LazyValue) value).createValue(UIManager.getLookAndFeelDefaults());
                } else if (value instanceof UIDefaults.ActiveValue) {
                    this.value = ((UIDefaults.ActiveValue) value).createValue(UIManager.getLookAndFeelDefaults());
                } else {
                    this.value = value;
                }
            }

            public String getValueClass() {
                return value.getClass().getSimpleName();
            }
        }
    }
}
