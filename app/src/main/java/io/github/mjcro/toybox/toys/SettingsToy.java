package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.settings.storage.SettingsStorageDispatcher;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.widgets.FileChooserInput;
import io.github.mjcro.toybox.swing.widgets.MultiViewTableOrExceptionPanel;
import org.jspecify.annotations.NonNull;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Toy for viewing and managing application settings, including binding
 * or creating encrypted settings files.
 */
public class SettingsToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://cog", "Settings");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel(context);
    }

    /**
     * Inner panel providing the settings management UI with file binding,
     * creation, and a table view of current settings.
     */
    private static class Panel extends JPanel {
        private final @NonNull Context context;
        private final @NonNull MultiViewTableOrExceptionPanel multiView = new MultiViewTableOrExceptionPanel();
        private final @NonNull JButton
                buttonLoad = ToyBoxButtons.createPrimary("Load settings", this::onLoadClick),
                buttonCreate = ToyBoxButtons.createPrimary("Create settings", this::onCreateClick),
                buttonRefresh = ToyBoxButtons.create("Refresh", this::onRefreshClick);
        private final @NonNull JPasswordField secretField = new JPasswordField();
        private final @NonNull FileChooserInput fileChooserInput;

        /**
         * Constructs the settings panel.
         *
         * @param context the current application context
         */
        Panel(@NonNull Context context) {
            super(new BorderLayout());
            this.context = context;
            this.fileChooserInput = new FileChooserInput(
                    context.getEnvironment(),
                    "-",
                    () -> setEnabled(true),
                    new FileNameExtensionFilter("DAT files", "dat")
            );

            secretField.getDocument().addUndoableEditListener(e -> setEnabled(true));

            multiView.getTable().setDefaultEditor(Object.class, null);
            add(multiView, BorderLayout.CENTER);

            add(buildHeader(), BorderLayout.PAGE_START);

            refresh();
        }

        /**
         * Builds the header panel containing file selection, password input, and action buttons.
         *
         * @return the header panel
         */
        private @NonNull JPanel buildHeader() {
            JPanel inputs = ToyBoxPanels.twoColumnsRight(
                    new AbstractMap.SimpleEntry<>(ToyBoxLabels.create("Settings file"), fileChooserInput),
                    new AbstractMap.SimpleEntry<>(ToyBoxLabels.create("Settings file secret"), secretField)
            );

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttons.add(buttonCreate);
            buttons.add(buttonLoad);
            buttons.add(buttonRefresh);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(inputs, BorderLayout.CENTER);
            panel.add(buttons, BorderLayout.PAGE_END);

            return ToyBoxPanels.titledBordered("Application settings configuration", panel);
        }

        /**
         * Handles the load button click, binding an existing settings file.
         *
         * @param a the action event
         */
        void onLoadClick(@NonNull ActionEvent a) {
            SettingsStorage storage = context.getEnvironment().getSettingsStorage();
            setEnabled(false);
            try {
                if (!(storage instanceof SettingsStorageDispatcher)) {
                    throw new RuntimeException("Unsupported session storage type");
                }
                SettingsStorageDispatcher sdd = (SettingsStorageDispatcher) storage;
                sdd.bindExistingSettingsFile(fileChooserInput.getFile().get(), new String(secretField.getPassword()));
                refresh();
            } catch (Exception e) {
                multiView.setViewException(e);
            } finally {
                setEnabled(true);
            }
        }

        /**
         * Handles the create button click, creating a new settings file.
         *
         * @param a the action event
         */
        void onCreateClick(@NonNull ActionEvent a) {
            SettingsStorage storage = context.getEnvironment().getSettingsStorage();
            setEnabled(false);
            try {
                if (!(storage instanceof SettingsStorageDispatcher)) {
                    throw new RuntimeException("Unsupported session storage type");
                }
                SettingsStorageDispatcher sdd = (SettingsStorageDispatcher) storage;
                sdd.createNewSettingFile(fileChooserInput.getFile().get(), new String(secretField.getPassword()));
                refresh();
            } catch (Exception e) {
                multiView.setViewException(e);
            } finally {
                setEnabled(true);
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            multiView.setEnabled(enabled);
            buttonLoad.setEnabled(enabled && fileChooserInput.getFile().isPresent() && fileChooserInput.getFile().get().exists());
            buttonCreate.setEnabled(enabled && fileChooserInput.getFile().isPresent() && !fileChooserInput.getFile().get().exists());
            buttonRefresh.setEnabled(enabled);
            fileChooserInput.setEnabled(enabled);
            secretField.setEnabled(enabled);
        }

        /**
         * Handles the refresh button click by reloading the settings table.
         *
         * @param a the action event
         */
        void onRefreshClick(@NonNull ActionEvent a) {
            Panel.this.setEnabled(false);
            try {
                Panel.this.refresh();
            } finally {
                Panel.this.setEnabled(true);
            }
        }

        /**
         * Reloads the settings table from the current storage.
         */
        void refresh() {
            setEnabled(false);
            try {
                // Sorting toys
                ArrayList<Setting> settings = new ArrayList<>();
                for (Setting setting : context.getEnvironment().getSettingsStorage()) {
                    settings.add(setting);
                }
                settings.sort(Comparator.comparing(Setting::getNamespace).thenComparing(Setting::getName));

                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Namespace");
                model.addColumn("Name");
                model.addColumn("Value");

                for (Setting setting : settings) {
                    model.addRow(new Object[]{
                            setting.getNamespace(),
                            setting.getName(),
                            setting.getDisplayValue()
                    });
                }

                multiView.setViewTable(model);
            } finally {
                setEnabled(true);
            }
        }
    }
}
