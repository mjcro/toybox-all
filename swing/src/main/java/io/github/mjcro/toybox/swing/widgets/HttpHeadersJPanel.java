package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.interfaces.experimental.integration.Headers;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeadersJPanel extends MultiViewTableOrExceptionPanel {
    public void setViewHttpHeaders(HttpHeaders headers) {
        if (headers == null) {
            setViewEmpty();
            return;
        }
        this.setViewHttpHeaders(headers.map());
    }

    public void setViewHttpHeaders(Headers headers) {
        if (headers == null || headers.isEmpty()) {
            setViewEmpty();
            return;
        }
        HashMap<String, List<String>> h = new HashMap<>();
        for (Map.Entry<String, List<String>> header : headers) {
            h.put(header.getKey(), header.getValue());
        }
        this.setViewHttpHeaders(h);
    }

    public void setViewSimpleHttpHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            setViewEmpty();
            return;
        }
        HashMap<String, List<String>> h = new HashMap<>();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            h.put(header.getKey(), Collections.singletonList(header.getValue()));
        }
        this.setViewHttpHeaders(h);
    }

    public void setViewHttpHeaders(Map<String, List<String>> headers) {
        if (headers == null || headers.isEmpty()) {
            setViewEmpty();
            return;
        }

        ArrayList<HttpHeaderRow> rows = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            for (String v : e.getValue()) {
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

    private static class HttpHeaderModel implements TableModel {
        private static final String[] columns = new String[]{"Header", "Value"};
        private final List<HttpHeaderRow> rows;

        private HttpHeaderModel(List<HttpHeaderRow> rows) {
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
        public String getColumnName(int columnIndex) {
            return columns[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HttpHeaderRow row = rows.get(rowIndex);
            return columnIndex == 0 ? row.name : row.value;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }

    private static class HttpHeaderRow {
        private final String name, value;

        private HttpHeaderRow(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
