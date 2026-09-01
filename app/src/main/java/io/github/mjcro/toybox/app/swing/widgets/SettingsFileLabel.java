package io.github.mjcro.toybox.app.swing.widgets;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.app.settings.storage.SettingsStorageDispatcher;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Status bar section displaying the settings file currently in use.
 *
 * <p>A click (either button) opens a dynamically built popup menu enumerating
 * settings files found in the current working directory and in the user home
 * folder, allowing to bind one of them, to create a new one or to fall back
 * to in-memory settings.
 */
public class SettingsFileLabel extends JLabel {
    private static final @NonNull Logger log = LoggerFactory.getLogger(SettingsFileLabel.class);

    /** File name extension used by ToyBox settings files. */
    public static final @NonNull String EXTENSION = "tbs";

    private static final @NonNull String DOT_EXTENSION = "." + EXTENSION;

    private final @NonNull Environment environment;
    private @Nullable JPopupMenu visiblePopup = null;

    /**
     * Constructs the settings file status label.
     *
     * @param environment the application environment
     */
    public SettingsFileLabel(@NonNull Environment environment) {
        this.environment = Objects.requireNonNull(environment, "environment");

        Hints.CENTER.apply(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Popup trigger is platform dependent - it may arrive on press or on release
                if (e.isPopupTrigger()) {
                    showPopup(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    showPopup(e.getX(), e.getY());
                }
            }
        });

        refresh();
    }

    /**
     * Updates the label text, icon and tooltip to match current settings storage state.
     */
    public void refresh() {
        Optional<File> file = getDispatcher().flatMap(SettingsStorageDispatcher::getFile);
        if (file.isPresent()) {
            ToyBoxIcons.get("lightbulb").ifPresent(this::setIcon);
            setText(file.get().getName());
            setToolTipText("Settings file " + file.get().getAbsolutePath());
        } else {
            ToyBoxIcons.get("lightbulb_off").ifPresent(this::setIcon);
            setText("mem");
            setToolTipText("Settings are kept in memory only and will be lost on exit");
        }
        revalidate();
        repaint();
    }

    /**
     * Returns the settings storage dispatcher, if application uses one.
     *
     * @return dispatcher, if available
     */
    private @NonNull Optional<@NonNull SettingsStorageDispatcher> getDispatcher() {
        SettingsStorage storage = environment.getSettingsStorage();
        return storage instanceof SettingsStorageDispatcher
                ? Optional.of((SettingsStorageDispatcher) storage)
                : Optional.empty();
    }

    /**
     * Builds and displays the settings files popup menu.
     *
     * @param x horizontal position within this label
     * @param y vertical position within this label
     */
    private void showPopup(int x, int y) {
        Optional<SettingsStorageDispatcher> dispatcher = getDispatcher();
        if (dispatcher.isEmpty() || (visiblePopup != null && visiblePopup.isVisible())) {
            return;
        }

        JPopupMenu popup = new JPopupMenu();
        visiblePopup = popup;

        for (Map.Entry<File, List<File>> entry : listSettingsFiles().entrySet()) {
            JMenuItem header = new JMenuItem(entry.getKey().getAbsolutePath());
            ToyBoxIcons.getSmall("folder").ifPresent(header::setIcon);
            header.setEnabled(false);
            popup.add(header);

            if (entry.getValue().isEmpty()) {
                JMenuItem empty = new JMenuItem("No " + DOT_EXTENSION + " files here");
                empty.setEnabled(false);
                popup.add(empty);
            } else {
                for (File file : entry.getValue()) {
                    JMenuItem item = new JMenuItem(file.getName());
                    ToyBoxIcons.getSmall("page_white_key").ifPresent(item::setIcon);
                    item.setToolTipText(file.getAbsolutePath());
                    item.addActionListener($ -> onSettingsFileChosen(file));
                    popup.add(item);
                }
            }
            popup.addSeparator();
        }

        JMenuItem create = new JMenuItem("Create new settings file...");
        ToyBoxIcons.getSmall("add").ifPresent(create::setIcon);
        create.addActionListener($ -> onCreateNewClick());
        popup.add(create);

        if (dispatcher.get().isFileStorageEnabled()) {
            JMenuItem detach = new JMenuItem("Detach, use in-memory settings");
            ToyBoxIcons.getSmall("disconnect").ifPresent(detach::setIcon);
            detach.addActionListener($ -> onDetachClick());
            popup.add(detach);
        }

        popup.show(this, x, y);
    }

    /**
     * Enumerates settings files located in the current working directory
     * and in the user home folder.
     *
     * @return map of directory to settings files found in it, both preserving lookup order
     */
    private @NonNull Map<@NonNull File, @NonNull List<@NonNull File>> listSettingsFiles() {
        LinkedHashMap<File, List<File>> result = new LinkedHashMap<>();
        for (String property : new String[]{"user.dir", "user.home"}) {
            String path = System.getProperty(property);
            if (path == null) {
                continue;
            }

            File directory = new File(path).getAbsoluteFile();
            if (result.containsKey(directory) || !directory.isDirectory()) {
                continue;
            }

            File[] files = directory.listFiles(
                    (dir, name) -> name.toLowerCase().endsWith(DOT_EXTENSION) && new File(dir, name).isFile()
            );

            ArrayList<File> found = files == null
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(files));
            found.sort(Comparator.comparing(File::getName));
            result.put(directory, found);
        }

        return result;
    }

    /**
     * Handles settings file selection - asks for password and binds chosen file.
     *
     * @param file the settings file to bind
     */
    private void onSettingsFileChosen(@NonNull File file) {
        Optional<SettingsStorageDispatcher> dispatcher = getDispatcher();
        if (dispatcher.isEmpty()) {
            return;
        }

        Optional<String> password = askPassword("Password for " + file.getName(), false);
        if (password.isEmpty()) {
            return;
        }

        try {
            dispatcher.get().bindExistingSettingsFile(file, password.get());
            log.info(Slf4jUtil.TOYBOX_MARKER, "Bound settings file {}", file.getAbsolutePath());
        } catch (Exception e) {
            log.error(Slf4jUtil.TOYBOX_MARKER, "Error binding settings file {}", file.getAbsolutePath(), e);
            showError("Unable to open " + file.getName(), e);
        } finally {
            refresh();
        }
    }

    /**
     * Handles new settings file creation - asks for target file and password.
     */
    private void onCreateNewClick() {
        Optional<SettingsStorageDispatcher> dispatcher = getDispatcher();
        if (dispatcher.isEmpty()) {
            return;
        }

        environment.chooseFileToSave(new Environment.FileCallback() {
            @Override
            public void onFileChosen(@NonNull File file) {
                File target = file.getName().toLowerCase().endsWith(DOT_EXTENSION)
                        ? file
                        : new File(file.getParentFile(), file.getName() + DOT_EXTENSION);

                Optional<String> password = askPassword("Password for new file " + target.getName(), true);
                if (password.isEmpty()) {
                    return;
                }

                try {
                    dispatcher.get().createNewSettingFile(target, password.get());
                    log.info(Slf4jUtil.TOYBOX_MARKER, "Created settings file {}", target.getAbsolutePath());
                } catch (Exception e) {
                    log.error(Slf4jUtil.TOYBOX_MARKER, "Error creating settings file {}", target.getAbsolutePath(), e);
                    showError("Unable to create " + target.getName(), e);
                } finally {
                    refresh();
                }
            }

            @Override
            public void onNoFileChosen() {
                // Nothing to do
            }
        }, new File(new File(System.getProperty("user.dir", ".")), "toybox" + DOT_EXTENSION));
    }

    /**
     * Handles detach request, reverting storage back to in-memory one.
     */
    private void onDetachClick() {
        getDispatcher().ifPresent(SettingsStorageDispatcher::close);
        refresh();
    }

    /**
     * Displays modal password input dialog.
     *
     * @param title   dialog title
     * @param confirm whether password confirmation field should be displayed
     * @return entered password, or empty if dialog was cancelled
     */
    private @NonNull Optional<@NonNull String> askPassword(@NonNull String title, boolean confirm) {
        JPasswordField password = new JPasswordField(20);
        JPasswordField repeat = new JPasswordField(20);

        JPanel panel = new JPanel(new GridLayout(confirm ? 4 : 2, 1, 0, 2));
        panel.add(new JLabel("Settings file secret", SwingConstants.LEADING));
        panel.add(password);
        if (confirm) {
            panel.add(new JLabel("Repeat secret", SwingConstants.LEADING));
            panel.add(repeat);
        }

        // Placing focus on password field once dialog becomes visible
        password.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                SwingUtilities.invokeLater(password::requestFocusInWindow);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        int result = JOptionPane.showConfirmDialog(
                getDialogParent(),
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return Optional.empty();
        }

        String value = new String(password.getPassword());
        if (confirm && !value.equals(new String(repeat.getPassword()))) {
            JOptionPane.showMessageDialog(
                    getDialogParent(),
                    "Entered secrets do not match",
                    "Settings file",
                    JOptionPane.ERROR_MESSAGE
            );
            return askPassword(title, true);
        }

        return Optional.of(value);
    }

    /**
     * Displays modal error dialog.
     *
     * @param message human-readable message
     * @param e       error occurred
     */
    private void showError(@NonNull String message, @NonNull Throwable e) {
        String reason = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        JOptionPane.showMessageDialog(
                getDialogParent(),
                message + "\n" + reason,
                "Settings file",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Returns component to be used as modal dialogs parent.
     *
     * @return dialog parent component, may be {@code null}
     */
    private @Nullable Component getDialogParent() {
        return SwingUtilities.getWindowAncestor(this);
    }
}
