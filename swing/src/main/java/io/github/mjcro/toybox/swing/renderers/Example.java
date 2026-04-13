package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.Instant;

/**
 * Demonstration panel showcasing various table cell renderers.
 */
public class Example extends JPanel {
    /**
     * Constructs the example panel with a pre-populated table.
     */
    public Example() {
        super(new BorderLayout());

        final DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Null");
        model.addColumn("String");
        model.addColumn("Bold");
        model.addColumn("Mono");
        model.addColumn("Number");
        model.addColumn("Instant");
        model.addColumn("Badge");
        model.addColumn("URL");

        model.addRow(new Object[]{
                null,
                "Hello, world",
                "Admin",
                "static",
                Math.PI,
                Instant.now(),
                "normal",
                "https://google.com"
        });
        model.addRow(new Object[]{
                null,
                "Second",
                "Admin",
                "foo()",
                Math.PI * 8,
                Instant.now(),
                "abc",
                "https://httpbin.org"
        });

        final JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRendererNull());
        table.getColumnModel().getColumn(1).setCellRenderer(new TableCellRendererString());
        table.getColumnModel().getColumn(2).setCellRenderer(new TableCellRendererString(Hints.TEXT_SEMIBOLD));
        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRendererString(Hints.TEXT_MONOSPACED, Hints.FONT_SMALLER_1));
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRendererNumber("%.2fs"));
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRendererInstant());
        table.getColumnModel().getColumn(6).setCellRenderer(new TableCellRendererBadge());
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRendererLink());
        table.setRowHeight(30);

        super.add(new JScrollPane(table));
    }

    /**
     * Entry point for running the renderer example standalone.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(@NonNull String[] args) {
        ToyBoxLaF.initialize(false);
        Components.show(new Example());
    }
}
