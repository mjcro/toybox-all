package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.renderers.TableCellRendererBadge;
import io.github.mjcro.toybox.swing.renderers.TableCellRendererString;
import io.github.mjcro.toybox.swing.util.DelayedInvoker;
import io.github.mjcro.toybox.toys.StringAnalysis.HashAlgorithm;
import io.github.mjcro.toybox.toys.StringAnalysis.MarkerRow;
import io.github.mjcro.toybox.toys.StringAnalysis.Trait;
import io.github.mjcro.toybox.toys.StringAnalysis.TraitGroup;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Toy for analyzing string properties such as length, encoding,
 * character classes, hash values and AI markers.
 */
public class StringAnalyzerToy implements Toy {

    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName(SilkIcons.TEXT_ALLCAPS, "String Analyzer");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        final Panel panel = new Panel(s -> context.getEnvironment().clipboardPut(s));
        context.getInitialData()
                .filter($ -> $ instanceof CharSequence)
                .map(Object::toString)
                .ifPresent(panel::setSourceText);

        return panel;
    }

    /**
     * Main panel: source text on top, fixed-layout summary and markers table below.
     * Analysis runs debounced off the EDT; only values change, never the layout.
     */
    private static final class Panel extends JPanel {
        private static final @NonNull Duration DEBOUNCE = Duration.ofMillis(150);
        private static final Hints.@NonNull LaFStyle TEXT_H2 = new Hints.LaFStyle("h2");
        private final @NonNull Consumer<@NonNull String> clipboard;
        private final @NonNull DelayedInvoker invoker = new DelayedInvoker(DEBOUNCE);
        private final @NonNull AtomicLong generation = new AtomicLong();
        private final @NonNull JTextArea sourceText;
        private final @NonNull JLabel lengthValue = createTileValue();
        private final @NonNull JLabel codePointsValue = createTileValue();
        private final @NonNull JLabel bytesValue = createTileValue();
        private final @NonNull JLabel linesValue = createTileValue();
        private final @NonNull JLabel wordsValue = createTileValue();
        private final @NonNull Map<@NonNull Trait, @NonNull JLabel> chips = new EnumMap<>(Trait.class);
        private final @NonNull JComboBox<@NonNull HashAlgorithm> algorithm = new JComboBox<>(HashAlgorithm.values());
        private final @NonNull JTextField hashText;
        private final @NonNull JButton copyHash;
        private final @NonNull JLabel markersSummary;
        private final @NonNull MarkersTableModel markersModel = new MarkersTableModel();
        private final @NonNull JTable markersTable = new JTable(markersModel);
        private final @NonNull List<@NonNull Object> markerHighlights = new ArrayList<>();
        private @Nullable Object currentHighlight;

        Panel(@NonNull Consumer<@NonNull String> clipboard) {
            this.clipboard = clipboard;
            this.sourceText = ToyBoxTextComponents.createTextArea(Hints.TEXT_MONOSPACED);
            this.sourceText.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(@NonNull DocumentEvent e) {
                    scheduleAnalysis();
                }

                @Override
                public void removeUpdate(@NonNull DocumentEvent e) {
                    scheduleAnalysis();
                }

                @Override
                public void changedUpdate(@NonNull DocumentEvent e) {
                    scheduleAnalysis();
                }
            });
            this.hashText = ToyBoxTextComponents.createTextField(Hints.TEXT_MONOSPACED, Hints.NOT_EDITABLE_TEXT);
            this.copyHash = ToyBoxButtons.create(
                    Hints.buttonIcon(SilkIcons.PAGE_COPY),
                    Hints.setToolTipText("Copy hash"),
                    Hints.onAction(() -> clipboard.accept(hashText.getText()))
            );
            this.algorithm.setSelectedItem(HashAlgorithm.SHA256);
            this.algorithm.addActionListener(e -> scheduleAnalysis());
            this.markersSummary = ToyBoxLabels.create(Hints.PADDING_NORMAL);

            final JPanel summary = buildSummary();
            Components.setInheritedPopupRecursively(summary);

            this.setLayout(new BorderLayout());
            this.add(
                    ToyBoxPanels.verticalSplit(
                            null, new JScrollPane(sourceText),
                            null, summary,
                            "Invisible characters and AI markers", buildMarkers()
                    ),
                    BorderLayout.CENTER
            );
            scheduleAnalysis();
        }

        void setSourceText(@NonNull String s) {
            sourceText.setText(s);
        }

        private @NonNull JPanel buildSummary() {
            final JPanel panel = new JPanel(new MigLayout("insets 8, fillx, gapy 6", "[right][grow,fill]"));
            panel.add(createGroupLabel("Size"));
            panel.add(
                    ToyBoxPanels.horizontalGrid(
                            12,
                            createTile(lengthValue, "chars"),
                            createTile(codePointsValue, "code points"),
                            createTile(bytesValue, "UTF-8 bytes"),
                            createTile(linesValue, "lines"),
                            createTile(wordsValue, "words")
                    ),
                    "wrap"
            );
            for (final TraitGroup group : TraitGroup.values()) {
                final JPanel row = new JPanel(new MigLayout("insets 0, gapx 16"));
                for (final Trait trait : Trait.values()) {
                    if (trait.group == group) {
                        final JLabel chip = ToyBoxLabels.create(trait.text, trait.description);
                        chips.put(trait, chip);
                        row.add(chip);
                    }
                }
                panel.add(createGroupLabel(group.text));
                panel.add(row, "wrap");
            }
            final JPanel hashRow = new JPanel(new MigLayout("insets 0, fillx", "[][grow,fill][]"));
            hashRow.add(algorithm);
            hashRow.add(hashText);
            hashRow.add(copyHash);
            panel.add(createGroupLabel("Hash"));
            panel.add(hashRow, "wrap");
            return panel;
        }

        private @NonNull JPanel buildMarkers() {
            markersTable.setFillsViewportHeight(true);
            markersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            markersTable.getTableHeader().setReorderingAllowed(false);
            markersTable.getColumnModel().getColumn(MarkersTableModel.POSITION).setCellRenderer(TableCellRendererString.monospaced());
            markersTable.getColumnModel().getColumn(MarkersTableModel.CODE).setCellRenderer(TableCellRendererString.monospaced());
            markersTable.getColumnModel().getColumn(MarkersTableModel.ACTION).setCellRenderer(TableCellRendererBadge.forColoredEnum());
            markersTable.getColumnModel().getColumn(MarkersTableModel.REPLACEMENT).setCellRenderer(TableCellRendererString.monospaced());
            final int[] widths = {80, 90, 360, 100, 120};
            for (int i = 0; i < widths.length; i++) {
                markersTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
            markersTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    showCurrentMarker();
                }
            });
            markersTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(@NonNull MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        selectCurrentMarkerInSource();
                    }
                }
            });

            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(markersSummary, BorderLayout.NORTH);
            panel.add(new JScrollPane(markersTable), BorderLayout.CENTER);
            return panel;
        }

        /**
         * Snapshots input on the EDT and analyzes it in background;
         * results of superseded runs are dropped.
         */
        private void scheduleAnalysis() {
            final String s = sourceText.getText();
            final HashAlgorithm hashAlgorithm = algorithm.getItemAt(algorithm.getSelectedIndex());
            final long current = generation.incrementAndGet();
            invoker.execute(() -> {
                final StringAnalysis analysis = StringAnalysis.of(s, hashAlgorithm);
                SwingUtilities.invokeLater(() -> {
                    if (generation.get() == current) {
                        apply(analysis);
                    }
                });
            });
        }

        private void apply(@NonNull StringAnalysis a) {
            lengthValue.setText(format(a.length()));
            codePointsValue.setText(format(a.codePoints()));
            bytesValue.setText(format(a.bytes()));
            linesValue.setText(format(a.lines()));
            wordsValue.setText(format(a.words()));

            for (final Map.Entry<Trait, JLabel> entry : chips.entrySet()) {
                final Trait trait = entry.getKey();
                final JLabel chip = entry.getValue();
                final int count = a.count(trait);
                chip.setEnabled(count > 0);
                chip.setText(count > 0 ? trait.text + " " + format(count) : trait.text);
                chip.setIcon(chipIcon(trait.warning && count > 0, count > 0));
            }

            final boolean empty = a.source().isEmpty();
            hashText.setText(empty ? "" : a.hash());
            hashText.setCaretPosition(0);
            copyHash.setEnabled(!empty);

            markersModel.setRows(a.markers());
            markersSummary.setText(describeMarkers(a));
            highlightMarkers(a.markers());
        }

        private void highlightMarkers(@NonNull List<@NonNull MarkerRow> rows) {
            final Highlighter highlighter = sourceText.getHighlighter();
            for (final Object tag : markerHighlights) {
                highlighter.removeHighlight(tag);
            }
            markerHighlights.clear();
            for (final MarkerRow row : rows) {
                addHighlight(row, MarkerPainter.ALL).ifPresent(markerHighlights::add);
            }
            showCurrentMarker();
        }

        /**
         * Emphasizes the marker selected in the table and scrolls source to it.
         */
        private void showCurrentMarker() {
            if (currentHighlight != null) {
                sourceText.getHighlighter().removeHighlight(currentHighlight);
                currentHighlight = null;
            }
            final int index = markersTable.getSelectedRow();
            if (index < 0 || index >= markersModel.getRowCount()) {
                return;
            }
            final MarkerRow row = markersModel.getRow(index);
            currentHighlight = addHighlight(row, MarkerPainter.CURRENT).orElse(null);
            try {
                final Rectangle2D r = sourceText.modelToView2D(row.offset());
                if (r != null) {
                    sourceText.scrollRectToVisible(r.getBounds());
                }
            } catch (BadLocationException ignore) {
                // Text changed after analysis; next run refreshes the table
            }
        }

        private void selectCurrentMarkerInSource() {
            final int index = markersTable.getSelectedRow();
            if (index >= 0 && index < markersModel.getRowCount()) {
                final MarkerRow row = markersModel.getRow(index);
                sourceText.requestFocusInWindow();
                sourceText.select(row.offset(), row.offset() + row.length());
            }
        }

        private @NonNull Optional<@NonNull Object> addHighlight(
                @NonNull MarkerRow row,
                @NonNull MarkerPainter painter
        ) {
            try {
                return Optional.of(
                        sourceText.getHighlighter().addHighlight(row.offset(), row.offset() + row.length(), painter)
                );
            } catch (BadLocationException e) {
                return Optional.empty();
            }
        }

        private static @NonNull String describeMarkers(@NonNull StringAnalysis a) {
            final List<MarkerRow> rows = a.markers();
            if (rows.isEmpty()) {
                return "None found";
            }
            final long removed = rows.stream().filter($ -> $.action() == StringAnalysis.MarkerAction.REMOVED).count();
            final StringBuilder sb = new StringBuilder()
                    .append(format(a.markersTotal())).append(" found: ")
                    .append(format(removed)).append(" removed, ")
                    .append(format(rows.size() - removed)).append(" replaced");
            if (a.markersTotal() > rows.size()) {
                sb.append(" (first ").append(format(rows.size())).append(" listed)");
            }
            return sb.append(". Double-click a row to select it in the text.").toString();
        }

        private static @NonNull Icon chipIcon(boolean warning, boolean present) {
            if (warning) {
                return ToyBoxIcons.mustGet(SilkIcons.ERROR);
            }
            return ToyBoxIcons.mustGet(present ? SilkIcons.BULLET_GREEN : SilkIcons.BULLET_WHITE);
        }

        private static @NonNull String format(long value) {
            return String.format("%,d", value);
        }

        private static @NonNull JLabel createGroupLabel(@NonNull String text) {
            return ToyBoxLabels.create(text, Hints.TEXT_SEMIBOLD);
        }

        private static @NonNull JLabel createTileValue() {
            return ToyBoxLabels.create("0", TEXT_H2);
        }

        private static @NonNull JPanel createTile(@NonNull JLabel value, @NonNull String caption) {
            final JPanel tile = new JPanel(new GridLayout(2, 1));
            tile.add(value);
            tile.add(ToyBoxLabels.create(caption, Hints.TEXT_SMALL));
            return tile;
        }
    }

    /**
     * Table model for AI markers.
     */
    private static final class MarkersTableModel extends AbstractTableModel {
        static final int POSITION = 0;
        static final int CODE = 1;
        static final int NAME = 2;
        static final int ACTION = 3;
        static final int REPLACEMENT = 4;
        private static final @NonNull String @NonNull [] COLUMNS = {"Line:Col", "Code", "Name", "Action", "Replacement"};
        private @NonNull List<@NonNull MarkerRow> rows = List.of();

        void setRows(@NonNull List<@NonNull MarkerRow> rows) {
            this.rows = rows;
            fireTableDataChanged();
        }

        @NonNull MarkerRow getRow(int index) {
            return rows.get(index);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public @NonNull String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public @NonNull Object getValueAt(int rowIndex, int columnIndex) {
            final MarkerRow row = rows.get(rowIndex);
            switch (columnIndex) {
                case POSITION:
                    return row.position();
                case CODE:
                    return row.code();
                case NAME:
                    return row.name();
                case ACTION:
                    return row.action();
                case REPLACEMENT:
                    return row.replacement();
                default:
                    throw new IndexOutOfBoundsException(columnIndex);
            }
        }
    }

    /**
     * Paints markers in the source text; zero-width ones get a minimal visible width.
     */
    private enum MarkerPainter implements Highlighter.HighlightPainter {
        ALL(80, 2),
        CURRENT(255, 3);

        private final int alpha;
        private final int minWidth;

        MarkerPainter(int alpha, int minWidth) {
            this.alpha = alpha;
            this.minWidth = minWidth;
        }

        @Override
        public void paint(@NonNull Graphics g, int p0, int p1, @NonNull Shape bounds, @NonNull JTextComponent c) {
            try {
                final Rectangle2D r0 = c.modelToView2D(p0);
                final Rectangle2D r1 = c.modelToView2D(p1);
                if (r0 == null || r1 == null) {
                    return;
                }
                final Color base = c.getSelectionColor();
                g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
                final Rectangle area = bounds.getBounds();
                final int x0 = (int) r0.getX();
                final int y0 = (int) r0.getY();
                final int h = (int) r0.getHeight();
                if ((int) r0.getY() == (int) r1.getY()) {
                    g.fillRect(x0, y0, Math.max((int) r1.getX() - x0, minWidth), h);
                    return;
                }
                // Multi-line fragment: tail of first line, full middle lines, head of last line
                g.fillRect(x0, y0, area.x + area.width - x0, h);
                final int middle = (int) r1.getY() - (y0 + h);
                if (middle > 0) {
                    g.fillRect(area.x, y0 + h, area.width, middle);
                }
                g.fillRect(area.x, (int) r1.getY(), Math.max((int) r1.getX() - area.x, minWidth), (int) r1.getHeight());
            } catch (BadLocationException ignore) {
                // Stale offsets are replaced on the next analysis
            }
        }
    }

    /**
     * Main method for standalone testing.
     *
     * @param args command-line arguments
     */
    public static void main(@NonNull String @NonNull [] args) {
        final JComponent panel = new Panel(
                s -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null)
        );
        Components.show(panel);
    }
}
