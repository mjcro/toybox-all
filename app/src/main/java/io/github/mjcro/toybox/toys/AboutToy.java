package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Toy that displays application information, version details, and a list
 * of all installed toys.
 */
public class AboutToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return new OrderedLabel(
                Integer.MAX_VALUE,
                Label.ofIconAndName("toybox-64", "About")
        );
    }

    @Override
    public @NonNull Optional<@NonNull String> getVersion() {
        return Optional.of(Application.INTERNAL_VERSION);
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new AboutPanel(context.getEnvironment());
    }

    /**
     * Panel displaying the About information including version and installed toys table.
     */
    public static class AboutPanel extends JPanel {
        private final @NonNull Environment environment;

        /**
         * Constructs the about panel.
         *
         * @param environment the application environment to query for installed toys
         */
        public AboutPanel(@NonNull Environment environment) {
            super();

            this.environment = environment;

            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.PAGE_AXIS));

            JLabel mainLabel = ToyBoxLabels.create("ToyBox", Hints.TEXT_BIGGEST, Hints.PADDING_EXTRA_LARGE);

            top.add(mainLabel);
            top.add(buildShortEnvPanel());


            BorderLayoutMaster.addTopCenter(this, top, buildInstalledToysPanel());
        }

        /**
         * Builds the panel listing all installed toys in a table.
         *
         * @return a panel containing the installed toys table
         */
        private @NonNull JPanel buildInstalledToysPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            Hints.PADDING_NORMAL.apply(panel);

            JTable table = new JTable();
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Toy");
            model.addColumn("Class");
            model.addColumn("Version");

            ArrayList<Toy> toys = new ArrayList<>(environment.getRegisteredToys());
            toys.sort(Comparator.comparing(a -> a.getLabel().getName()));

            for (Toy toy : toys) {
                model.addRow(new Object[]{
                        toy.getLabel().getName(),
                        toy.getClass().getName(),
                        toy.getVersion().orElse(null)
                });
            }

            table.setModel(model);
            table.setDefaultEditor(Object.class, null);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(200);
            columnModel.getColumn(1).setPreferredWidth(500);
            columnModel.getColumn(2).setPreferredWidth(70);

            BorderLayoutMaster.addTopCenter(panel, Hints.PADDING_NORMAL.wrap(ToyBoxLabels.create("Installed toys")), new JScrollPane(table));
            return panel;
        }

        /**
         * Builds a panel showing application and system version information.
         *
         * @return a panel with version details
         */
        private static @NonNull JPanel buildShortEnvPanel() {
            return ToyBoxPanels.twoColumnsRight(
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Application version"),
                            ToyBoxTextComponents.createTextField(Hints.setReadOnlyText(Application.VERSION))
                    ),
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("ToyBox toolkit version"),
                            ToyBoxTextComponents.createTextField(Hints.setReadOnlyText(Application.INTERNAL_VERSION))
                    ),
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Java version"),
                            ToyBoxTextComponents.createTextField(Hints.setReadOnlyText(Runtime.version().toString()))
                    ),
                    new AbstractMap.SimpleEntry<>(
                            ToyBoxLabels.create("Operating system"),
                            ToyBoxTextComponents.createTextField(Hints.setReadOnlyText(System.getProperty("os.name")))
                    )
            );
        }
    }
}
