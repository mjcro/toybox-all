package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.LogBuffer;
import io.github.mjcro.toybox.app.swing.LogsJPanel;

import javax.swing.*;
import java.util.List;

public class LogsToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://table_error", "Application logs");
    }

    @Override
    public JPanel build(Context context) {
        return new LogsJPanel(LogBuffer.Instance::getAll);
    }
}
