package io.github.mjcro.toybox.swing.linking;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JCheckBox;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Data link binding a {@link JCheckBox} component to a {@link Boolean} value.
 */
public class JCheckBoxBooleanDataLink implements ComponentDataLink<JCheckBox, Boolean> {
    private final @NonNull JCheckBox component;
    private final @Nullable Consumer<@NonNull Optional<@NonNull Boolean>> onSubmit;

    /**
     * Creates a new data link for the given check box.
     *
     * @param component The check box component to bind.
     * @param onSubmit  Optional callback invoked on submit with the current value.
     */
    public JCheckBoxBooleanDataLink(
            @NonNull JCheckBox component,
            @Nullable Consumer<@NonNull Optional<@NonNull Boolean>> onSubmit
    ) {
        this.component = Objects.requireNonNull(component, "component");
        this.onSubmit = onSubmit;
    }

    @Override
    public @NonNull JCheckBox getComponent() {
        return component;
    }

    @Override
    public void setValue(@Nullable Boolean value) {
        getComponent().setSelected(value != null && value);
    }

    @Override
    public @NonNull Optional<@NonNull Boolean> getValue() {
        return Optional.of(component.isSelected());
    }

    @Override
    public void submit() {
        if (onSubmit != null) {
            onSubmit.accept(getValue());
        }
    }
}
