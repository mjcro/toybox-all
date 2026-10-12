package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import org.jspecify.annotations.NonNull;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.Collections;
import java.util.Objects;

/**
 * Builds the "Themes" submenu listing available Look and Feels.
 */
public class ThemeMenuBuilder {
    private final @NonNull ThemeSwitcher themeSwitcher;

    /**
     * Constructs the builder.
     *
     * @param themeSwitcher switcher applying and saving the selected theme
     */
    public ThemeMenuBuilder(@NonNull ThemeSwitcher themeSwitcher) {
        this.themeSwitcher = Objects.requireNonNull(themeSwitcher, "themeSwitcher");
    }

    /**
     * Builds a submenu with one radio item per available Look and Feel;
     * selecting an inactive one applies it to all open windows and saves it to settings.
     *
     * @return Themes submenu.
     */
    public @NonNull JMenu build() {
        JMenu menu = new JMenu("Themes");
        ToyBoxIcons.getSmall(SilkIcons.PALETTE).ifPresent(menu::setIcon);
        ButtonGroup group = new ButtonGroup();
        for (UIManager.LookAndFeelInfo info : ToyBoxLaF.available()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(info.getName());
            item.putClientProperty(ThemeMenuBuilder.class, info.getClassName());
            item.addActionListener(e -> SwingUtilities.invokeLater(() -> themeSwitcher.select(info.getClassName())));
            group.add(item);
            menu.add(item);
        }
        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                syncSelection(group);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        return menu;
    }

    /**
     * Marks the item of the active Look and Feel as selected.
     *
     * @param group Theme items group.
     */
    private static void syncSelection(@NonNull ButtonGroup group) {
        String active = UIManager.getLookAndFeel().getClass().getName();
        group.clearSelection();
        for (AbstractButton item : Collections.list(group.getElements())) {
            if (active.equals(item.getClientProperty(ThemeMenuBuilder.class))) {
                item.setSelected(true);
            }
        }
    }
}
