package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Setting;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.widgets.KeyValueJPanel;
import io.github.mjcro.toybox.swing.widgets.MultiViewBasicPanel;
import io.github.mjcro.toybox.swing.widgets.OrderedKeyValueModel;
import io.github.mjcro.toybox.swing.widgets.panels.ActionBar;
import org.jspecify.annotations.NonNull;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * Inner panel rendering current settings as read-only key-value data.
     */
    private static final class Panel extends JPanel {
        private final @NonNull Context context;
        private final @NonNull SettingsView settingsView = new SettingsView();
        private final @NonNull JButton buttonRefresh = ToyBoxButtons.create("Refresh", this::onRefreshClick);

        /**
         * Constructs the settings panel.
         *
         * @param context the current application context
         */
        Panel(@NonNull Context context) {
            super(new BorderLayout());
            this.context = context;

            add(settingsView, BorderLayout.CENTER);

            final ActionBar actionBar = new ActionBar();
            actionBar.add(buttonRefresh);
            add(actionBar, BorderLayout.PAGE_START);

            refresh();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            settingsView.setEnabled(enabled);
            buttonRefresh.setEnabled(enabled);
        }

        /**
         * Handles the refresh button click by reloading the settings view.
         *
         * @param a the action event
         */
        void onRefreshClick(@NonNull ActionEvent a) {
            refresh();
        }

        /**
         * Prepares a setting value for rendering.
         * Values of sensitive settings are truncated to their first three
         * characters followed by a mask. Non-sensitive values are rendered as is.
         *
         * @param setting the setting to render
         * @return display string for the setting value
         */
        private static @NonNull String render(@NonNull Setting setting) {
            final String value = setting.getDisplayValue();
            if (!setting.isSensitive() || setting.getValue() == null) {
                return value;
            }

            return value.substring(0, Math.min(SENSITIVE_VISIBLE_CHARS, value.length())) + MASK;
        }

        /**
         * Reloads the settings view from the current storage.
         */
        void refresh() {
            setEnabled(false);
            try {
                final ArrayList<@NonNull Setting> settings = new ArrayList<>();
                for (final Setting setting : context.getEnvironment().getSettingsStorage()) {
                    settings.add(setting);
                }
                settings.sort(Comparator.comparing(Setting::getNamespace).thenComparing(Setting::getName));

                settingsView.setViewSettings(toModel(settings));
            } catch (final Exception e) {
                settingsView.setViewException(e);
            } finally {
                setEnabled(true);
            }
        }

        private static @NonNull OrderedKeyValueModel toModel(@NonNull List<@NonNull Setting> settings) {
            final Map<@NonNull String, @NonNull String> values = new LinkedHashMap<>();
            for (final Setting setting : settings) {
                values.put(setting.getName(), render(setting));
            }
            return new OrderedKeyValueModel(values);
        }
    }

    private static final class SettingsView extends MultiViewBasicPanel {
        private static final @NonNull String CARD_SETTINGS = "settings";
        private final @NonNull KeyValueJPanel keyValuePanel = new KeyValueJPanel();

        SettingsView() {
            add(new JScrollPane(keyValuePanel), CARD_SETTINGS);
        }

        void setViewSettings(@NonNull OrderedKeyValueModel model) {
            keyValuePanel.setModel(model);
            setSelectedCard(CARD_SETTINGS);
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            keyValuePanel.setEnabled(enabled);
        }
    }
}
