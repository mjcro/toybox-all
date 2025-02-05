package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.api.services.EnvironmentModifier;

import javax.swing.text.JTextComponent;
import java.time.Instant;
import java.util.List;

public class PopupRegistrar implements EnvironmentModifier, Environment.PopupHook {
    @Override
    public void modify(Environment environment) {
        environment.addPopupHook(this);
    }

    @Override
    public List<Labeled> onPopup(Context ctx, Object target) {
        if (target instanceof CharSequence) {
            CharSequence x = (CharSequence) target;
            return List.of(
                    Action.ofName("String analyze", () -> ctx.show(new StringAnalyzerToy(), x)),
                    Action.ofName("String conversion", () -> ctx.show(new StringConverterToy(), x))
            );
        }
        if (target instanceof JTextComponent) {
            JTextComponent x = (JTextComponent) target;
            return List.of(
                    Action.ofName("String analyze", () -> ctx.show(new StringAnalyzerToy(), x.getText())),
                    Action.ofName("String conversion", () -> ctx.show(new StringConverterToy(), x.getText()))
            );
        }
        if (target instanceof Instant) {
            Instant i = (Instant) target;
            return List.of(
                    Action.ofName("Instant analyze", () -> ctx.show(new InstantAnalyzerToy(), i))
            );
        }
        return null;
    }
}
