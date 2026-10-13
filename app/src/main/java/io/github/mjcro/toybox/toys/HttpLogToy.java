package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.http.HttpExchange;
import io.github.mjcro.toybox.api.http.HttpExchangeLog;
import io.github.mjcro.toybox.app.utils.TextFormat;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.HttpExchangeJPanel;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Toy displaying HTTP exchanges captured by clients obtained from {@link io.github.mjcro.toybox.api.Environment}.
 */
public class HttpLogToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName(SilkIcons.TRANSMIT, "HTTP log");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel(context.getEnvironment().getHttpExchangeLog());
    }

    /**
     * Panel with exchanges table and details of selected exchange.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull HttpExchangeLog buffer;
        private final @NonNull ExchangesModel model = new ExchangesModel();
        private final @NonNull JTable table = new JTable(model);
        private final @NonNull JLabel countLabel = ToyBoxLabels.create();
        private final @NonNull HttpExchangeJPanel details = new HttpExchangeJPanel();

        /**
         * Constructs panel.
         *
         * @param buffer log to display exchanges from
         */
        Panel(@NonNull HttpExchangeLog buffer) {
            super(new BorderLayout());
            this.buffer = buffer;

            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    showSelected();
                }
            });

            JButton refresh = ToyBoxButtons.create("Refresh", Hints.onAction(this::refresh));
            ToyBoxIcons.get(SilkIcons.ARROW_REFRESH).ifPresent(refresh::setIcon);
            JButton clear = ToyBoxButtons.createDelete("Clear", $ -> clear());

            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
            header.add(refresh);
            header.add(clear);
            header.add(countLabel);

            add(header, BorderLayout.PAGE_START);
            add(ToyBoxPanels.verticalSplit(new JScrollPane(table), details), BorderLayout.CENTER);

            refresh();
        }

        /**
         * Reloads exchanges from buffer.
         */
        private void refresh() {
            List<HttpExchange> exchanges = buffer.getAll();
            Collections.reverse(exchanges);
            model.setExchanges(exchanges);
            countLabel.setText(exchanges.size() + " exchange(s)");
        }

        /**
         * Clears buffer and view.
         */
        private void clear() {
            buffer.clear();
            log.info(Slf4jUtil.TOYBOX_MARKER, "Captured HTTP exchanges cleared");
            refresh();
        }

        /**
         * Displays details of selected exchange.
         */
        private void showSelected() {
            int row = table.getSelectedRow();
            if (row < 0) {
                details.clear();
                return;
            }

            details.setExchange(model.get(table.convertRowIndexToModel(row)));
        }
    }

    /**
     * Table model exposing exchanges summary, one row per exchange.
     */
    private static class ExchangesModel extends AbstractTableModel {
        private static final @NonNull String @NonNull [] columns = {"Time", "Method", "URL", "Status", "Elapsed", "Response"};
        private static final @NonNull DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                .withZone(ZoneId.systemDefault());

        private @NonNull List<@NonNull HttpExchange> exchanges = new ArrayList<>();

        void setExchanges(@NonNull List<@NonNull HttpExchange> exchanges) {
            this.exchanges = exchanges;
            fireTableDataChanged();
        }

        @NonNull HttpExchange get(int row) {
            return exchanges.get(row);
        }

        @Override
        public int getRowCount() {
            return exchanges.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public @NonNull String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public @NonNull Object getValueAt(int rowIndex, int columnIndex) {
            HttpExchange e = exchanges.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return timeFormatter.format(e.getStartedAt());
                case 1:
                    return e.getMethod();
                case 2:
                    return e.getUri().toString();
                case 3:
                    return e.getStatus().isPresent()
                            ? String.valueOf(e.getStatus().getAsInt())
                            : "Error";
                case 4:
                    return TextFormat.duration(e.getElapsed());
                default:
                    return e.getError().isPresent()
                            ? e.getError().get().getClass().getSimpleName()
                            : TextFormat.bytes(e.getResponseBodySize());
            }
        }
    }
}
