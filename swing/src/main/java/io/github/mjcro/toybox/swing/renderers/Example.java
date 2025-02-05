package io.github.mjcro.toybox.swing.renderers;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.ToyboxLaF;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;

public class Example extends JPanel {
    public Example() {
        super(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
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

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRendererNull());
        table.getColumnModel().getColumn(1).setCellRenderer(new TableCellRendererString());
        table.getColumnModel().getColumn(2).setCellRenderer(new TableCellRendererString(Styles.TEXT_SEMIBOLD));
        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRendererString(Styles.TEXT_MONOSPACED, Styles.FONT_SMALLER_1));
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRendererNumber("%.2fs"));
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRendererInstant());
        table.getColumnModel().getColumn(6).setCellRenderer(new TableCellRendererBadge());
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRendererLink());
        table.setRowHeight(30);

        super.add(new JScrollPane(table));
    }

    public static void main(String[] args) {
        ToyboxLaF.initialize(false);
        Components.show(new Example());
    }
}
