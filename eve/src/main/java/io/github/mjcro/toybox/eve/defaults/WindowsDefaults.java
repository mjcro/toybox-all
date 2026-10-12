package io.github.mjcro.toybox.eve.defaults;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalFileChooserUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

import io.github.mjcro.toybox.eve.EveBorders;
import io.github.mjcro.toybox.eve.EvePalette;
import io.github.mjcro.toybox.eve.plaf.WindowsFrameBorder;
import io.github.mjcro.toybox.eve.plaf.WindowsIcons;

/**
 * UI defaults of the windows (tabbed panes, internal frames, desktop, root pane, option pane, file and color choosers) component group.
 */
public final class WindowsDefaults {
    private static final int GLYPH = 12;
    private static final int FILE_ICON = 16;
    private static final int MESSAGE_ICON = 32;
    private static final String[] METAL_FILE_CHOOSER_TEXTS = {
            "lookInLabel", "saveInLabel", "fileNameLabel", "folderNameLabel", "filesOfTypeLabel",
            "upFolderToolTip", "upFolder", "homeFolderToolTip", "homeFolder", "newFolderToolTip", "newFolder",
            "newFolderActionLabel", "listViewButtonToolTip", "listViewButton", "listViewActionLabel",
            "detailsViewButtonToolTip", "detailsViewButton", "detailsViewActionLabel", "refreshActionLabel",
            "viewMenuLabel", "fileNameHeader", "fileSizeHeader", "fileTypeHeader", "fileDateHeader", "fileAttrHeader",
    };

    private WindowsDefaults() {
    }

    /**
     * Puts the group-specific defaults into the table.
     * Invoked by {@link io.github.mjcro.toybox.eve.EveLookAndFeel} after the shared defaults,
     * so values put here override them.
     *
     * @param table defaults table being initialized, not null.
     */
    public static void contribute(UIDefaults table) {
        rootPane(table);
        internalFrames(table);
        tabbedPane(table);
        optionPane(table);
        fileChooser(table);
        fileChooserTexts(table);
        colorChooser(table);
    }

    private static void rootPane(UIDefaults table) {
        table.putDefaults(new Object[]{
                "RootPane.frameBorder", new WindowsFrameBorder(EvePalette.ACCENT, 4),
                "RootPane.plainDialogBorder", new WindowsFrameBorder(EvePalette.ACCENT, 4),
                "RootPane.informationDialogBorder", new WindowsFrameBorder(EvePalette.ACCENT, 4),
                "RootPane.questionDialogBorder", new WindowsFrameBorder(EvePalette.ACCENT, 4),
                "RootPane.colorChooserDialogBorder", new WindowsFrameBorder(EvePalette.ACCENT, 4),
                "RootPane.fileChooserDialogBorder", new WindowsFrameBorder(EvePalette.ACCENT, 4),
                "RootPane.warningDialogBorder", new WindowsFrameBorder(EvePalette.WARNING, 4),
                "RootPane.errorDialogBorder", new WindowsFrameBorder(EvePalette.DANGER, 4),
        });
    }

    private static void internalFrames(UIDefaults table) {
        table.putDefaults(new Object[]{
                "InternalFrame.border", new WindowsFrameBorder(EvePalette.ACCENT, 3),
                "InternalFrame.activeTitleBackground", EvePalette.RAISED,
                "InternalFrame.activeTitleForeground", EvePalette.TEXT_BRIGHT,
                "InternalFrame.inactiveTitleBackground", EvePalette.PANEL,
                "InternalFrame.inactiveTitleForeground", EvePalette.TEXT_DIM,
                "InternalFrame.closeIcon", WindowsIcons.close(GLYPH, EvePalette.TEXT),
                "InternalFrame.maximizeIcon", WindowsIcons.maximize(GLYPH, EvePalette.TEXT),
                "InternalFrame.minimizeIcon", WindowsIcons.restore(GLYPH, EvePalette.TEXT),
                "InternalFrame.iconifyIcon", WindowsIcons.minimize(GLYPH, EvePalette.TEXT),
                "DesktopIcon.border", new WindowsFrameBorder(EvePalette.ACCENT, 1),
                "DesktopIcon.width", 170,
                "DesktopIcon.background", EvePalette.PANEL,
                "DesktopIcon.foreground", EvePalette.TEXT,
                "Desktop.background", EvePalette.BACKGROUND,
        });
    }

    private static void tabbedPane(UIDefaults table) {
        table.putDefaults(new Object[]{
                "TabbedPane.background", EvePalette.PANEL,
                "TabbedPane.foreground", EvePalette.TEXT,
                "TabbedPane.selected", EvePalette.PANEL,
                "TabbedPane.contentAreaColor", EvePalette.PANEL,
                "TabbedPane.tabAreaBackground", new ColorUIResource(EvePalette.mix(EvePalette.BACKGROUND, EvePalette.PANEL, 0.4f)),
                "TabbedPane.light", EvePalette.BORDER,
                "TabbedPane.highlight", EvePalette.BORDER,
                "TabbedPane.shadow", EvePalette.BORDER,
                "TabbedPane.darkShadow", EvePalette.BACKGROUND,
                "TabbedPane.focus", EvePalette.ACCENT,
                "TabbedPane.tabInsets", new InsetsUIResource(3, 10, 3, 10),
                "TabbedPane.selectedTabPadInsets", new InsetsUIResource(0, 0, 0, 0),
                "TabbedPane.tabAreaInsets", new InsetsUIResource(0, 0, 0, 0),
                "TabbedPane.contentBorderInsets", new InsetsUIResource(1, 0, 0, 0),
                "TabbedPane.tabRunOverlay", 0,
                "TabbedPane.textIconGap", 4,
                "TabbedPane.labelShift", 0,
                "TabbedPane.selectedLabelShift", 0,
                "TabbedPane.tabsOverlapBorder", Boolean.FALSE,
                "TabbedPane.contentOpaque", Boolean.TRUE,
                "TabbedPane.tabsOpaque", Boolean.FALSE,
        });
    }

    private static void optionPane(UIDefaults table) {
        table.putDefaults(new Object[]{
                "OptionPane.border", EveBorders.empty(10, 12, 8, 12),
                "OptionPane.messageAreaBorder", EveBorders.empty(0, 0, 0, 0),
                "OptionPane.buttonAreaBorder", EveBorders.empty(10, 0, 0, 0),
                "OptionPane.minimumSize", new DimensionUIResource(240, 80),
                "OptionPane.sameSizeButtons", Boolean.TRUE,
                "OptionPane.buttonPadding", 6,
                "OptionPane.errorIcon", WindowsIcons.error(MESSAGE_ICON),
                "OptionPane.informationIcon", WindowsIcons.information(MESSAGE_ICON),
                "OptionPane.questionIcon", WindowsIcons.question(MESSAGE_ICON),
                "OptionPane.warningIcon", WindowsIcons.warning(MESSAGE_ICON),
        });
    }

    private static void fileChooser(UIDefaults table) {
        table.putDefaults(new Object[]{
                "FileChooser.detailsViewIcon", WindowsIcons.detailsView(FILE_ICON, EvePalette.TEXT),
                "FileChooser.homeFolderIcon", WindowsIcons.home(FILE_ICON, EvePalette.TEXT),
                "FileChooser.listViewIcon", WindowsIcons.listView(FILE_ICON, EvePalette.TEXT),
                "FileChooser.newFolderIcon", WindowsIcons.newFolder(FILE_ICON, EvePalette.ACCENT),
                "FileChooser.upFolderIcon", WindowsIcons.upFolder(FILE_ICON, EvePalette.ACCENT),
                "FileView.computerIcon", WindowsIcons.computer(FILE_ICON, EvePalette.TEXT),
                "FileView.directoryIcon", WindowsIcons.folder(FILE_ICON, EvePalette.ACCENT),
                "FileView.fileIcon", WindowsIcons.file(FILE_ICON, EvePalette.TEXT_DIM),
                "FileView.floppyDriveIcon", WindowsIcons.floppy(FILE_ICON, EvePalette.TEXT),
                "FileView.hardDriveIcon", WindowsIcons.hardDrive(FILE_ICON, EvePalette.TEXT),
        });
    }

    /**
     * Copies the localized texts {@link MetalFileChooserUI} needs. The JDK resolves the Metal
     * resource bundle only for tables built by {@link MetalLookAndFeel}, so adding that bundle
     * to the Eve table has no effect.
     */
    private static void fileChooserTexts(UIDefaults table) {
        final UIDefaults metal = new MetalLookAndFeel().getDefaults();
        for (String base : METAL_FILE_CHOOSER_TEXTS) {
            for (String suffix : new String[]{"Text", "Mnemonic", "AccessibleName"}) {
                final String key = "FileChooser." + base + suffix;
                final Object value = metal.get(key);
                if (value != null && table.get(key) == null) {
                    table.put(key, value);
                }
            }
        }
    }

    private static void colorChooser(UIDefaults table) {
        table.putDefaults(new Object[]{
                "ColorChooser.background", EvePalette.PANEL,
                "ColorChooser.foreground", EvePalette.TEXT,
                "ColorChooser.swatchesSwatchSize", new DimensionUIResource(11, 11),
                "ColorChooser.swatchesRecentSwatchSize", new DimensionUIResource(11, 11),
                "ColorChooser.swatchesDefaultRecentColor", EvePalette.RAISED,
        });
    }
}
