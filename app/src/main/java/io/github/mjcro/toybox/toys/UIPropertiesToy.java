package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;

import javax.swing.*;
import java.util.List;

public class UIPropertiesToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://palette", "Swing UI properties");
    }

    @Override
    public JPanel build(Context context) {
        return new UIPropertiesPanel();
    }
}
