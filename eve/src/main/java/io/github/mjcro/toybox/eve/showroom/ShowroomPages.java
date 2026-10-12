package io.github.mjcro.toybox.eve.showroom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
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
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Builders of the showroom pages, one per component group.
 *
 * <p>Every page is a plain {@link JComponent} that does not require a window, so pages
 * can also be rendered off-screen (headless) for visual checks and tests.
 */
public final class ShowroomPages {
    private static final String[] SHIPS = {
            "Rifter", "Merlin", "Tristan", "Punisher", "Drake", "Hurricane", "Ferox", "Brutix",
            "Raven", "Megathron", "Apocalypse", "Tempest", "Caracal", "Vexor", "Omen", "Stabber",
            "Kestrel", "Atron", "Slasher", "Executioner", "Thorax", "Moa", "Maller", "Rupture",
    };
    private static final String[] CLASSES = {"Frigate", "Cruiser", "Battlecruiser", "Battleship"};
    private static final String[] FACTIONS = {"Minmatar", "Caldari", "Gallente", "Amarr"};
    private static final String CYRILLIC = "Варп-двигатель готов. Щиты: 100%. Ёмкость груза — 450 м³.";

    private ShowroomPages() {
    }

    /**
     * Builds all pages keyed by tab title, in display order.
     *
     * @return non-null ordered map of page title to page component.
     */
    public static Map<String, JComponent> all() {
        final Map<String, JComponent> pages = new LinkedHashMap<>();
        pages.put("Buttons", buttons());
        pages.put("Text", text());
        pages.put("Data", data());
        pages.put("Ranges", ranges());
        pages.put("Containers", containers());
        pages.put("Dialogs", dialogs());
        return pages;
    }

    /**
     * Builds the buttons page: push buttons, toggles, check boxes, radio buttons, labels and separators.
     *
     * @return non-null page.
     */
    public static JComponent buttons() {
        final JButton engage = tip(new JButton("Engage"), "Regular push button");
        final JButton warp = tip(new JButton("Warp to 0"), "Button with icon");
        warp.setIcon(EveIcons.arrow(SwingConstants.EAST, 12, EvePalette.ACCENT));
        final JButton offline = tip(new JButton("Offline"), "Disabled button");
        offline.setEnabled(false);
        final JButton dock = tip(new JButton("Dock"), "Default button");
        whenShown(dock, () -> {
            final JRootPane rootPane = SwingUtilities.getRootPane(dock);
            if (rootPane != null) {
                rootPane.setDefaultButton(dock);
            }
        });

        final ButtonGroup modes = new ButtonGroup();
        final JToggleButton approach = toggle(modes, "Approach", true);
        final JToggleButton orbit = toggle(modes, "Orbit", false);
        final JToggleButton keep = toggle(modes, "Keep at range", false);
        final JToggleButton locked = new JToggleButton("Locked", true);
        locked.setEnabled(false);

        final JCheckBox autoRepeat = tip(new JCheckBox("Auto-repeat", true), "Selected check box");
        final JCheckBox overheat = tip(new JCheckBox("Overheat"), "Unselected check box");
        final JCheckBox safety = new JCheckBox("Safety (disabled)", true);
        safety.setEnabled(false);
        final JCheckBox empty = new JCheckBox("Disabled");
        empty.setEnabled(false);

        final ButtonGroup security = new ButtonGroup();
        final JRadioButton green = radio(security, "Green", true);
        final JRadioButton yellow = radio(security, "Yellow", false);
        final JRadioButton red = radio(security, "Red", false);
        final JRadioButton disabledRadio = new JRadioButton("Disabled", false);
        disabledRadio.setEnabled(false);

        final JLabel accent = new JLabel("Accent label");
        accent.setForeground(EvePalette.ACCENT);
        final JLabel warning = new JLabel("Warning label");
        warning.setForeground(EvePalette.WARNING);
        final JLabel danger = new JLabel("Danger label");
        danger.setForeground(EvePalette.DANGER);
        final JLabel disabledLabel = new JLabel("Disabled label");
        disabledLabel.setEnabled(false);
        final JLabel html = new JLabel("<html><b>HTML</b> label with <i>styles</i> and <u>underline</u></html>");
        final JLabel cyrillic = new JLabel("Кириллица: Порт Жита");
        final JLabel iconLabel = new JLabel("Label with icon", EveIcons.check(12, EvePalette.SUCCESS), SwingConstants.LEADING);

        final JSeparator vertical = new JSeparator(SwingConstants.VERTICAL);
        vertical.setPreferredSize(new Dimension(2, 40));

        return columns(
                stack(
                        section("Push buttons", flow(engage, warp, dock, offline)),
                        section("Toggle buttons", flow(approach, orbit, keep, locked)),
                        section("Check boxes", grid(2, autoRepeat, overheat, safety, empty)),
                        section("Radio buttons", grid(2, green, yellow, red, disabledRadio))
                ),
                stack(
                        section("Labels", grid(1, new JLabel("Regular label"), accent, warning, danger,
                                disabledLabel, html, cyrillic, iconLabel)),
                        section("Separators", flow(new JLabel("Left"), vertical, new JLabel("Right"))),
                        section("Horizontal separator", new JSeparator())
                )
        );
    }

    /**
     * Builds the text page: every text component, including Cyrillic content.
     *
     * @return non-null page.
     */
    public static JComponent text() {
        final JTextField name = tip(new JTextField("Capsuleer"), "JTextField");
        final JTextField readOnly = new JTextField("Read-only value");
        readOnly.setEditable(false);
        final JTextField disabled = new JTextField("Disabled value");
        disabled.setEnabled(false);
        final JFormattedTextField isk = tip(new JFormattedTextField(NumberFormat.getNumberInstance()), "JFormattedTextField");
        isk.setValue(1_250_000_000L);
        final JFormattedTextField date = new JFormattedTextField(DateFormat.getDateInstance());
        date.setValue(new Date());
        final JPasswordField password = tip(new JPasswordField("secret"), "JPasswordField");

        final JTextArea area = new JTextArea(String.join("\n",
                "// JTextArea uses the bundled monospaced font",
                "fleet.warpTo(\"Jita IV - Moon 4\", 0);",
                CYRILLIC,
                "0123456789 ISK +-*/=<>[]{}"
        ), 6, 30);
        final JTextPane pane = new JTextPane();
        fillStyled(pane.getStyledDocument());
        final JEditorPane html = new JEditorPane("text/html",
                "<html><body><h3>Market report</h3><p>Tritanium <b>+4.2%</b>, Pyerite <i>-1.1%</i>.</p>"
                        + "<ul><li>Jita</li><li>Amarr</li></ul></body></html>");
        html.setEditable(false);

        return columns(
                stack(
                        section("Single line", form(
                                "Text field", name,
                                "Read-only", readOnly,
                                "Disabled", disabled,
                                "Number", isk,
                                "Date", date,
                                "Password", password
                        )),
                        section("Text area", new JScrollPane(area))
                ),
                stack(
                        section("Text pane", scroll(pane, 360, 110)),
                        section("Editor pane (HTML)", scroll(html, 360, 130))
                )
        );
    }

    /**
     * Builds the data page: sortable table, tree, list and combo boxes.
     *
     * @return non-null page.
     */
    public static JComponent data() {
        final JTable table = new JTable(new ShipTableModel());
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.setRowSelectionInterval(1, 2);

        final JTree tree = new JTree(universe());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        for (int i = 0; i < 3; i++) {
            tree.expandRow(i);
        }
        tree.setSelectionRow(2);

        final DefaultListModel<String> items = new DefaultListModel<>();
        for (String s : new String[]{"Tritanium", "Pyerite", "Mexallon", "Isogen", "Nocxium",
                "Zydrine", "Megacyte", "Морфит", "Кристаллы"}) {
            items.addElement(s);
        }
        final JList<String> list = new JList<>(items);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setSelectedIndices(new int[]{1, 3});

        final JComboBox<String> combo = tip(new JComboBox<>(FACTIONS), "Non-editable combo box");
        final JComboBox<String> editable = new JComboBox<>(SHIPS);
        editable.setEditable(true);
        final JComboBox<String> disabled = new JComboBox<>(CLASSES);
        disabled.setEnabled(false);

        final JPanel side = stack(
                section("Combo boxes", form("Faction", combo, "Ship (editable)", editable, "Disabled", disabled)),
                section("List", scroll(list, 200, 160))
        );
        final JSplitPane top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll(tree, 200, 200), new JScrollPane(table));
        top.setResizeWeight(0.25);
        final JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, top, side);
        main.setResizeWeight(0.75);
        return padded(main);
    }

    /**
     * Builds the ranges page: sliders, spinners, progress bars and scroll bars.
     *
     * @return non-null page.
     */
    public static JComponent ranges() {
        final JSlider shields = new JSlider(0, 100, 65);
        shields.setMajorTickSpacing(25);
        shields.setMinorTickSpacing(5);
        shields.setPaintTicks(true);
        shields.setPaintLabels(true);
        final JSlider plain = new JSlider(0, 100, 30);
        final JSlider disabledSlider = new JSlider(0, 100, 50);
        disabledSlider.setEnabled(false);
        final JSlider vertical = new JSlider(SwingConstants.VERTICAL, 0, 10, 7);
        vertical.setMajorTickSpacing(5);
        vertical.setMinorTickSpacing(1);
        vertical.setPaintTicks(true);
        vertical.setPreferredSize(new Dimension(50, 150));

        final JSpinner number = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
        final JSpinner date = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        date.setEditor(new JSpinner.DateEditor(date, "yyyy-MM-dd"));
        final JSpinner list = new JSpinner(new SpinnerListModel(CLASSES));
        final JSpinner disabledSpinner = new JSpinner(new SpinnerNumberModel(1.5, 0.0, 10.0, 0.1));
        disabledSpinner.setEnabled(false);

        final JProgressBar armor = new JProgressBar(0, 100);
        armor.setValue(72);
        armor.setStringPainted(true);
        final JProgressBar capacitor = new JProgressBar(0, 100);
        capacitor.setValue(35);
        final JProgressBar scanning = new JProgressBar();
        scanning.setIndeterminate(true);
        scanning.setString("Scanning...");
        scanning.setStringPainted(true);
        final JProgressBar verticalBar = new JProgressBar(SwingConstants.VERTICAL, 0, 100);
        verticalBar.setValue(60);
        verticalBar.setPreferredSize(new Dimension(14, 150));

        final JScrollBar hBar = new JScrollBar(JScrollBar.HORIZONTAL, 30, 20, 0, 100);
        final JScrollBar vBar = new JScrollBar(JScrollBar.VERTICAL, 10, 30, 0, 100);
        vBar.setPreferredSize(new Dimension(vBar.getPreferredSize().width, 150));
        final JTextArea big = new JTextArea(30, 80);
        for (int i = 1; i <= 30; i++) {
            big.append("Line " + i + ": " + "scroll me horizontally and vertically ".repeat(4) + "\n");
        }

        return columns(
                stack(
                        section("Sliders", grid(1, shields, plain, disabledSlider)),
                        section("Spinners", form("Number", number, "Date", date, "List", list, "Disabled", disabledSpinner)),
                        section("Vertical", flow(vertical, verticalBar, vBar))
                ),
                stack(
                        section("Progress bars", grid(1, armor, capacitor, scanning)),
                        section("Scroll bar", hBar),
                        section("Scroll pane", scroll(big, 360, 180))
                )
        );
    }

    /**
     * Builds the containers page: split panes, nested tabbed panes and a desktop with internal frames.
     *
     * @return non-null page.
     */
    public static JComponent containers() {
        final JTabbedPane left = new JTabbedPane(SwingConstants.LEFT);
        left.addTab("Overview", new JLabel("  Left placed tabs"));
        left.addTab("Fitting", new JLabel("  Fitting"));
        left.addTab("Cargo", new JLabel("  Cargo"));
        left.setEnabledAt(2, false);

        final JTabbedPane scrolling = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        for (String s : new String[]{"Local", "Corp", "Alliance", "Fleet", "Help", "Rookie", "Trade", "Recruit"}) {
            scrolling.addTab(s, new JLabel("  " + s + " channel"));
        }
        scrolling.setToolTipTextAt(0, "Scroll tab layout");

        final JDesktopPane desktop = new JDesktopPane();
        desktop.setPreferredSize(new Dimension(480, 300));
        final JInternalFrame inventory = internal("Inventory", 16, 16);
        desktop.add(inventory);
        desktop.add(internal("Market", 260, 80));
        final JInternalFrame notepad = internal("Notepad", 60, 200);
        desktop.add(notepad);
        // Selection and iconification only take effect once the desktop is showing and sized.
        whenShown(desktop, () -> {
            try {
                notepad.setIcon(true);
                inventory.setSelected(true);
            } catch (PropertyVetoException ignored) {
                // Showroom nicety only.
            }
        });

        final JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, left, scrolling);
        vertical.setOneTouchExpandable(true);
        vertical.setResizeWeight(0.5);
        final JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vertical, desktop);
        main.setOneTouchExpandable(true);
        main.setContinuousLayout(true);
        main.setResizeWeight(0.4);
        return padded(main);
    }

    /**
     * Builds the dialogs page: buttons opening standard dialogs and inline option pane, color and file choosers.
     *
     * @return non-null page.
     */
    public static JComponent dialogs() {
        final JButton info = new JButton("Information");
        info.addActionListener(e -> JOptionPane.showMessageDialog(owner(e), "Docking request accepted.",
                "Station", JOptionPane.INFORMATION_MESSAGE));
        final JButton warn = new JButton("Warning");
        warn.addActionListener(e -> JOptionPane.showMessageDialog(owner(e), "You are entering low-security space.",
                "Warning", JOptionPane.WARNING_MESSAGE));
        final JButton error = new JButton("Error");
        error.addActionListener(e -> JOptionPane.showMessageDialog(owner(e), "Warp drive disrupted!",
                "Error", JOptionPane.ERROR_MESSAGE));
        final JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e -> JOptionPane.showConfirmDialog(owner(e), "Self-destruct the ship?",
                "Confirm", JOptionPane.YES_NO_CANCEL_OPTION));
        final JButton input = new JButton("Input");
        input.addActionListener(e -> JOptionPane.showInputDialog(owner(e), "Bookmark name:", "Safe spot 1"));
        final JButton choice = new JButton("Choice");
        choice.addActionListener(e -> JOptionPane.showInputDialog(owner(e), "Select destination:", "Autopilot",
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Jita", "Amarr", "Dodixie", "Rens"}, "Jita"));
        final JButton file = new JButton("File chooser");
        file.addActionListener(e -> new JFileChooser().showOpenDialog(owner(e)));
        final JButton color = new JButton("Color chooser");
        color.addActionListener(e -> JColorChooser.showDialog(owner(e), "Overview color", EvePalette.ACCENT));
        final JButton custom = new JButton("Custom dialog");
        custom.addActionListener(e -> showCustomDialog(owner(e)));

        final JTabbedPane inline = new JTabbedPane();
        inline.addTab("Option pane", flow(new JOptionPane("Jump to the next system?",
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION)));
        inline.addTab("Color chooser", new JColorChooser(EvePalette.ACCENT));
        inline.addTab("File chooser", new JFileChooser());

        final JPanel buttons = stack(section("Dialogs",
                grid(1, info, warn, error, confirm, input, choice, file, color, custom)));
        final JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        root.add(buttons, BorderLayout.WEST);
        root.add(inline, BorderLayout.CENTER);
        return root;
    }

    private static void showCustomDialog(Component owner) {
        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(owner), "Fleet invitation");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final JButton accept = new JButton("Accept");
        accept.addActionListener(e -> dialog.dispose());
        final JButton decline = new JButton("Decline");
        decline.addActionListener(e -> dialog.dispose());
        final JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(new JLabel("Commander invites you to join the fleet."), BorderLayout.CENTER);
        content.add(flow(accept, decline), BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(accept);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static Component owner(ActionEvent e) {
        return (Component) e.getSource();
    }

    private static JInternalFrame internal(String title, int x, int y) {
        final JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
        final JList<String> content = new JList<>(new String[]{title + " item 1", title + " item 2", title + " item 3"});
        frame.add(new JScrollPane(content));
        frame.setBounds(x, y, 220, 150);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Runs the action once, on the next EDT cycle after the component becomes showing.
     */
    private static void whenShown(JComponent component, Runnable action) {
        component.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && component.isShowing()) {
                    component.removeHierarchyListener(this);
                    SwingUtilities.invokeLater(action);
                }
            }
        });
    }

    private static DefaultMutableTreeNode universe() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("New Eden");
        final String[][] regions = {
                {"The Forge", "Kimotoro", "Jita", "Perimeter", "New Caldari"},
                {"Domain", "Throne Worlds", "Amarr", "Sarum Prime", "Ashab"},
                {"Sinq Laison", "Coriault", "Dodixie", "Botane", "Ourapheh"},
                {"Heimatar", "Hed", "Rens", "Frarn", "Lustrevik"},
        };
        for (String[] region : regions) {
            final DefaultMutableTreeNode r = new DefaultMutableTreeNode(region[0]);
            final DefaultMutableTreeNode constellation = new DefaultMutableTreeNode(region[1]);
            for (int i = 2; i < region.length; i++) {
                constellation.add(new DefaultMutableTreeNode(region[i]));
            }
            r.add(constellation);
            root.add(r);
        }
        return root;
    }

    private static void fillStyled(StyledDocument doc) {
        final SimpleAttributeSet regular = new SimpleAttributeSet();
        final SimpleAttributeSet bold = new SimpleAttributeSet();
        StyleConstants.setBold(bold, true);
        final SimpleAttributeSet accent = new SimpleAttributeSet();
        StyleConstants.setForeground(accent, EvePalette.ACCENT);
        final SimpleAttributeSet warning = new SimpleAttributeSet();
        StyleConstants.setForeground(warning, EvePalette.WARNING);
        StyleConstants.setItalic(warning, true);
        try {
            doc.insertString(doc.getLength(), "JTextPane ", bold);
            doc.insertString(doc.getLength(), "with styled runs: ", regular);
            doc.insertString(doc.getLength(), "accent", accent);
            doc.insertString(doc.getLength(), ", ", regular);
            doc.insertString(doc.getLength(), "warning italic", warning);
            doc.insertString(doc.getLength(), ".\n" + CYRILLIC + "\n", regular);
            doc.insertString(doc.getLength(), "Date: " + LocalDate.of(2026, 10, 12), regular);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T extends JComponent> T tip(T component, String tooltip) {
        component.setToolTipText(tooltip);
        return component;
    }

    private static JToggleButton toggle(ButtonGroup group, String text, boolean selected) {
        final JToggleButton button = new JToggleButton(text, selected);
        button.setToolTipText("Toggle button in a group");
        group.add(button);
        return button;
    }

    private static JRadioButton radio(ButtonGroup group, String text, boolean selected) {
        final JRadioButton button = new JRadioButton(text, selected);
        button.setToolTipText("Radio button");
        group.add(button);
        return button;
    }

    private static JScrollPane scroll(JComponent view, int width, int height) {
        final JScrollPane pane = new JScrollPane(view);
        pane.setPreferredSize(new Dimension(width, height));
        return pane;
    }

    private static JComponent padded(JComponent content) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel section(String title, JComponent content) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title.toUpperCase()),
                BorderFactory.createEmptyBorder(2, 4, 4, 4)
        ));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel flow(JComponent... children) {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 2));
        for (JComponent child : children) {
            panel.add(child);
        }
        return panel;
    }

    private static JPanel grid(int cols, JComponent... children) {
        final JPanel panel = new JPanel(new GridLayout(0, cols, 4, 2));
        for (JComponent child : children) {
            panel.add(child);
        }
        return panel;
    }

    /**
     * Two-column form: label, component, label, component...
     */
    private static JPanel form(Object... labelsAndComponents) {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 4);
        for (int i = 0; i < labelsAndComponents.length; i += 2) {
            c.gridy = i / 2;
            c.gridx = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.LINE_START;
            panel.add(new JLabel((String) labelsAndComponents[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            panel.add((Component) labelsAndComponents[i + 1], c);
        }
        return panel;
    }

    /**
     * Vertical stack of sections aligned to the top.
     */
    private static JPanel stack(JComponent... sections) {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(0, 0, 6, 0);
        for (JComponent section : sections) {
            panel.add(section, c);
        }
        c.weighty = 1;
        panel.add(new JPanel(), c);
        return panel;
    }

    private static JComponent columns(JComponent... columns) {
        final JPanel panel = new JPanel(new GridLayout(1, columns.length, 8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        for (JComponent column : columns) {
            panel.add(column);
        }
        return panel;
    }

    /**
     * Read-only table model with generated ship data.
     */
    private static final class ShipTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Ship", "Class", "Faction", "Security", "Distance, km", "Online"};

        @Override
        public int getRowCount() {
            return SHIPS.length;
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return switch (column) {
                case 3 -> Double.class;
                case 4 -> Integer.class;
                case 5 -> Boolean.class;
                default -> String.class;
            };
        }

        @Override
        public Object getValueAt(int row, int column) {
            return switch (column) {
                case 0 -> SHIPS[row];
                case 1 -> CLASSES[row % CLASSES.length];
                case 2 -> FACTIONS[(row / 2) % FACTIONS.length];
                case 3 -> Math.round((1.0 - (row * 0.07) % 1.1) * 10) / 10.0;
                case 4 -> (row * 7919) % 25000 + 150;
                default -> row % 3 != 0;
            };
        }
    }
}
