package io.github.mjcro.toybox.eve.showroom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import io.github.mjcro.toybox.eve.EveIcons;
import io.github.mjcro.toybox.eve.EveLookAndFeel;
import io.github.mjcro.toybox.eve.EvePalette;

/**
 * Runnable showroom demonstrating every standard Swing component under the Eve Look and Feel.
 *
 * <p>Run with {@code java -jar eve.jar} or by launching {@link #main(String[])}.
 * The window uses Look and Feel decorations; the "Enabled" check box in the status
 * bar toggles the enabled state of the active page to show disabled rendering.
 */
public final class EveShowroom {
    private static final String ORIGINALLY_ENABLED = "eve.showroom.originallyEnabled";

    private EveShowroom() {
    }

    /**
     * Installs the Look and Feel and opens the showroom window.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EveLookAndFeel.install();
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            final JFrame frame = new JFrame("Eve Look and Feel showroom");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setJMenuBar(createMenuBar());
            frame.setContentPane(createContent());
            frame.setSize(new Dimension(1120, 740));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Builds the showroom menu bar containing every menu item type.
     *
     * @return non-null menu bar.
     */
    public static JMenuBar createMenuBar() {
        final JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(item("New fitting", KeyEvent.VK_N));
        file.add(item("Open...", KeyEvent.VK_O));
        final JMenuItem save = item("Save", KeyEvent.VK_S);
        save.setEnabled(false);
        file.add(save);
        file.addSeparator();
        final JMenu recent = new JMenu("Recent");
        recent.add(new JMenuItem("Jita trade route"));
        recent.add(new JMenuItem("Amarr patrol"));
        recent.add(new JMenuItem("Маршрут Додикси"));
        file.add(recent);
        file.addSeparator();
        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);

        final JMenu view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        view.add(new JCheckBoxMenuItem("Show toolbar", true));
        view.add(new JCheckBoxMenuItem("Show status bar", true));
        final JCheckBoxMenuItem locked = new JCheckBoxMenuItem("Lock windows", true);
        locked.setEnabled(false);
        view.add(locked);
        view.addSeparator();
        final ButtonGroup density = new ButtonGroup();
        for (String name : new String[]{"Compact", "Regular", "Comfortable"}) {
            final JRadioButtonMenuItem radio = new JRadioButtonMenuItem(name, "Compact".equals(name));
            density.add(radio);
            view.add(radio);
        }

        final JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        final JMenuItem about = new JMenuItem("About Eve");
        about.addActionListener(e -> JOptionPane.showMessageDialog((Component) e.getSource(),
                "Eve Look and Feel\nDense dark Swing theme inspired by EVE Online.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);

        final JMenuBar bar = new JMenuBar();
        bar.add(file);
        bar.add(view);
        bar.add(help);
        return bar;
    }

    /**
     * Builds the window content: toolbar, page tabs and status bar.
     *
     * @return non-null content panel.
     */
    public static JComponent createContent() {
        final JTabbedPane tabs = new JTabbedPane();
        for (Map.Entry<String, JComponent> page : ShowroomPages.all().entrySet()) {
            tabs.addTab(page.getKey(), page.getValue());
        }

        final JPanel root = new JPanel(new BorderLayout());
        root.add(createToolBar(), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(createStatusBar(tabs), BorderLayout.SOUTH);
        return root;
    }

    private static JToolBar createToolBar() {
        final JToolBar toolbar = new JToolBar("Tools");
        final JButton undock = new JButton("Undock");
        undock.setToolTipText("Toolbar button");
        toolbar.add(undock);
        final JButton warp = new JButton(EveIcons.arrow(SwingConstants.EAST, 14, EvePalette.ACCENT));
        warp.setToolTipText("Icon-only toolbar button");
        toolbar.add(warp);
        toolbar.addSeparator();
        final JToggleButton autopilot = new JToggleButton("Autopilot");
        autopilot.setToolTipText("Toolbar toggle");
        toolbar.add(autopilot);
        toolbar.addSeparator();
        toolbar.add(new JLabel("Speed "));
        final JComboBox<String> speed = new JComboBox<>(new String[]{"Full", "Half", "Stop"});
        speed.setMaximumSize(speed.getPreferredSize());
        toolbar.add(speed);
        return toolbar;
    }

    private static JComponent createStatusBar(JTabbedPane tabs) {
        final JLabel location = new JLabel("Jita IV - Moon 4 - Caldari Navy Assembly Plant");
        location.setForeground(EvePalette.TEXT_DIM);
        final JProgressBar load = new JProgressBar(0, 100);
        load.setValue(42);
        load.setPreferredSize(new Dimension(120, 10));
        final JCheckBox enabled = new JCheckBox("Enabled", true);
        enabled.setToolTipText("Toggle enabled state of the current page");
        enabled.addActionListener(e -> {
            final Component page = tabs.getSelectedComponent();
            if (page != null) {
                setEnabledDeep(page, enabled.isSelected());
            }
        });

        final JPanel right = new JPanel(new BorderLayout(8, 0));
        right.add(load, BorderLayout.WEST);
        right.add(enabled, BorderLayout.EAST);
        final JPanel status = new JPanel(new BorderLayout(8, 0));
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, EvePalette.BORDER),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        status.add(location, BorderLayout.CENTER);
        status.add(right, BorderLayout.EAST);
        return status;
    }

    private static JMenuItem item(String text, int key) {
        final JMenuItem item = new JMenuItem(text);
        item.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        return item;
    }

    /**
     * Recursively toggles the enabled state; components disabled on purpose stay disabled when re-enabling.
     */
    private static void setEnabledDeep(Component component, boolean enabled) {
        if (component instanceof JComponent jc) {
            if (!enabled) {
                jc.putClientProperty(ORIGINALLY_ENABLED, jc.isEnabled());
                jc.setEnabled(false);
            } else {
                jc.setEnabled(!Boolean.FALSE.equals(jc.getClientProperty(ORIGINALLY_ENABLED)));
            }
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                setEnabledDeep(child, enabled);
            }
        }
    }
}
