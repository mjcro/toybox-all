package io.github.mjcro.toybox.eve;

import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.jspecify.annotations.Nullable;

import io.github.mjcro.toybox.eve.defaults.ButtonsDefaults;
import io.github.mjcro.toybox.eve.defaults.CompatibilityDefaults;
import io.github.mjcro.toybox.eve.defaults.DataDefaults;
import io.github.mjcro.toybox.eve.defaults.MenusDefaults;
import io.github.mjcro.toybox.eve.defaults.RangesDefaults;
import io.github.mjcro.toybox.eve.defaults.TextDefaults;
import io.github.mjcro.toybox.eve.defaults.WindowsDefaults;

/**
 * Eve: dense, dark Swing Look and Feel inspired by the EVE Online "Photon" UI.
 *
 * <p>Self-contained: every standard Swing component gets its own UI delegate from
 * {@code io.github.mjcro.toybox.eve.plaf}, fonts ({@link EveFonts}) are bundled,
 * all icons are vector-painted. Supports Look and Feel window decorations, enable
 * them with {@link javax.swing.JFrame#setDefaultLookAndFeelDecorated(boolean)}
 * before creating windows.
 *
 * <p>Usage:
 * <pre>{@code
 * EveLookAndFeel.install();
 * }</pre>
 */
public class EveLookAndFeel extends BasicLookAndFeel {
    /** Look and Feel name and identifier. */
    public static final String NAME = "Eve";

    private static final String PLAF_PACKAGE = "io.github.mjcro.toybox.eve.plaf.";
    private static final List<String> UI_CLASS_IDS = List.of(
            "ButtonUI", "ToggleButtonUI", "CheckBoxUI", "RadioButtonUI", "LabelUI", "PanelUI",
            "SeparatorUI", "ToolTipUI",
            "TextFieldUI", "FormattedTextFieldUI", "PasswordFieldUI", "TextAreaUI", "TextPaneUI",
            "EditorPaneUI", "ComboBoxUI", "SpinnerUI",
            "SliderUI", "ProgressBarUI", "ScrollBarUI", "ScrollPaneUI", "ViewportUI", "SplitPaneUI",
            "TableUI", "TableHeaderUI", "TreeUI", "ListUI",
            "MenuBarUI", "MenuUI", "MenuItemUI", "CheckBoxMenuItemUI", "RadioButtonMenuItemUI",
            "PopupMenuUI", "PopupMenuSeparatorUI", "ToolBarUI", "ToolBarSeparatorUI",
            "TabbedPaneUI", "InternalFrameUI", "DesktopPaneUI", "DesktopIconUI", "RootPaneUI",
            "OptionPaneUI", "FileChooserUI", "ColorChooserUI"
    );

    /**
     * Creates the Look and Feel; prefer {@link #install()} to activate it.
     */
    public EveLookAndFeel() {
    }

    /**
     * Installs Eve as the current Look and Feel.
     * MUST be called on the Event Dispatch Thread or before any Swing component is created.
     *
     * @throws IllegalStateException if Swing rejects the Look and Feel.
     */
    public static void install() {
        try {
            UIManager.setLookAndFeel(new EveLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            throw new IllegalStateException("Unable to install Eve Look and Feel", e);
        }
    }

    /**
     * Returns the UI class IDs (like {@code "ButtonUI"}) this Look and Feel provides delegates for.
     *
     * @return non-null unmodifiable list.
     */
    public static List<String> uiClassIds() {
        return UI_CLASS_IDS;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getID() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Dense dark Look and Feel inspired by EVE Online UI";
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }

    @Override
    public void initialize() {
        super.initialize();
        EveColorAdapter.install();
    }

    @Override
    public void uninitialize() {
        EveColorAdapter.uninstall();
        super.uninitialize();
    }

    @Override
    public UIDefaults getDefaults() {
        EveFonts.register();
        return super.getDefaults();
    }

    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        final List<Object> pairs = new ArrayList<>(UI_CLASS_IDS.size() * 2);
        for (String id : UI_CLASS_IDS) {
            pairs.add(id);
            pairs.add(PLAF_PACKAGE + "Eve" + id);
        }
        table.putDefaults(pairs.toArray());
    }

    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        final Object[] colors = {
                "desktop", EvePalette.BACKGROUND,
                "activeCaption", EvePalette.RAISED,
                "activeCaptionText", EvePalette.TEXT_BRIGHT,
                "activeCaptionBorder", EvePalette.BORDER_BRIGHT,
                "inactiveCaption", EvePalette.PANEL,
                "inactiveCaptionText", EvePalette.TEXT_DIM,
                "inactiveCaptionBorder", EvePalette.BORDER,
                "window", EvePalette.SURFACE,
                "windowBorder", EvePalette.BORDER,
                "windowText", EvePalette.TEXT,
                "menu", EvePalette.PANEL,
                "menuText", EvePalette.TEXT,
                "text", EvePalette.SURFACE,
                "textText", EvePalette.TEXT,
                "textHighlight", EvePalette.SELECTION,
                "textHighlightText", EvePalette.TEXT_BRIGHT,
                "textInactiveText", EvePalette.TEXT_DIM,
                "control", EvePalette.PANEL,
                "controlText", EvePalette.TEXT,
                "controlHighlight", EvePalette.BORDER,
                "controlLtHighlight", EvePalette.BORDER_BRIGHT,
                "controlShadow", EvePalette.BORDER,
                "controlDkShadow", EvePalette.BACKGROUND,
                "scrollbar", EvePalette.PANEL,
                "info", EvePalette.RAISED,
                "infoText", EvePalette.TEXT,
        };
        table.putDefaults(colors);
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        initTextRendering(table);
        initFonts(table);
        initSharedDefaults(table);
        ButtonsDefaults.contribute(table);
        TextDefaults.contribute(table);
        RangesDefaults.contribute(table);
        DataDefaults.contribute(table);
        MenusDefaults.contribute(table);
        WindowsDefaults.contribute(table);
        CompatibilityDefaults.contribute(table);
    }

    /**
     * Enables text antialiasing, honoring desktop font hints when the platform provides them.
     */
    private static void initTextRendering(UIDefaults table) {
        Object aa = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        Object contrast = 140;
        final @Nullable Object hints = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (hints instanceof Map<?, ?> map) {
            final @Nullable Object desktopAa = map.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            if (desktopAa != null && desktopAa != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
                    && desktopAa != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
                aa = desktopAa;
            }
            final @Nullable Object desktopContrast = map.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
            if (desktopContrast != null) {
                contrast = desktopContrast;
            }
        }
        table.put(RenderingHints.KEY_TEXT_ANTIALIASING, aa);
        table.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, contrast);
    }

    /**
     * Replaces every {@code *.font} default with the bundled fonts.
     */
    private static void initFonts(UIDefaults table) {
        final FontUIResource regular = EveFonts.regular();
        for (Object key : new ArrayList<>(table.keySet())) {
            if (key instanceof String s && s.endsWith(".font")) {
                table.put(s, regular);
            }
        }
        final FontUIResource small = EveFonts.small();
        final FontUIResource header = EveFonts.header();
        final FontUIResource mono = EveFonts.mono();
        table.putDefaults(new Object[]{
                "TitledBorder.font", header,
                "TableHeader.font", header,
                "ToolTip.font", small,
                "InternalFrame.titleFont", header,
                "TextArea.font", mono,
        });
    }

    /**
     * Baseline colors, borders and metrics shared by all component groups.
     * Group hooks in {@code io.github.mjcro.toybox.eve.defaults} refine them.
     */
    private static void initSharedDefaults(UIDefaults table) {
        table.putDefaults(new Object[]{
                // Containers
                "Panel.background", EvePalette.PANEL,
                "Panel.foreground", EvePalette.TEXT,
                "Viewport.background", EvePalette.SURFACE,
                "Viewport.foreground", EvePalette.TEXT,
                "ScrollPane.background", EvePalette.PANEL,
                "ScrollPane.border", EveBorders.frame(),
                "TitledBorder.border", EveBorders.frame(),
                "TitledBorder.titleColor", EvePalette.ACCENT,
                "Label.foreground", EvePalette.TEXT,
                "Label.disabledForeground", EvePalette.TEXT_DIM,
                "Separator.foreground", EvePalette.BORDER,
                "Separator.background", EvePalette.PANEL,

                // Buttons
                "Button.background", EvePalette.RAISED,
                "Button.foreground", EvePalette.TEXT_BRIGHT,
                "Button.border", EveBorders.button(),
                "Button.margin", new InsetsUIResource(0, 0, 0, 0),
                "ToggleButton.background", EvePalette.RAISED,
                "ToggleButton.foreground", EvePalette.TEXT_BRIGHT,
                "ToggleButton.border", EveBorders.button(),
                "ToggleButton.margin", new InsetsUIResource(0, 0, 0, 0),
                "CheckBox.background", EvePalette.PANEL,
                "CheckBox.foreground", EvePalette.TEXT,
                "RadioButton.background", EvePalette.PANEL,
                "RadioButton.foreground", EvePalette.TEXT,

                // Text
                "TextField.background", EvePalette.SURFACE,
                "TextField.inactiveBackground", EvePalette.PANEL,
                "TextField.border", EveBorders.field(),
                "TextField.caretForeground", EvePalette.ACCENT,
                "FormattedTextField.background", EvePalette.SURFACE,
                "FormattedTextField.inactiveBackground", EvePalette.PANEL,
                "FormattedTextField.border", EveBorders.field(),
                "FormattedTextField.caretForeground", EvePalette.ACCENT,
                "PasswordField.background", EvePalette.SURFACE,
                "PasswordField.inactiveBackground", EvePalette.PANEL,
                "PasswordField.border", EveBorders.field(),
                "PasswordField.caretForeground", EvePalette.ACCENT,
                "TextArea.background", EvePalette.SURFACE,
                "TextArea.border", EveBorders.textArea(),
                "TextArea.caretForeground", EvePalette.ACCENT,
                "TextPane.background", EvePalette.SURFACE,
                "TextPane.border", EveBorders.textArea(),
                "TextPane.caretForeground", EvePalette.ACCENT,
                "EditorPane.background", EvePalette.SURFACE,
                "EditorPane.border", EveBorders.textArea(),
                "EditorPane.caretForeground", EvePalette.ACCENT,

                // Data
                "List.background", EvePalette.SURFACE,
                "List.foreground", EvePalette.TEXT,
                "List.selectionBackground", EvePalette.SELECTION,
                "List.selectionForeground", EvePalette.TEXT_BRIGHT,
                "Table.background", EvePalette.SURFACE,
                "Table.foreground", EvePalette.TEXT,
                "Table.selectionBackground", EvePalette.SELECTION,
                "Table.selectionForeground", EvePalette.TEXT_BRIGHT,
                "Table.gridColor", EvePalette.GRID,
                "Table.rowHeight", 20,
                "TableHeader.background", EvePalette.RAISED,
                "TableHeader.foreground", EvePalette.TEXT_DIM,
                "Tree.background", EvePalette.SURFACE,
                "Tree.foreground", EvePalette.TEXT,
                "Tree.textBackground", EvePalette.SURFACE,
                "Tree.textForeground", EvePalette.TEXT,
                "Tree.selectionBackground", EvePalette.SELECTION,
                "Tree.selectionForeground", EvePalette.TEXT_BRIGHT,
                "Tree.selectionBorderColor", EvePalette.ACCENT,
                "Tree.rowHeight", 20,

                // Popups and menus
                "PopupMenu.background", EvePalette.PANEL,
                "PopupMenu.border", EveBorders.popup(),
                "ToolTip.background", EvePalette.RAISED,
                "ToolTip.foreground", EvePalette.TEXT_BRIGHT,
                "ToolTip.border", EveBorders.popup(),
                "MenuItem.selectionBackground", EvePalette.SELECTION,
                "MenuItem.selectionForeground", EvePalette.TEXT_BRIGHT,
                "Menu.selectionBackground", EvePalette.SELECTION,
                "Menu.selectionForeground", EvePalette.TEXT_BRIGHT,

                // Ranges
                "ProgressBar.foreground", EvePalette.ACCENT,
                "ProgressBar.background", EvePalette.SURFACE,
                "ProgressBar.selectionForeground", EvePalette.BACKGROUND,
                "ProgressBar.selectionBackground", EvePalette.TEXT,
                "ScrollBar.width", 8,
                "SplitPane.dividerSize", 5,

                // Windows
                "Desktop.background", EvePalette.BACKGROUND,
                "OptionPane.background", EvePalette.PANEL,
                "OptionPane.messageForeground", EvePalette.TEXT,
        });
    }
}
