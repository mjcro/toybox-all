package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.interfaces.experimental.integration.Headers;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel that displays HTTP headers in a two-column table view.
 * Supports {@link HttpHeaders}, {@link Headers}, and raw map representations.
 */
public class HttpHeadersJPanel extends MultiViewTableOrExceptionPanel {
    /**
     * Displays headers from a {@link HttpHeaders} instance.
     *
     * @param headers the HTTP headers to display, or {@code null} to show empty
     */
    public void setViewHttpHeaders(@Nullable HttpHeaders headers) {
        if (headers == null) {
            setViewEmpty();
            return;
        }
        this.setViewHttpHeaders(headers.map());
    }

    /**
     * Displays headers from a {@link Headers} instance.
     *
     * @param headers the headers to display, or {@code null} to show empty
     */
    public void setViewHttpHeaders(@Nullable Headers headers) {
        if (headers == null || headers.isEmpty()) {
            setViewEmpty();
            return;
        }
        final HashMap<@NonNull String, @NonNull List<@NonNull String>> h = new HashMap<>();
        for (final Map.Entry<@NonNull String, @NonNull List<@NonNull String>> header : headers) {
            h.put(header.getKey(), header.getValue());
        }
        this.setViewHttpHeaders(h);
    }

    /**
     * Displays headers from a simple single-valued map.
     *
     * @param headers the header name-to-value map, or {@code null} to show empty
     */
    public void setViewSimpleHttpHeaders(@Nullable Map<@NonNull String, @NonNull String> headers) {
        if (headers == null || headers.isEmpty()) {
            setViewEmpty();
            return;
        }
        final HashMap<@NonNull String, @NonNull List<@NonNull String>> h = new HashMap<>();
        for (final Map.Entry<@NonNull String, @NonNull String> header : headers.entrySet()) {
            h.put(header.getKey(), Collections.singletonList(header.getValue()));
        }
        this.setViewHttpHeaders(h);
    }

    /**
     * Displays headers from a multi-valued map.
     *
     * @param headers the header name-to-values map, or {@code null} to show empty
     */
    public void setViewHttpHeaders(@Nullable Map<@NonNull String, @NonNull List<@NonNull String>> headers) {
        if (headers == null || headers.isEmpty()) {
            setViewEmpty();
            return;
        }

        final ArrayList<@NonNull HttpHeaderRow> rows = new ArrayList<>();
        for (final Map.Entry<@NonNull String, @NonNull List<@NonNull String>> e : headers.entrySet()) {
            for (final String v : e.getValue()) {
                rows.add(new HttpHeaderRow(e.getKey(), v));
            }
        }
        rows.sort(Comparator.comparing($ -> $.name));

        setViewTable(new HttpHeaderModel(rows));
    }

    @Override
    public void setViewEmpty() {
        setViewLabel("No headers");
    }

    /**
     * Table model that exposes HTTP header rows as a two-column ("Header", "Value") table.
     */
    private static class HttpHeaderModel implements TableModel {
        private static final @NonNull String @NonNull [] columns = new String[]{"Header", "Value"};
        private final @NonNull List<@NonNull HttpHeaderRow> rows;

        private HttpHeaderModel(@NonNull List<@NonNull HttpHeaderRow> rows) {
            this.rows = rows;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public @NonNull String getColumnName(int columnIndex) {
            return columns[columnIndex];
        }

        @Override
        public @NonNull Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public @NonNull Object getValueAt(int rowIndex, int columnIndex) {
            final HttpHeaderRow row = rows.get(rowIndex);
            return columnIndex == 0 ? row.name : row.value;
        }

        @Override
        public void setValueAt(@Nullable Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(@NonNull TableModelListener l) {
        }

        @Override
        public void removeTableModelListener(@NonNull TableModelListener l) {
        }
    }

    /**
     * Immutable pair holding a single HTTP header name and value.
     */
    private static class HttpHeaderRow {
        private final @NonNull String name;
        private final @NonNull String value;

        private HttpHeaderRow(@NonNull String name, @NonNull String value) {
            this.name = name;
            this.value = value;
        }
    }
}
