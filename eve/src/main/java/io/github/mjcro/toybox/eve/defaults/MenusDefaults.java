package io.github.mjcro.toybox.eve.defaults;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;

import io.github.mjcro.toybox.eve.EveFonts;
import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePalette;
import io.github.mjcro.toybox.eve.plaf.MenusBorders;
import io.github.mjcro.toybox.eve.plaf.MenusIcons;

/**
 * UI defaults of the menus (menu bars, menus, menu items, popup menus, tool bars) component group.
 */
public final class MenusDefaults {
    private static final String[] ITEM_PREFIXES = {"MenuItem", "CheckBoxMenuItem", "RadioButtonMenuItem", "Menu"};

    private MenusDefaults() {
    }

    /**
     * Puts the group-specific defaults into the table.
     * Invoked by {@link io.github.mjcro.toybox.eve.EveLookAndFeel} after the shared defaults,
     * so values put here override them.
     *
     * @param table defaults table being initialized, not null.
     */
    public static void contribute(UIDefaults table) {
        final FontUIResource small = EveFonts.small();
        final Icon blankCheck = MenusIcons.blank(MenusIcons.MARK_SIZE, MenusIcons.MARK_SIZE);
        final Icon blankArrow = MenusIcons.blank(10, 10);
        for (String prefix : ITEM_PREFIXES) {
            table.putDefaults(new Object[]{
                    prefix + ".background", EvePalette.PANEL,
                    prefix + ".foreground", EvePalette.TEXT,
                    prefix + ".selectionBackground", EvePalette.SELECTION,
                    prefix + ".selectionForeground", EvePalette.TEXT_BRIGHT,
                    prefix + ".disabledForeground", EvePalette.TEXT_DIM,
                    prefix + ".acceleratorFont", small,
                    prefix + ".acceleratorForeground", EvePalette.TEXT_DIM,
                    prefix + ".acceleratorSelectionForeground", EvePalette.TEXT,
                    prefix + ".margin", new InsetsUIResource(3, 6, 3, 6),
                    prefix + ".borderPainted", Boolean.FALSE,
                    prefix + ".arrowIcon", blankArrow,
                    prefix + ".checkIcon", blankCheck,
            });
        }
        table.putDefaults(new Object[]{
                "CheckBoxMenuItem.checkIcon", MenusIcons.checkMark(),
                "RadioButtonMenuItem.checkIcon", MenusIcons.radioMark(),
                "Menu.arrowIcon", EveIcons.arrow(SwingConstants.EAST, 10, EvePalette.TEXT),
                "Menu.submenuPopupOffsetX", -1,
                "Menu.submenuPopupOffsetY", -3,
                "Menu.menuPopupOffsetY", 0,

                "MenuBar.background", EvePalette.PANEL,
                "MenuBar.foreground", EvePalette.TEXT,
                "MenuBar.border", MenusBorders.menuBar(),

                "PopupMenu.background", EvePalette.PANEL,
                "PopupMenu.foreground", EvePalette.TEXT,
                "PopupMenu.border", MenusBorders.popup(),
                "PopupMenuSeparator.foreground", EvePalette.BORDER,
                "PopupMenuSeparator.background", EvePalette.PANEL,

                "ToolBar.background", EvePalette.PANEL,
                "ToolBar.foreground", EvePalette.TEXT,
                "ToolBar.border", MenusBorders.toolBar(),
                "ToolBar.isRollover", Boolean.TRUE,
                "ToolBar.dockingBackground", EvePalette.SELECTION,
                "ToolBar.dockingForeground", EvePalette.ACCENT,
                "ToolBar.floatingBackground", EvePalette.PANEL,
                "ToolBar.floatingForeground", EvePalette.BORDER_BRIGHT,
                "ToolBarSeparator.foreground", EvePalette.BORDER,
        });
    }
}
