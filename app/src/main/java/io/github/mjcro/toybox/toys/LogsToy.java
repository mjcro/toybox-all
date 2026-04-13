package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.LogBuffer;
import io.github.mjcro.toybox.app.swing.LogsJPanel;
import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;
import java.util.List;

/**
 * Toy that displays the application log buffer in a scrollable panel.
 */
public class LogsToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://folder_bug", "Application logs");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new LogsJPanel(LogBuffer.Instance::getAll);
    }
}
