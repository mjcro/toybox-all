package io.github.mjcro.toybox.swing.widgets;

import javax.swing.*;
import javax.swing.table.TableModel;

public class MultiViewTableOrExceptionPanel extends MultiViewBasicPanel {
    public static final String
            CARD_TABLE = "table";

    private final JTable table = new JTable();

    public MultiViewTableOrExceptionPanel() {
        add(new JScrollPane(table), CARD_TABLE);
    }

    public MultiViewTableOrExceptionPanel(TableModel model) {
        this();
        setViewTable(model);
    }

    public JTable getTable() {
        return table;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        table.setEnabled(enabled);
    }

    public void setViewTable(TableModel model) {
        if (model == null) {
            setViewEmpty();
        } else {
            table.setModel(model);
            setSelectedCard(CARD_TABLE);
        }
    }
}
