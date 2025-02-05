package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.factories.LabelsFactory;

import javax.swing.*;
import java.util.List;

public class Settings implements Toy {
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
        JPanel panel = new JPanel();
        panel.add(LabelsFactory.create("Under construction"));
        return panel;
    }
}
