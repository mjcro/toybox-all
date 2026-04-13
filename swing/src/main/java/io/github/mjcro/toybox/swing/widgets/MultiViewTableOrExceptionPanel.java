package io.github.mjcro.toybox.swing.widgets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Multi-view panel that switches between a table view, an empty view,
 * and an exception details view using a card layout.
 */
public class MultiViewTableOrExceptionPanel extends MultiViewBasicPanel {
    /** Card identifier for the table view. */
    public static final @NonNull String
            CARD_TABLE = "table";

    private final @NonNull JTable table = new JTable();

    /**
     * Creates a new panel with an empty table.
     */
    public MultiViewTableOrExceptionPanel() {
        add(new JScrollPane(table), CARD_TABLE);
    }

    /**
     * Creates a new panel and immediately displays the given table model.
     *
     * @param model the table model to display
     */
    public MultiViewTableOrExceptionPanel(@NonNull TableModel model) {
        this();
        setViewTable(model);
    }

    /**
     * Returns the underlying table component.
     *
     * @return the table
     */
    public @NonNull JTable getTable() {
        return table;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        table.setEnabled(enabled);
    }

    /**
     * Switches the view to the table card with the given model,
     * or to the empty card if the model is {@code null}.
     *
     * @param model the table model, or {@code null} to show the empty view
     */
    public void setViewTable(@Nullable TableModel model) {
        if (model == null) {
            setViewEmpty();
        } else {
            table.setModel(model);
            setSelectedCard(CARD_TABLE);
        }
    }
}
