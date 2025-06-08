package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.settings.storage.SettingsStorageDispatcher;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.widgets.FileChooserInput;
import io.github.mjcro.toybox.swing.widgets.MultiViewTableOrExceptionPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SettingsToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://cog", "Settings");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel(context);
    }

    private static class Panel extends JPanel {
        private final Context context;
        private final MultiViewTableOrExceptionPanel multiView = new MultiViewTableOrExceptionPanel();
        private final JButton
                buttonLoad = ToyBoxButtons.createPrimary("Load settings", this::onLoadClick),
                buttonCreate = ToyBoxButtons.createPrimary("Create settings", this::onCreateClick),
                buttonRefresh = ToyBoxButtons.create("Refresh", this::onRefreshClick);
        private final JPasswordField secretField = new JPasswordField();
        private final FileChooserInput fileChooserInput;

        Panel(Context context) {
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

        private JPanel buildHeader() {
            JPanel inputs = ToyBoxPanels.twoColumnsRight(
                    new AbstractMap.SimpleEntry<>(new JLabel("Settings file"), fileChooserInput),
                    new AbstractMap.SimpleEntry<>(new JLabel("Settings file secret"), secretField)
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

        void onLoadClick(ActionEvent a) {
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

        void onCreateClick(ActionEvent a) {
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

        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            multiView.setEnabled(enabled);
            buttonLoad.setEnabled(enabled && fileChooserInput.getFile().isPresent() && fileChooserInput.getFile().get().exists());
            buttonCreate.setEnabled(enabled && fileChooserInput.getFile().isPresent() && !fileChooserInput.getFile().get().exists());
            buttonRefresh.setEnabled(enabled);
            fileChooserInput.setEnabled(enabled);
            secretField.setEnabled(enabled);
        }

        void onRefreshClick(ActionEvent a) {
            Panel.this.setEnabled(false);
            try {
                Panel.this.refresh();
            } finally {
                Panel.this.setEnabled(true);
            }
        }

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
