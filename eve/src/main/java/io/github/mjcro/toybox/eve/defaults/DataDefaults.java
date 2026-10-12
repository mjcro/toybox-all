package io.github.mjcro.toybox.eve.defaults;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePalette;
import io.github.mjcro.toybox.eve.plaf.DataHeaderCorner;
import io.github.mjcro.toybox.eve.plaf.DataIcons;

/**
 * UI defaults of the data (tables, table headers, trees, lists) component group.
 */
public final class DataDefaults {
    /** Subtle tint of odd table rows, slightly lighter than {@link EvePalette#SURFACE}. */
    private static final ColorUIResource ALTERNATE_ROW = new ColorUIResource(0x1D242D);

    private DataDefaults() {
    }

    /**
     * Puts the group-specific defaults into the table.
     * Invoked by {@link io.github.mjcro.toybox.eve.EveLookAndFeel} after the shared defaults,
     * so values put here override them.
     *
     * @param table defaults table being initialized, not null.
     */
    public static void contribute(UIDefaults table) {
        final Border cellPadding = EveBorders.empty(1, 6, 1, 6);
        final Border cellFocus = focusBorder(EvePalette.ACCENT, 0, 5);
        final Border listPadding = EveBorders.empty(2, 7, 2, 7);
        final Border listFocus = focusBorder(EvePalette.BORDER_BRIGHT, 1, 6);
        table.putDefaults(new Object[]{
                // Table
                "Table.showHorizontalLines", Boolean.TRUE,
                "Table.showVerticalLines", Boolean.FALSE,
                "Table.alternateRowColor", ALTERNATE_ROW,
                "Table.cellNoFocusBorder", cellPadding,
                "Table.focusCellHighlightBorder", cellFocus,
                "Table.focusSelectedCellHighlightBorder", cellFocus,
                "Table.focusCellBackground", EvePalette.SURFACE,
                "Table.focusCellForeground", EvePalette.TEXT_BRIGHT,
                "Table.dropLineColor", EvePalette.ACCENT,
                "Table.dropLineShortColor", EvePalette.ACCENT_DARK,
                "Table.dropCellBackground", EvePalette.SELECTION_INACTIVE,
                "Table.dropCellForeground", EvePalette.TEXT_BRIGHT,
                "Table.scrollPaneBorder", EveBorders.frame(),
                "Table.scrollPaneCornerComponent", (UIDefaults.ActiveValue) t -> new DataHeaderCorner(),
                "Table.ascendingSortIcon", EveIcons.arrow(SwingConstants.NORTH, 9, EvePalette.ACCENT),
                "Table.descendingSortIcon", EveIcons.arrow(SwingConstants.SOUTH, 9, EvePalette.ACCENT),

                // Table header
                "TableHeader.font", EveFonts.header(),
                "TableHeader.background", EvePalette.RAISED,
                "TableHeader.foreground", EvePalette.TEXT_DIM,
                "TableHeader.cellBorder", EveBorders.empty(3, 6, 3, 6),
                "TableHeader.focusCellBackground", EvePalette.RAISED_HOVER,

                // Tree
                "Tree.paintLines", Boolean.FALSE,
                "Tree.hash", EvePalette.GRID,
                "Tree.leftChildIndent", 8,
                "Tree.rightChildIndent", 10,
                "Tree.expandedIcon", EveIcons.arrow(SwingConstants.SOUTH, 10, EvePalette.TEXT_DIM),
                "Tree.collapsedIcon", EveIcons.arrow(SwingConstants.EAST, 10, EvePalette.TEXT_DIM),
                "Tree.openIcon", DataIcons.folder(true),
                "Tree.closedIcon", DataIcons.folder(false),
                "Tree.leafIcon", DataIcons.leaf(),
                "Tree.rendererMargins", new InsetsUIResource(0, 2, 0, 6),
                "Tree.selectionBorderColor", EvePalette.SELECTION,
                "Tree.drawsFocusBorderAroundIcon", Boolean.FALSE,
                "Tree.drawDashedFocusIndicator", Boolean.FALSE,
                "Tree.repaintWholeRow", Boolean.TRUE,
                "Tree.editorBorder", EveBorders.line(EvePalette.ACCENT),
                "Tree.dropLineColor", EvePalette.ACCENT,
                "Tree.dropCellBackground", EvePalette.SELECTION_INACTIVE,
                "Tree.dropCellForeground", EvePalette.TEXT_BRIGHT,

                // List
                "List.cellNoFocusBorder", listPadding,
                "List.focusCellHighlightBorder", listFocus,
                "List.focusSelectedCellHighlightBorder", listFocus,
                "List.dropLineColor", EvePalette.ACCENT,
                "List.dropCellBackground", EvePalette.SELECTION_INACTIVE,
                "List.dropCellForeground", EvePalette.TEXT_BRIGHT,
        });
    }

    /**
     * Builds a 1px line border with inner padding, keeping cell content in place
     * compared to the matching padding-only border.
     */
    private static Border focusBorder(ColorUIResource color, int vertical, int horizontal) {
        return new BorderUIResource.CompoundBorderUIResource(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(vertical, horizontal, vertical, horizontal)
        );
    }
}
