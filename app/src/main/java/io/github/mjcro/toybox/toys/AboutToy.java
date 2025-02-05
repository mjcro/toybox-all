package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.factories.LabelsFactory;
import io.github.mjcro.toybox.swing.factories.TextComponentsFactory;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AboutToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU);
    }

    @Override
    public Label getLabel() {
        return new OrderedLabel(
                Integer.MAX_VALUE,
                Label.ofIconAndName("toybox-64", "About")
        );
    }

    @Override
    public Optional<String> getVersion() {
        return Optional.of(Application.VERSION);
    }

    @Override
    public JPanel build(Context context) {
        return new AboutPanel(context.getEnvironment());
    }

    public static class AboutPanel extends JPanel {
        private final Environment environment;

        public AboutPanel(Environment environment) {
            super();

            this.environment = environment;

            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.PAGE_AXIS));

            JLabel mainLabel = LabelsFactory.create("ToyBox", Styles.TEXT_BIGGEST, Styles.PADDING_EXTRA_LARGE);

            top.add(mainLabel);
            top.add(buildShortEnvPanel());


            BorderLayoutMaster.addTopCenter(this, top, buildInstalledToysPanel());
        }

        private JPanel buildInstalledToysPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            Styles.PADDING_NORMAL.apply(panel);

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

            BorderLayoutMaster.addTopCenter(panel, Styles.PADDING_NORMAL.wrap(LabelsFactory.create("Installed toys")), new JScrollPane(table));
            return panel;
        }

        private static JPanel buildShortEnvPanel() {
            JPanel panel = new JPanel(new MigLayout("", "[][grow, fill]"));

            panel.add(new JLabel("ToyBox version"));
            panel.add(TextComponentsFactory.createJTextField(Styles.setReadOnlyText(Application.VERSION)), "wrap");

            panel.add(new JLabel("Java version"));
            panel.add(TextComponentsFactory.createJTextField(Styles.setReadOnlyText(Runtime.version().toString())), "wrap");

            panel.add(new JLabel("Operating system"));
            panel.add(TextComponentsFactory.createJTextField(Styles.setReadOnlyText(System.getProperty("os.name"))), "wrap");

            return panel;
        }
    }
}
