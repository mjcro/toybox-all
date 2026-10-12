package io.github.mjcro.toybox.eve;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.mjcro.toybox.eve.showroom.ShowroomPages;

class EveLookAndFeelTest {
    @BeforeAll
    static void install() throws Exception {
        Snapshots.onEdt(() -> {
            EveLookAndFeel.install();
            return Boolean.TRUE;
        });
    }

    @Test
    void mapsEveryBasicUiClassId() {
        final UIDefaults basic = new BasicLookAndFeel() {
            @Override
            public String getName() {
                return "probe";
            }

            @Override
            public String getID() {
                return "probe";
            }

            @Override
            public String getDescription() {
                return "probe";
            }

            @Override
            public boolean isNativeLookAndFeel() {
                return false;
            }

            @Override
            public boolean isSupportedLookAndFeel() {
                return true;
            }
        }.getDefaults();
        final UIDefaults eve = UIManager.getLookAndFeelDefaults();
        int checked = 0;
        for (Map.Entry<Object, Object> e : basic.entrySet()) {
            if (e.getKey() instanceof String key && key.endsWith("UI")
                    && e.getValue() instanceof String value && value.startsWith("javax.swing.plaf.basic.")) {
                Assertions.assertEquals("io.github.mjcro.toybox.eve.plaf.Eve" + key, eve.get(key), key);
                checked++;
            }
        }
        Assertions.assertEquals(EveLookAndFeel.uiClassIds().size(), checked);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "TitlePane.small.closeIcon", "Actions.Red", "Actions.Yellow", "Actions.Green", "Actions.Blue",
            "Component.linkColor", "monospaced.font", "Table.selectionInactiveForeground",
    })
    void providesCompatibilityKeys(String key) {
        Assertions.assertNotNull(UIManager.get(key), key);
    }

    static Stream<Arguments> components() {
        return Stream.of(
                of("Button", () -> new JButton("Button")),
                of("ToggleButton", () -> new JToggleButton("Toggle", true)),
                of("CheckBox", () -> new JCheckBox("Check", true)),
                of("RadioButton", () -> new JRadioButton("Radio", true)),
                of("Label", () -> new JLabel("Label")),
                of("Panel", JPanel::new),
                of("Separator", JSeparator::new),
                of("ToolTip", () -> {
                    final JToolTip tip = new JToolTip();
                    tip.setTipText("Tooltip");
                    return tip;
                }),
                of("TextField", () -> new JTextField("Text")),
                of("FormattedTextField", () -> new JFormattedTextField(42)),
                of("PasswordField", () -> new JPasswordField("secret")),
                of("TextArea", () -> new JTextArea("Text\nArea")),
                of("TextPane", JTextPane::new),
                of("EditorPane", () -> new JEditorPane("text/html", "<b>html</b>")),
                of("ComboBox", () -> new JComboBox<>(new String[]{"One", "Two"})),
                of("Spinner", JSpinner::new),
                of("Slider", JSlider::new),
                of("ProgressBar", () -> new JProgressBar(0, 100)),
                of("ScrollBar", JScrollBar::new),
                of("ScrollPane", () -> new JScrollPane(new JList<>(new String[]{"a", "b"}))),
                of("Viewport", JViewport::new),
                of("SplitPane", () -> new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JLabel("L"), new JLabel("R"))),
                of("Table", () -> new JTable(3, 3)),
                of("TableHeader", () -> new JTable(3, 3).getTableHeader()),
                of("Tree", JTree::new),
                of("List", () -> new JList<>(new String[]{"a", "b"})),
                of("MenuBar", () -> {
                    final JMenuBar bar = new JMenuBar();
                    bar.add(new JMenu("Menu"));
                    return bar;
                }),
                of("Menu", () -> new JMenu("Menu")),
                of("MenuItem", () -> new JMenuItem("Item")),
                of("CheckBoxMenuItem", () -> new JCheckBoxMenuItem("Check", true)),
                of("RadioButtonMenuItem", () -> new JRadioButtonMenuItem("Radio", true)),
                of("PopupMenu", () -> {
                    final JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem("Item"));
                    return popup;
                }),
                of("PopupMenuSeparator", JPopupMenu.Separator::new),
                of("ToolBar", () -> {
                    final JToolBar bar = new JToolBar();
                    bar.add(new JButton("Tool"));
                    return bar;
                }),
                of("ToolBarSeparator", JToolBar.Separator::new),
                of("TabbedPane", () -> {
                    final JTabbedPane tabs = new JTabbedPane();
                    tabs.addTab("One", new JLabel("1"));
                    tabs.addTab("Two", new JLabel("2"));
                    return tabs;
                }),
                of("InternalFrame", () -> {
                    final JInternalFrame frame = new JInternalFrame("Frame", true, true, true, true);
                    frame.setVisible(true);
                    return frame;
                }),
                of("DesktopPane", JDesktopPane::new),
                of("DesktopIcon", () -> new JInternalFrame.JDesktopIcon(new JInternalFrame("Icon"))),
                of("RootPane", JRootPane::new),
                of("OptionPane", () -> new JOptionPane("Message")),
                of("FileChooser", JFileChooser::new),
                of("ColorChooser", JColorChooser::new)
        );
    }

    @Test
    void componentListCoversEveryDelegate() {
        Assertions.assertEquals(EveLookAndFeel.uiClassIds().size(), components().count());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("components")
    void installsEveDelegateAndPaints(String name, Supplier<JComponent> factory) throws Exception {
        Snapshots.onEdt(() -> {
            final JComponent c = factory.get();
            final Object ui = c.getClass().getMethod("getUI").invoke(c);
            Assertions.assertEquals("Eve" + name + "UI", ui.getClass().getSimpleName());
            final Dimension size = c.getPreferredSize();
            final BufferedImage image = Snapshots.render(c, Math.max(40, size.width), Math.max(20, size.height));
            Assertions.assertNotNull(image);
            return Boolean.TRUE;
        });
    }

    @Test
    void showroomPagesPaint() throws Exception {
        Snapshots.onEdt(() -> {
            for (Map.Entry<String, JComponent> page : ShowroomPages.all().entrySet()) {
                Assertions.assertNotNull(Snapshots.render(page.getValue(), 1100, 660), page.getKey());
            }
            return Boolean.TRUE;
        });
    }

    private static Arguments of(String name, Supplier<JComponent> factory) {
        return Arguments.of(name, factory);
    }
}
