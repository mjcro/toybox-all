package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.widgets.MultiViewTableOrExceptionPanel;
import io.github.mjcro.toybox.swing.widgets.panels.ActionBar;
import org.jspecify.annotations.NonNull;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Toy for viewing current application settings values.
 *
 * <p>This toy is read-only - settings file binding and creation are performed
 * using the settings file section of the application status bar.
 */
public class SettingsToy implements Toy {
    /** Amount of leading value characters shown for sensitive settings. */
    private static final int SENSITIVE_VISIBLE_CHARS = 3;

    /** Mask appended to truncated values of sensitive settings. */
    private static final @NonNull String MASK = "***";

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
     * Inner panel rendering current settings as a read-only table.
     */
    private static class Panel extends JPanel {
        private final @NonNull Context context;
        private final @NonNull MultiViewTableOrExceptionPanel multiView = new MultiViewTableOrExceptionPanel();
        private final @NonNull JButton buttonRefresh = ToyBoxButtons.create("Refresh", this::onRefreshClick);

        /**
         * Constructs the settings panel.
         *
         * @param context the current application context
         */
        Panel(@NonNull Context context) {
            super(new BorderLayout());
            this.context = context;

            multiView.getTable().setDefaultEditor(Object.class, null);
            add(multiView, BorderLayout.CENTER);

            ActionBar actionBar = new ActionBar();
            actionBar.add(buttonRefresh);
            add(actionBar, BorderLayout.PAGE_START);

            refresh();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            multiView.setEnabled(enabled);
            buttonRefresh.setEnabled(enabled);
        }

        /**
         * Handles the refresh button click by reloading the settings table.
         *
         * @param a the action event
         */
        void onRefreshClick(@NonNull ActionEvent a) {
            refresh();
        }

        /**
         * Prepares setting value for rendering in the table.
         * Values of sensitive settings are truncated to their first three
         * characters followed by a mask. Non-sensitive values are rendered as is.
         *
         * @param setting the setting to render
         * @return display string for the value column
         */
        private static @NonNull String render(@NonNull Setting setting) {
            String value = setting.getDisplayValue();
            if (!setting.isSensitive() || setting.getValue() == null) {
                return value;
            }

            return value.substring(0, Math.min(SENSITIVE_VISIBLE_CHARS, value.length())) + MASK;
        }

        /**
         * Reloads the settings table from the current storage.
         */
        void refresh() {
            setEnabled(false);
            try {
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
                            render(setting)
                    });
                }

                multiView.setViewTable(model);
            } catch (Exception e) {
                multiView.setViewException(e);
            } finally {
                setEnabled(true);
            }
        }
    }
}
